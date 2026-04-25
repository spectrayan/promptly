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

# ── Kill everything on a given port ──
function Stop-Port {
    param([int]$Port, [string]$Label)
    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $pids) {
        Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
        Log "Stopped $Label (PID $p, port $Port)"
    }
}

function Stop-DevServices {
    Write-Host ""
    Log "Shutting down..."
    Stop-Port -Port 4200 -Label "Frontend"
    Stop-Port -Port 8080 -Label "Backend"
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
            Ok "Infrastructure ready."
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
        -ArgumentList "/c", "npx nx serve promptly" `
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
