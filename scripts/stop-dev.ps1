# ═══════════════════════════════════════════════════════════════════════
# Promptly — Dev Stop Script (PowerShell)
# Kills processes running on frontend (4200) and backend (8080) ports.
# ═══════════════════════════════════════════════════════════════════════

$ErrorActionPreference = "SilentlyContinue"

function Log($msg)  { Write-Host "[promptly] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[promptly] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[promptly] $msg" -ForegroundColor Yellow }

function Kill-Port {
    param(
        [int]$Port,
        [string]$Label
    )

    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique

    if (-not $pids) {
        Warn "$Label - nothing running on port $Port."
        return
    }

    foreach ($pid in $pids) {
        try {
            $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
            $procName = if ($proc) { $proc.ProcessName } else { "unknown" }

            # Kill the process tree (parent + children)
            Stop-Process -Id $pid -Force -ErrorAction Stop
            Ok "$Label - killed $procName (PID $pid, port $Port)."
        } catch {
            Warn "$Label - failed to kill PID $pid."
        }
    }
}

Write-Host "Stopping Promptly dev services..." -ForegroundColor White
Write-Host ""

Kill-Port -Port 4200 -Label "Frontend"
Kill-Port -Port 8080 -Label "Backend"

# Also kill any orphaned Maven/Node processes from Promptly
$orphanedMvn = Get-Process -Name "java" -ErrorAction SilentlyContinue |
    Where-Object { $_.CommandLine -match "promptly" -or $_.CommandLine -match "spring-boot:run" }
foreach ($proc in $orphanedMvn) {
    try {
        Stop-Process -Id $proc.Id -Force
        Ok "Killed orphaned Java process (PID $($proc.Id))."
    } catch {}
}

Write-Host ""
Ok "All services stopped."
