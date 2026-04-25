# ═══════════════════════════════════════════════════════════════════════
# Promptly — Seed Database (PowerShell)
# Loads seed-data/init.js into the running MongoDB container.
# Usage: .\scripts\seed.ps1
# ═══════════════════════════════════════════════════════════════════════

$RootDir = Split-Path -Parent $PSScriptRoot
$SeedFile = Join-Path $RootDir "seed-data\init.js"
$Container = "promptly-mongodb"
$Database = "test"

function Log($msg)  { Write-Host "[seed] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[seed] $msg" -ForegroundColor Green }
function Err($msg)  { Write-Host "[seed] $msg" -ForegroundColor Red }

if (-not (Test-Path $SeedFile)) {
    Err "Seed file not found: $SeedFile"
    exit 1
}

# Check if container is running
$running = docker ps --format "{{.Names}}" 2>$null | Where-Object { $_ -eq $Container }
if (-not $running) {
    Err "Container '$Container' is not running. Start it first with .\scripts\start-dev.ps1"
    exit 1
}

Log "Seeding database '$Database' from $SeedFile..."
Get-Content $SeedFile -Raw | docker exec -i $Container mongosh $Database --quiet

if ($LASTEXITCODE -eq 0) {
    Ok "Database seeded successfully."
}
else {
    Err "Seeding failed."
    exit 1
}
