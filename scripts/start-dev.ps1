# ═══════════════════════════════════════════════════════════════════════
# Promptly — Dev Startup Script (PowerShell)
# Starts MongoDB (Docker), Spring Boot backend, and Angular frontend.
# Press Ctrl+C to gracefully stop all services.
# Usage: .\scripts\start-dev.ps1
# ═══════════════════════════════════════════════════════════════════════

$RootDir = Split-Path -Parent $PSScriptRoot

# ── Helpers ──
function Log($msg)  { Write-Host "[promptly] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[promptly] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[promptly] $msg" -ForegroundColor Yellow }
function Err($msg)  { Write-Host "[promptly] $msg" -ForegroundColor Red }

# ── Walk up the process tree to find the root ancestor ──
function Get-RootAncestorPid {
    param([int]$Pid)
    $current = $Pid
    $visited = @{}
    while ($true) {
        if ($visited.ContainsKey($current)) { break }
        $visited[$current] = $true
        $proc = Get-CimInstance Win32_Process -Filter "ProcessId = $current" -ErrorAction SilentlyContinue
        if (-not $proc -or -not $proc.ParentProcessId) { break }
        $parentName = (Get-Process -Id $proc.ParentProcessId -ErrorAction SilentlyContinue).ProcessName
        if ($parentName -in @("explorer", "services", "svchost", "wininit", "csrss", "System", "powershell", "pwsh", "WindowsTerminal", "Code")) {
            break
        }
        $current = $proc.ParentProcessId
    }
    return $current
}

# ── Kill entire process tree rooted at a PID ──
function Stop-ProcessTree {
    param([int]$Pid, [string]$Label)
    taskkill /T /F /PID $Pid 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Log "Stopped $Label process tree (root PID $Pid)"
    }
}

# ── Kill everything on a given port (walk up to root, kill entire tree) ──
function Stop-Port {
    param([int]$Port, [string]$Label)
    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique
    $roots = @{}
    foreach ($p in $pids) {
        $rootPid = Get-RootAncestorPid -Pid $p
        $roots[$rootPid] = $true
    }
    foreach ($rootPid in $roots.Keys) {
        Stop-ProcessTree -Pid $rootPid -Label $Label
    }
}

function Stop-DevServices {
    Write-Host ""
    Log "Shutting down..."
    Stop-Port -Port 4200 -Label "Frontend"
    Stop-Port -Port 8080 -Label "Backend"

    # Kill any orphaned cmd.exe wrappers from this script
    $orphans = Get-CimInstance Win32_Process -Filter "Name = 'cmd.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -match "spring-boot:run" -or $_.CommandLine -match "nx serve" }
    foreach ($proc in $orphans) {
        Stop-ProcessTree -Pid $proc.ProcessId -Label "Orphan cmd"
    }

    Ok "All services stopped."
}

Push-Location $RootDir

try {
    # ═══════════════════════════════════════════════════════════════════
    # 1. Infrastructure — MongoDB
    # ═══════════════════════════════════════════════════════════════════
    $MongoPort = 27017
    $mongoRunning = Get-NetTCPConnection -LocalPort $MongoPort -State Listen -ErrorAction SilentlyContinue

    if ($mongoRunning) {
        Ok "MongoDB already running on port $MongoPort - skipping Docker."
    }
    else {
        Log "MongoDB not detected on port $MongoPort. Starting via Docker..."
        $dockerCmd = Get-Command docker -ErrorAction SilentlyContinue
        if ($dockerCmd) {
            docker compose up -d 2>$null
            if ($LASTEXITCODE -ne 0) {
                docker-compose up -d 2>$null
            }

            # Wait for MongoDB to accept connections (up to 30s)
            $maxWait = 30
            $waited = 0
            Log "Waiting for MongoDB to become ready..."
            while ($waited -lt $maxWait) {
                $ready = Get-NetTCPConnection -LocalPort $MongoPort -State Listen -ErrorAction SilentlyContinue
                if ($ready) { break }
                Start-Sleep -Seconds 1
                $waited++
            }
            if ($waited -ge $maxWait) {
                Err "MongoDB did not start within ${maxWait}s. Check Docker logs."
                return
            }
            # Give MongoDB a moment to finish replica set init
            Start-Sleep -Seconds 2
            Ok "Infrastructure ready (MongoDB up in ${waited}s)."
        }
        else {
            Err "Docker not found and MongoDB is not running. Please start MongoDB on port $MongoPort."
            return
        }
    }

    # ═══════════════════════════════════════════════════════════════════
    # 2. Backend — Spring Boot (dev profile)
    # ═══════════════════════════════════════════════════════════════════
    Log "Starting backend (Spring Boot)..."

    $backendProc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c", "mvn spring-boot:run -f apps/backend/core/pom.xml -Dspring-boot.run.profiles=dev" `
        -NoNewWindow -PassThru

    Ok "Backend starting (PID $($backendProc.Id)) - http://localhost:8080"

    # ═══════════════════════════════════════════════════════════════════
    # 3. Frontend — Angular dev server
    # ═══════════════════════════════════════════════════════════════════
    Log "Starting frontend (Angular)..."

    $frontendProc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c", "node_modules\.bin\nx serve promptly" `
        -NoNewWindow -PassThru

    Ok "Frontend starting (PID $($frontendProc.Id)) - http://localhost:4200"

    # ═══════════════════════════════════════════════════════════════════
    # Status Banner
    # ═══════════════════════════════════════════════════════════════════
    Write-Host ""
    Write-Host "===========================================================" -ForegroundColor White
    Write-Host "  Promptly Dev Environment" -ForegroundColor White
    Write-Host "  Frontend  -> http://localhost:4200" -ForegroundColor Cyan
    Write-Host "  Backend   -> http://localhost:8080" -ForegroundColor Cyan
    Write-Host "  MongoDB   -> localhost:27017" -ForegroundColor Cyan
    Write-Host "===========================================================" -ForegroundColor White
    Write-Host "  Press Ctrl+C to stop all services" -ForegroundColor Yellow
    Write-Host ""

    # ═══════════════════════════════════════════════════════════════════
    # Wait for either process to exit — Ctrl+C breaks out to finally
    # ═══════════════════════════════════════════════════════════════════
    while (-not $backendProc.HasExited -and -not $frontendProc.HasExited) {
        Start-Sleep -Seconds 1
    }

    if ($backendProc.HasExited) {
        Warn "Backend exited (code $($backendProc.ExitCode))."
    }
    if ($frontendProc.HasExited) {
        Warn "Frontend exited (code $($frontendProc.ExitCode))."
    }
}
catch {
    Warn "Interrupted: $_"
}
finally {
    Stop-DevServices
    Pop-Location
}
