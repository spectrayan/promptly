# ═══════════════════════════════════════════════════════════════════════
# Promptly — Dev Stop Script (PowerShell)
# Kills processes running on frontend (4200) and backend (8080) ports,
# including their entire process trees (cmd → mvn → java, etc.).
# ═══════════════════════════════════════════════════════════════════════

$ErrorActionPreference = "SilentlyContinue"

function Log($msg)  { Write-Host "[promptly] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[promptly] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[promptly] $msg" -ForegroundColor Yellow }

function Stop-ProcessTree {
    param([int]$ProcessId, [string]$Label)
    $output = taskkill /T /F /PID $ProcessId 2>&1 | Out-String
    if ($output -match "SUCCESS") {
        Ok "$Label - killed process tree (root PID $ProcessId)."
    }
}

function Kill-ByPort {
    param([int]$Port, [string]$Label)

    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique

    if (-not $pids) {
        Warn "$Label - nothing running on port $Port."
        return
    }

    foreach ($portPid in $pids) {
        $proc = Get-Process -Id $portPid -ErrorAction SilentlyContinue
        $procName = if ($proc) { $proc.ProcessName } else { "unknown" }
        Log "$Label - stopping $procName (PID $portPid) on port $Port..."
        Stop-ProcessTree -ProcessId $portPid -Label $Label
    }
}

function Kill-Orphans {
    # Use WMI (Get-CimInstance) since Get-Process.CommandLine doesn't work in PS 5.1
    $orphans = @()

    # Java processes (Spring Boot backend)
    $orphans += Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -match "promptly" -or $_.CommandLine -match "spring-boot:run" }

    # Node processes (Angular frontend / nx serve)
    $orphans += Get-CimInstance Win32_Process -Filter "Name = 'node.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -match "promptly" -or $_.CommandLine -match "nx serve" -or $_.CommandLine -match "nx.js" }

    # cmd.exe wrappers from start-dev.ps1
    $orphans += Get-CimInstance Win32_Process -Filter "Name = 'cmd.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -match "spring-boot:run" -or $_.CommandLine -match "nx serve" }

    foreach ($proc in $orphans) {
        $name = $proc.Name -replace '\.exe$', ''
        Log "Killing orphaned $name (PID $($proc.ProcessId))..."
        Stop-ProcessTree -ProcessId $proc.ProcessId -Label "Orphan"
    }
}

Write-Host "Stopping Promptly dev services..." -ForegroundColor White
Write-Host ""

# Pass 1: Kill by port
Kill-ByPort -Port 4200 -Label "Frontend"
Kill-ByPort -Port 8080 -Label "Backend"

# Brief pause so processes have time to release ports and exit
Start-Sleep -Milliseconds 500

# Pass 2: Kill any orphans left behind (WMI-based scan)
Kill-Orphans

# Pass 3: Final check — if ports are still in use, force kill
Start-Sleep -Milliseconds 500
$stillRunning = $false
foreach ($port in @(8080, 4200)) {
    $pids = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $pids) {
        Warn "Port $port still in use by PID $p - force killing..."
        Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
        $stillRunning = $true
    }
}

Write-Host ""
if ($stillRunning) {
    Start-Sleep -Milliseconds 500
}
Ok "All services stopped."
