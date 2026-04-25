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

# ── Track child processes for cleanup ──
$script:BackendJob  = $null
$script:FrontendJob = $null

function Stop-DevServices {
    Write-Host ""
    Log "Shutting down..."

    # Kill frontend (node/ng) on port 4200
    $feProcs = Get-NetTCPConnection -LocalPort 4200 -State Listen -ErrorAction SilentlyContinue |
               Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $feProcs) {
        Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
        Log "Stopped frontend process (PID $p)"
    }

    # Kill backend (java) on port 8080
    $beProcs = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
               Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $beProcs) {
        Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
        Log "Stopped backend process (PID $p)"
    }

    # Stop PowerShell background jobs
    if ($script:BackendJob) {
        Stop-Job -Job $script:BackendJob -ErrorAction SilentlyContinue
        Remove-Job -Job $script:BackendJob -Force -ErrorAction SilentlyContinue
    }
    if ($script:FrontendJob) {
        Stop-Job -Job $script:FrontendJob -ErrorAction SilentlyContinue
        Remove-Job -Job $script:FrontendJob -Force -ErrorAction SilentlyContinue
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

    $script:BackendJob = Start-Job -ScriptBlock {
        param($root)
        Set-Location $root
        & mvn spring-boot:run -f apps/backend/core/pom.xml "-Dspring-boot.run.profiles=dev" 2>&1
    } -ArgumentList $RootDir

    Ok "Backend starting (Job $($script:BackendJob.Id)) - http://localhost:8080"

    # ═══════════════════════════════════════════════════════════════════
    # 3. Frontend — Angular dev server
    # ═══════════════════════════════════════════════════════════════════
    Log "Starting frontend (Angular)..."

    $script:FrontendJob = Start-Job -ScriptBlock {
        param($root)
        Set-Location $root
        & npx nx serve promptly 2>&1
    } -ArgumentList $RootDir

    Ok "Frontend starting (Job $($script:FrontendJob.Id)) - http://localhost:4200"

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
    # Stream output and wait — Ctrl+C breaks this loop
    # ═══════════════════════════════════════════════════════════════════
    while ($true) {
        # Stream backend output
        $beOutput = Receive-Job -Job $script:BackendJob -ErrorAction SilentlyContinue
        if ($beOutput) {
            $beOutput | ForEach-Object { Write-Host "[backend]  $_" -ForegroundColor DarkGray }
        }

        # Stream frontend output
        $feOutput = Receive-Job -Job $script:FrontendJob -ErrorAction SilentlyContinue
        if ($feOutput) {
            $feOutput | ForEach-Object { Write-Host "[frontend] $_" -ForegroundColor DarkGray }
        }

        # Check if either job has failed
        if ($script:BackendJob.State -eq 'Failed') {
            Err "Backend process exited unexpectedly!"
            Receive-Job -Job $script:BackendJob -ErrorAction SilentlyContinue | Write-Host
            break
        }
        if ($script:FrontendJob.State -eq 'Failed') {
            Err "Frontend process exited unexpectedly!"
            Receive-Job -Job $script:FrontendJob -ErrorAction SilentlyContinue | Write-Host
            break
        }

        Start-Sleep -Milliseconds 500
    }
}
catch {
    Warn "Interrupted: $_"
}
finally {
    Stop-DevServices
    Pop-Location
}
