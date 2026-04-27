# ═══════════════════════════════════════════════════════════════════════
# Promptly — E2E Test Orchestration Script (PowerShell)
#
# Starts all services (MongoDB, Backend, Frontend), seeds the database,
# runs Playwright E2E tests, and tears everything down.
#
# Usage:
#   .\apps\e2e\promptly-e2e\scripts\run-e2e.ps1           # headless
#   .\apps\e2e\promptly-e2e\scripts\run-e2e.ps1 -Headed   # headed mode
#   .\apps\e2e\promptly-e2e\scripts\run-e2e.ps1 -Debug    # Playwright inspector
# ═══════════════════════════════════════════════════════════════════════

param(
    [switch]$Headed,
    [switch]$Debug,
    [string]$Spec = ""
)

$ErrorActionPreference = "Stop"
$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..\..\..\..") 
$E2eDir = Join-Path $RootDir "apps\e2e\promptly-e2e"
$TestExitCode = 1

# ── Helpers ──────────────────────────────────────────────
function Log($msg)  { Write-Host "[e2e] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[e2e] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[e2e] $msg" -ForegroundColor Yellow }
function Err($msg)  { Write-Host "[e2e] $msg" -ForegroundColor Red }

function Wait-ForEndpoint {
    param(
        [string]$Url,
        [string]$Label,
        [int]$TimeoutSeconds = 120,
        [int]$IntervalSeconds = 3
    )
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    Log "Waiting for $Label ($Url)..."

    while ($stopwatch.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Ok "$Label is ready! (${([math]::Round($stopwatch.Elapsed.TotalSeconds))}s)"
                return $true
            }
        }
        catch {
            # Not ready yet
        }
        Start-Sleep -Seconds $IntervalSeconds
    }

    Err "$Label did not become ready within ${TimeoutSeconds}s"
    return $false
}

function Stop-Port {
    param([int]$Port, [string]$Label)
    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $pids) {
        Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
        Log "Stopped $Label (PID $p, port $Port)"
    }
}

function Invoke-Teardown {
    Write-Host ""
    Log "Tearing down..."
    Stop-Port -Port 4200 -Label "Frontend"
    Stop-Port -Port 8080 -Label "Backend"
    # Don't tear down MongoDB Docker — other devs may be using it
    Ok "Teardown complete."
}

# ══════════════════════════════════════════════════════════════════════
# Main
# ══════════════════════════════════════════════════════════════════════

Push-Location $RootDir

try {
    Write-Host ""
    Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor White
    Write-Host "  Promptly — E2E Test Runner" -ForegroundColor White
    Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor White
    Write-Host ""

    # ── 1. Check Docker ─────────────────────────────────────────
    $dockerCmd = Get-Command docker -ErrorAction SilentlyContinue
    if (-not $dockerCmd) {
        Err "Docker is not installed or not in PATH."
        exit 1
    }

    # ── 2. Start MongoDB ────────────────────────────────────────
    $mongoRunning = Get-NetTCPConnection -LocalPort 27017 -State Listen -ErrorAction SilentlyContinue
    if ($mongoRunning) {
        Ok "MongoDB already running on port 27017."
    }
    else {
        Log "Starting MongoDB via Docker Compose..."
        docker compose up -d 2>$null
        if ($LASTEXITCODE -ne 0) {
            docker-compose up -d 2>$null
        }
    }

    # Wait for MongoDB health
    $mongoReady = Wait-ForEndpoint -Url "http://localhost:27017" -Label "MongoDB" -TimeoutSeconds 60 -IntervalSeconds 3
    # MongoDB doesn't have HTTP — check via docker instead
    Log "Checking MongoDB connectivity..."
    $attempts = 0
    while ($attempts -lt 20) {
        $pingResult = docker exec promptly-mongodb mongosh --eval "db.runCommand('ping').ok" --quiet 2>$null
        if ($pingResult -match "1") {
            Ok "MongoDB is healthy."
            break
        }
        Start-Sleep -Seconds 3
        $attempts++
    }
    if ($attempts -ge 20) {
        Err "MongoDB did not become healthy."
        exit 1
    }

    # ── 3. Start Backend ────────────────────────────────────────
    $backendRunning = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($backendRunning) {
        Ok "Backend already running on port 8080."
    }
    else {
        Log "Starting Spring Boot backend..."
        $backendProc = Start-Process -FilePath "cmd.exe" `
            -ArgumentList "/c", "mvn spring-boot:run -f apps/backend/core/pom.xml -Dspring-boot.run.profiles=dev" `
            -NoNewWindow -PassThru
        Log "Backend starting (PID $($backendProc.Id))..."
    }

    # Wait for backend
    $backendReady = Wait-ForEndpoint -Url "http://localhost:8080/actuator/health" -Label "Backend" -TimeoutSeconds 120
    if (-not $backendReady) {
        Err "Backend failed to start."
        Invoke-Teardown
        exit 1
    }

    # ── 4. Seed Database ────────────────────────────────────────
    Log "Seeding database..."
    & "$RootDir\scripts\seed.ps1"
    Ok "Database seeded."

    # ── 5. Start Frontend ───────────────────────────────────────
    $frontendRunning = Get-NetTCPConnection -LocalPort 4200 -State Listen -ErrorAction SilentlyContinue
    if ($frontendRunning) {
        Ok "Frontend already running on port 4200."
    }
    else {
        Log "Starting Angular dev server..."
        $frontendProc = Start-Process -FilePath "cmd.exe" `
            -ArgumentList "/c", "node_modules\.bin\nx serve promptly" `
            -NoNewWindow -PassThru
        Log "Frontend starting (PID $($frontendProc.Id))..."
    }

    # Wait for frontend
    $frontendReady = Wait-ForEndpoint -Url "http://localhost:4200" -Label "Frontend" -TimeoutSeconds 120
    if (-not $frontendReady) {
        Err "Frontend failed to start."
        Invoke-Teardown
        exit 1
    }

    # ── 6. Run Playwright Tests ─────────────────────────────────
    Write-Host ""
    Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Green
    Write-Host "  All services ready — running Playwright tests..." -ForegroundColor Green
    Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Green
    Write-Host ""

    $playwrightArgs = @("playwright", "test", "--config", "apps/e2e/promptly-e2e/playwright.config.ts")

    if ($Headed) {
        $playwrightArgs += "--headed"
    }
    if ($Debug) {
        $env:PWDEBUG = "1"
    }
    if ($Spec) {
        $playwrightArgs += $Spec
    }

    & npx @playwrightArgs
    $TestExitCode = $LASTEXITCODE

    if ($Debug) {
        Remove-Item Env:\PWDEBUG -ErrorAction SilentlyContinue
    }
}
catch {
    Err "Error: $_"
    $TestExitCode = 1
}
finally {
    Invoke-Teardown
    Pop-Location
}

# ── Report ──────────────────────────────────────────────────
Write-Host ""
if ($TestExitCode -eq 0) {
    Ok "All E2E tests passed!"
}
else {
    Err "E2E tests failed (exit code: $TestExitCode)"
    Warn "View report: npx playwright show-report reports/e2e"
}

exit $TestExitCode
