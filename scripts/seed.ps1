# ═══════════════════════════════════════════════════════════════════════
# Promptly — Seed Database (PowerShell)
# Imports per-collection JSON files via mongoimport then creates indexes.
# Usage: .\scripts\seed.ps1
# ═══════════════════════════════════════════════════════════════════════

$RootDir = Split-Path -Parent $PSScriptRoot
$SeedDir = Join-Path $RootDir "seed-data"
$Container = "promptly-mongodb"
$Database = "promptly"

function Log($msg)  { Write-Host "[seed] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[seed] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[seed] $msg" -ForegroundColor Yellow }
function Err($msg)  { Write-Host "[seed] $msg" -ForegroundColor Red }

# Check container
$running = docker ps --format "{{.Names}}" 2>$null | Where-Object { $_ -eq $Container }
if (-not $running) {
    # Fallback to mongo-vector
    $Container = "mongo-vector"
    $running = docker ps --format "{{.Names}}" 2>$null | Where-Object { $_ -eq $Container }
    if (-not $running) {
        Err "No MongoDB container found (tried promptly-mongodb, mongo-vector). Start it first."
        exit 1
    }
}

Log "Seeding database '$Database' (container: $Container)..."
Write-Host ""

# ── Collection -> JSON file mapping ──
$collections = [ordered]@{
    "users"           = "users/users.json"
    "projects"        = "projects/projects.json"
    "project_members" = "project_members/project_members.json"
    "prompts"         = "prompts/prompts.json"
    "workflows"       = "workflows/workflows.json"
    "scan_results"    = "scan_results/scan_results.json"
    "audit_logs"      = "audit_logs/audit_logs.json"
}

# ── Import each collection ──
foreach ($entry in $collections.GetEnumerator()) {
    $collection = $entry.Key
    $jsonFile = Join-Path $SeedDir $entry.Value

    if (-not (Test-Path $jsonFile)) {
        Warn "Skipping $collection - $($entry.Value) not found"
        continue
    }

    # Drop existing collection
    $dropCmd = "db.$collection.drop()"
    echo $dropCmd | docker exec -i $Container mongosh $Database --quiet 2>$null

    # Import via mongoimport
    Get-Content $jsonFile -Raw | docker exec -i $Container mongoimport --db $Database --collection $collection --jsonArray --quiet 2>$null

    # Count documents
    $countCmd = "db.$collection.countDocuments()"
    $count = echo $countCmd | docker exec -i $Container mongosh $Database --quiet 2>$null | Select-Object -Last 1

    Ok "$collection`: $count documents"
}

Write-Host ""

# ── Create indexes ──
Log "Creating indexes..."
$initJs = Join-Path $SeedDir "init.js"
if (Test-Path $initJs) {
    Get-Content $initJs -Raw | docker exec -i $Container mongosh $Database --quiet 2>$null
}

Write-Host ""
Ok "Done!"
