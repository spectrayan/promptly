# PowerShell Seeding script for Promptly
$container = "mongo"
$db = "promptly"
$seedDir = $PSScriptRoot

Write-Host "🌱 Seeding Promptly database (container: $container, db: $db)..." -ForegroundColor Green

$collections = @{
    "users"                         = "users/users.json"
    "projects"                      = "projects/projects.json"
    "project_members"               = "project_members/project_members.json"
    "prompts"                       = "prompts/prompts.json"
    "prompt_history"                = "prompt_history/prompt_history.json"
    "workflows"                     = "workflows/workflows.json"
    "workflow_steps"                = "workflow_steps/workflow_steps.json"
    "scan_results"                  = "scan_results/scan_results.json"
    "audit_logs"                    = "audit_logs/audit_logs.json"
    "notifications"                 = "notifications/notifications.json"
    "notification_project_settings" = "notification_project_settings/notification_project_settings.json"
}

$orderedCollections = @(
    "users", "projects", "project_members", "prompts", "prompt_history",
    "workflows", "workflow_steps", "scan_results", "audit_logs",
    "notifications", "notification_project_settings"
)

foreach ($collection in $orderedCollections) {
    $jsonFile = $collections[$collection]
    $fullPath = Join-Path $seedDir $jsonFile

    if (-not (Test-Path $fullPath)) {
        Write-Host "  ⚠️  Skipping $collection — $jsonFile not found" -ForegroundColor Yellow
        continue
    }

    # Drop existing collection
    $dropCmd = "db.$collection.drop()"
    & docker exec -i $container mongosh $db --quiet --eval $dropCmd | Out-Null

    # Import via mongoimport
    Get-Content $fullPath -Raw | & docker exec -i $container mongoimport --db $db --collection $collection --jsonArray --quiet

    $countCmd = "db.$collection.countDocuments()"
    $count = & docker exec -i $container mongosh $db --quiet --eval $countCmd
    $cleanCount = $count.Trim()
    Write-Host "  ✅ $($collection): $($cleanCount) documents" -ForegroundColor Cyan
}

Write-Host "`n📇 Creating indexes..." -ForegroundColor Green
Get-Content (Join-Path $seedDir "init.js") -Raw | & docker exec -i $container mongosh $db --quiet

Write-Host "`n🎉 Done!" -ForegroundColor Green
