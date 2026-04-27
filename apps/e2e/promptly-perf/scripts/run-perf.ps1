# =======================================================================
# Promptly - Performance Test Orchestration Script (Kubernetes)
#
# Builds the backend image, deploys to a local K8s cluster (Docker Desktop),
# seeds data, runs k6 load tests, then tears down.
#
# Usage:
#   .\apps\e2e\promptly-perf\scripts\run-perf.ps1                 # full suite
#   .\apps\e2e\promptly-perf\scripts\run-perf.ps1 -Scenario smoke # smoke only
#   .\apps\e2e\promptly-perf\scripts\run-perf.ps1 -Scenario api   # k6 API only
#   .\apps\e2e\promptly-perf\scripts\run-perf.ps1 -SkipBuild      # skip maven/nx
#   .\apps\e2e\promptly-perf\scripts\run-perf.ps1 -KeepStack      # leave running
# =======================================================================

param(
    [ValidateSet("all", "smoke", "api", "soak", "sse", "lighthouse")]
    [string]$Scenario = "all",
    [switch]$SkipBuild,
    [switch]$KeepStack
)

$ErrorActionPreference = "Stop"
$RootDir     = Resolve-Path (Join-Path $PSScriptRoot "..\..\..\..")
$PerfDir     = Join-Path $RootDir "apps\e2e\promptly-perf"
$K8sDir      = Join-Path $PerfDir "k8s"
$ReportsDir  = Join-Path $PerfDir "reports"
$Namespace   = "promptly-perf"
$TestExitCode = 0

# ── Helpers ──────────────────────────────────────────────
function Log($msg)  { Write-Host "[perf] $msg" -ForegroundColor Cyan }
function Ok($msg)   { Write-Host "[perf] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[perf] $msg" -ForegroundColor Yellow }
function Err($msg)  { Write-Host "[perf] $msg" -ForegroundColor Red }

function Wait-ForEndpoint {
    param(
        [string]$Url,
        [string]$Label,
        [int]$TimeoutSeconds = 180,
        [int]$IntervalSeconds = 5
    )
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    Log "Waiting for $Label ($Url)..."

    while ($stopwatch.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Ok "$Label ready! ($([math]::Round($stopwatch.Elapsed.TotalSeconds))s)"
                return $true
            }
        }
        catch {}
        Start-Sleep -Seconds $IntervalSeconds
    }

    Err "$Label did not become ready within ${TimeoutSeconds}s"
    return $false
}

function Wait-ForK8sReady {
    param(
        [string]$Deployment,
        [int]$TimeoutSeconds = 180
    )
    Log "Waiting for deployment/$Deployment to be ready..."
    $result = & cmd /c "kubectl rollout status deployment/$Deployment -n $Namespace --timeout=${TimeoutSeconds}s 2>&1"
    $exitCode = $LASTEXITCODE
    $result | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
    if ($exitCode -ne 0) {
        Err "Deployment $Deployment failed to become ready."
        return $false
    }
    Ok "deployment/$Deployment is ready."
    return $true
}

# ======================================================================
# Main
# ======================================================================

Push-Location $RootDir

try {
    Write-Host ""
    Write-Host "===========================================================" -ForegroundColor White
    Write-Host "  Promptly - Performance Test Runner (Kubernetes)" -ForegroundColor White
    Write-Host "  Scenario: $Scenario" -ForegroundColor White
    Write-Host "===========================================================" -ForegroundColor White
    Write-Host ""

    # Ensure reports directory exists
    if (-not (Test-Path $ReportsDir)) { New-Item -ItemType Directory -Path $ReportsDir -Force | Out-Null }

    # ── 1. Build ────────────────────────────────────────────
    if (-not $SkipBuild) {
        Log "Building backend JAR..."
        & cmd /c "mvn package -pl apps/backend/core -am -DskipTests -B -q 2>&1"
        if ($LASTEXITCODE -ne 0) { Err "Maven build failed."; exit 1 }
        Ok "Backend JAR built."

        Log "Building frontend..."
        & cmd /c "npx nx build promptly --configuration=production 2>&1"
        if ($LASTEXITCODE -ne 0) { Warn "Frontend build failed (continuing without frontend)." }
        else { Ok "Frontend built." }
    }
    else {
        Warn 'Skipping build (-SkipBuild flag).'
    }

    # ── 1b. Build Docker images ─────────────────────────────
    Log "Building Docker image (promptly-backend:perf)..."
    $buildOutput = & cmd /c "docker build -t promptly-backend:perf -f apps/backend/core/Dockerfile . 2>&1"
    $buildOutput | Select-Object -Last 3 | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
    if ($LASTEXITCODE -ne 0) { Err "Backend Docker image build failed."; exit 1 }
    Ok "Backend Docker image built."

    Log "Building Docker image (promptly-nginx:perf)..."
    $buildOutput = & cmd /c "docker build -t promptly-nginx:perf -f apps/e2e/promptly-perf/nginx/Dockerfile . 2>&1"
    $buildOutput | Select-Object -Last 3 | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
    if ($LASTEXITCODE -ne 0) { Warn "Nginx Docker image build failed (frontend may not be available)." }
    else { Ok "Nginx Docker image built." }

    # ── 2. Deploy to Kubernetes ─────────────────────────────
    Log "Deploying perf stack to Kubernetes (namespace: $Namespace)..."

    # Clean up any previous deployment
    $existingNs = & cmd /c "kubectl get namespace $Namespace --no-headers 2>&1"
    if ($existingNs -match "Terminating") {
        Log "Waiting for old namespace to finish terminating..."
        & cmd /c "kubectl wait --for=delete namespace/$Namespace --timeout=120s 2>&1" | Out-Null
    }
    elseif ($existingNs -match "$Namespace") {
        Log "Cleaning up existing namespace..."
        & cmd /c "kubectl delete namespace $Namespace --wait=true 2>&1" | Out-Null
        Start-Sleep 3
    }

    # Create namespace
    & cmd /c "kubectl apply -f `"$K8sDir\namespace.yml`" 2>&1" | Out-Null

    # Deploy MongoDB first
    Log "Deploying MongoDB..."
    & cmd /c "kubectl apply -f `"$K8sDir\mongodb.yml`" 2>&1" | Out-Null
    $mongoReady = Wait-ForK8sReady -Deployment "mongodb" -TimeoutSeconds 120
    if (-not $mongoReady) {
        Err "MongoDB failed to start."
        & cmd /c "kubectl logs -l app=mongodb -n $Namespace --tail=20 2>&1" | ForEach-Object { Write-Host $_ }
        exit 1
    }

    # Deploy backend
    Log "Deploying backend (2 replicas)..."
    & cmd /c "kubectl apply -f `"$K8sDir\backend.yml`" 2>&1" | Out-Null
    $backendReady = Wait-ForK8sReady -Deployment "backend" -TimeoutSeconds 180
    if (-not $backendReady) {
        Err "Backend failed to start."
        & cmd /c "kubectl logs -l app=backend -n $Namespace --tail=30 2>&1" | ForEach-Object { Write-Host $_ }
        exit 1
    }

    # Deploy nginx
    Log "Deploying nginx LB..."
    & cmd /c "kubectl apply -f `"$K8sDir\nginx.yml`" 2>&1" | Out-Null
    $nginxReady = Wait-ForK8sReady -Deployment "nginx" -TimeoutSeconds 60
    if (-not $nginxReady) {
        Err "Nginx failed to start."
        exit 1
    }

    # Start port-forward in background for API access
    Log "Setting up port-forward (localhost:30080 -> svc/nginx:8080)..."
    $portForwardJob = Start-Job -ScriptBlock {
        param($ns)
        kubectl port-forward svc/nginx 30080:8080 -n $ns 2>&1
    } -ArgumentList $Namespace
    Start-Sleep 3

    # Wait for API to be accessible via port-forward
    $apiReady = Wait-ForEndpoint -Url "http://localhost:30080/actuator" -Label "API (via port-forward)" -TimeoutSeconds 60
    if (-not $apiReady) {
        Err "API not reachable via port-forward."
        & cmd /c "kubectl get pods -n $Namespace -o wide 2>&1" | ForEach-Object { Write-Host $_ }
        & cmd /c "kubectl logs -l app=backend -n $Namespace --tail=20 2>&1" | ForEach-Object { Write-Host $_ }
        exit 1
    }

    # ── 3. Seed Database ────────────────────────────────────
    Log "Seeding perf database..."
    $mongoPod = (& cmd /c "kubectl get pods -n $Namespace -l app=mongodb -o jsonpath='{.items[0].metadata.name}' 2>&1").Trim("'")

    $seedCollections = @(
        @{ collection = "users";           file = "seed-data\users\users.json" }
        @{ collection = "projects";        file = "seed-data\projects\projects.json" }
        @{ collection = "project_members"; file = "seed-data\project_members\project_members.json" }
        @{ collection = "prompts";         file = "seed-data\prompts\prompts.json" }
        @{ collection = "workflows";       file = "seed-data\workflows\workflows.json" }
    )

    foreach ($seed in $seedCollections) {
        $jsonPath = Join-Path $RootDir $seed.file
        if (Test-Path $jsonPath) {
            # Drop and re-import
            & cmd /c "kubectl exec -n $Namespace $mongoPod -- mongosh promptly --quiet --eval `"db.$($seed.collection).drop()`" 2>&1" | Out-Null
            # Copy JSON file to pod, then import
            & cmd /c "kubectl cp `"$jsonPath`" ${Namespace}/${mongoPod}:/tmp/seed.json 2>&1" | Out-Null
            & cmd /c "kubectl exec -n $Namespace $mongoPod -- mongoimport --db promptly --collection $($seed.collection) --jsonArray --file /tmp/seed.json --quiet 2>&1" | Out-Null
            Ok "Seeded: $($seed.collection)"
        }
    }

    # Run indexes
    $initJs = Join-Path $RootDir "seed-data\init.js"
    if (Test-Path $initJs) {
        & cmd /c "kubectl cp `"$initJs`" ${Namespace}/${mongoPod}:/tmp/init.js 2>&1" | Out-Null
        & cmd /c "kubectl exec -n $Namespace $mongoPod -- mongosh promptly --quiet --file /tmp/init.js 2>&1" | Out-Null
    }
    Ok "Database seeded."

    # ── 4. Run Tests ────────────────────────────────────────
    Write-Host ""
    Write-Host "===========================================================" -ForegroundColor Green
    Write-Host "  Stack ready - running performance tests..." -ForegroundColor Green
    Write-Host "===========================================================" -ForegroundColor Green
    Write-Host ""

    $k6Scripts = Join-Path $PerfDir "k6\scripts"
    # k6 runs against the NodePort URL
    $baseUrl = "http://host.docker.internal:30080"

    # ── k6 Smoke ──
    if ($Scenario -eq "all" -or $Scenario -eq "smoke") {
        Log "Running k6 smoke test..."
        & cmd /c "docker run --rm --add-host=host.docker.internal:host-gateway -v `"${k6Scripts}:/scripts`" -v `"${ReportsDir}:/reports`" -e `"BASE_URL=$baseUrl`" grafana/k6:latest run /scripts/smoke.js 2>&1" | ForEach-Object { Write-Host $_ }
        if ($LASTEXITCODE -ne 0) {
            Warn "Smoke test failed."
            $TestExitCode = 1
        } else { Ok "Smoke test passed." }
    }

    # ── k6 API (prompt CRUD) ──
    if ($Scenario -eq "all" -or $Scenario -eq "api") {
        Log "Running k6 prompt-crud test..."
        & cmd /c "docker run --rm --add-host=host.docker.internal:host-gateway -v `"${k6Scripts}:/scripts`" -v `"${ReportsDir}:/reports`" -e `"BASE_URL=$baseUrl`" grafana/k6:latest run /scripts/prompt-crud.js 2>&1" | ForEach-Object { Write-Host $_ }
        if ($LASTEXITCODE -ne 0) {
            Warn "Prompt CRUD test had failures."
            $TestExitCode = 1
        } else { Ok "Prompt CRUD test passed." }
    }

    # ── k6 Soak ──
    if ($Scenario -eq "soak") {
        Log "Running k6 soak test (5-minute sustained load)..."
        & cmd /c "docker run --rm --add-host=host.docker.internal:host-gateway -v `"${k6Scripts}:/scripts`" -v `"${ReportsDir}:/reports`" -e `"BASE_URL=$baseUrl`" grafana/k6:latest run /scripts/soak.js 2>&1" | ForEach-Object { Write-Host $_ }
        if ($LASTEXITCODE -ne 0) {
            Warn "Soak test had failures."
            $TestExitCode = 1
        } else { Ok "Soak test passed." }
    }

    # ── k6 SSE ──
    if ($Scenario -eq "all" -or $Scenario -eq "sse") {
        Log "Running k6 SSE streaming test..."
        & cmd /c "docker run --rm --add-host=host.docker.internal:host-gateway -v `"${k6Scripts}:/scripts`" -v `"${ReportsDir}:/reports`" -e `"BASE_URL=$baseUrl`" grafana/k6:latest run /scripts/ai-stream.js 2>&1" | ForEach-Object { Write-Host $_ }
        if ($LASTEXITCODE -ne 0) {
            Warn "SSE streaming test had failures."
            $TestExitCode = 1
        } else { Ok "SSE streaming test passed." }
    }

    # ── Lighthouse CI ──
    if ($Scenario -eq "all" -or $Scenario -eq "lighthouse") {
        # Start port-forward for frontend if not already running
        Log "Setting up port-forward for frontend (localhost:30042 -> svc/nginx:4200)..."
        $fePortForwardJob = Start-Job -ScriptBlock {
            param($ns)
            kubectl port-forward svc/nginx 30042:4200 -n $ns 2>&1
        } -ArgumentList $Namespace
        Start-Sleep 3

        $feReady = Wait-ForEndpoint -Url "http://localhost:30042" -Label "Frontend (via port-forward)" -TimeoutSeconds 30
        if ($feReady) {
            Log "Running Lighthouse CI audit..."
            $lhConfig = Join-Path $PerfDir "lighthouse\lighthouserc.json"
            # Override the URL to use the port-forwarded frontend
            $gitHash = git rev-parse HEAD 2>$null
            if (-not $gitHash) { $gitHash = "local" }
            $env:LHCI_BUILD_CONTEXT__CURRENT_HASH = $gitHash
            & cmd /c "npx @lhci/cli autorun --config=`"$lhConfig`" --collect.url=http://localhost:30042 2>&1" | ForEach-Object { Write-Host $_ }
            if ($LASTEXITCODE -ne 0) {
                Warn "Lighthouse CI had failures."
                $TestExitCode = 1
            } else { Ok "Lighthouse CI passed." }

            # Copy Lighthouse reports to our reports dir
            $lhciDir = Join-Path $PerfDir ".lighthouseci"
            if (Test-Path $lhciDir) {
                $lhReportsDir = Join-Path $ReportsDir "lighthouse"
                if (-not (Test-Path $lhReportsDir)) { New-Item -ItemType Directory -Path $lhReportsDir -Force | Out-Null }
                Copy-Item "$lhciDir\*" $lhReportsDir -Recurse -Force -ErrorAction SilentlyContinue
                Ok "Lighthouse reports copied to: $lhReportsDir"
            }
        } else {
            Warn "Frontend not reachable - skipping Lighthouse CI."
        }

        # Clean up frontend port-forward
        if ($fePortForwardJob) {
            Stop-Job $fePortForwardJob -ErrorAction SilentlyContinue
            Remove-Job $fePortForwardJob -Force -ErrorAction SilentlyContinue
        }
    }
}
catch {
    Err "Error: $_"
    $TestExitCode = 1
}
finally {
    # Stop port-forward
    if ($portForwardJob) {
        Stop-Job $portForwardJob -ErrorAction SilentlyContinue
        Remove-Job $portForwardJob -Force -ErrorAction SilentlyContinue
    }

    if (-not $KeepStack) {
        Write-Host ""
        Log "Tearing down perf stack..."
        & cmd /c "kubectl delete namespace $Namespace --ignore-not-found 2>&1" | Out-Null
        Ok "K8s namespace deleted."
    }
    else {
        Warn "Stack left running (-KeepStack flag). Tear down manually:"
        Warn "  kubectl delete namespace $Namespace"
    }
    Pop-Location
}

# ── Summary ──────────────────────────────────────────────
Write-Host ""
Write-Host '===========================================================' -ForegroundColor White
if ($TestExitCode -eq 0) {
    Ok "All performance tests passed!"
}
else {
    Err "Some performance tests failed (exit code: $TestExitCode)"
}
Write-Host "  Reports: $ReportsDir" -ForegroundColor Gray
Write-Host '===========================================================' -ForegroundColor White

exit $TestExitCode
