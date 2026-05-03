#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — E2E Test Orchestration Script (Bash)
#
# Starts MongoDB, Backend (mongo profile), Frontend, seeds the database,
# runs Playwright E2E tests, and tears everything down.
#
# Usage:
#   ./apps/e2e/promptly-e2e/scripts/run-e2e-mongo.sh           # headless
#   ./apps/e2e/promptly-e2e/scripts/run-e2e-mongo.sh --headed  # headed mode
#   ./apps/e2e/promptly-e2e/scripts/run-e2e.sh --debug   # Playwright inspector
# ═══════════════════════════════════════════════════════════════════════

set -e

HEADED=0
DEBUG=0
SPEC=""

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --headed) HEADED=1 ;;
        --debug) DEBUG=1 ;;
        *) SPEC="$1" ;;
    esac
    shift
done

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../.." && pwd)
cd "$ROOT_DIR"

function log()  { echo -e "\e[36m[e2e] $1\e[0m"; }
function ok()   { echo -e "\e[32m[e2e] $1\e[0m"; }
function warn() { echo -e "\e[33m[e2e] $1\e[0m"; }
function err()  { echo -e "\e[31m[e2e] $1\e[0m"; }

function wait_for_endpoint() {
    local url=$1
    local label=$2
    local timeout=${3:-120}
    local start_time=$(date +%s)

    log "Waiting for $label ($url)..."
    while true; do
        if curl -s -f "$url" > /dev/null; then
            local current_time=$(date +%s)
            local elapsed=$((current_time - start_time))
            ok "$label is ready! (${elapsed}s)"
            return 0
        fi
        
        local current_time=$(date +%s)
        local elapsed=$((current_time - start_time))
        if [ $elapsed -ge $timeout ]; then
            err "$label did not become ready within ${timeout}s"
            return 1
        fi
        sleep 3
    done
}

function stop_port() {
    local port=$1
    local label=$2
    local pids=$(lsof -ti tcp:$port || true)
    if [ ! -z "$pids" ]; then
        kill -9 $pids 2>/dev/null || true
        log "Stopped $label (PIDs: $pids, port: $port)"
    fi
}

function teardown() {
    echo ""
    log "Tearing down..."
    stop_port 4200 "Frontend"
    stop_port 8080 "Backend"
    ok "Teardown complete."
}

trap 'teardown' EXIT

echo "═══════════════════════════════════════════════════════════"
echo "  Promptly — E2E Test Runner (MongoDB)"
echo "═══════════════════════════════════════════════════════════"
echo ""

if ! command -v docker &> /dev/null; then
    err "Docker is not installed or not in PATH."
    exit 1
fi

if ! lsof -i:27017 -sTCP:LISTEN -t >/dev/null ; then
    log "Starting MongoDB via Docker Compose..."
    docker compose -f docker-compose.yml up -d
else
    ok "MongoDB already running on port 27017."
fi

log "Checking MongoDB connectivity..."
attempts=0
while [ $attempts -lt 20 ]; do
    if docker exec promptly-mongodb mongosh --eval "db.runCommand('ping').ok" >/dev/null 2>&1; then
        ok "MongoDB is healthy."
        break
    fi
    sleep 3
    attempts=$((attempts + 1))
done
if [ $attempts -ge 20 ]; then
    err "MongoDB did not become healthy."
    exit 1
fi

if ! lsof -i:8080 -sTCP:LISTEN -t >/dev/null ; then
    log "Starting Spring Boot backend..."
    PROMPTLY_PERSISTENCE_TYPE=mongo SPRING_PROFILES_ACTIVE=dev,mongo mvn spring-boot:run -Ppersistence-mongo -f apps/backend/core/pom.xml > /tmp/backend.log 2>&1 &
    BACKEND_PID=$!
    log "Backend starting (PID $BACKEND_PID)..."
else
    ok "Backend already running on port 8080."
fi

if ! wait_for_endpoint "http://localhost:8080/actuator/health" "Backend" 120; then
    err "Backend failed to start. Logs:"
    tail -n 50 /tmp/backend.log
    exit 1
fi

log "Seeding database..."
bash seed-data/seed.sh
ok "Database seeded."

if ! lsof -i:4200 -sTCP:LISTEN -t >/dev/null ; then
    log "Starting Angular dev server..."
    npx nx serve promptly > /tmp/frontend.log 2>&1 &
    FRONTEND_PID=$!
    log "Frontend starting (PID $FRONTEND_PID)..."
else
    ok "Frontend already running on port 4200."
fi

if ! wait_for_endpoint "http://localhost:4200" "Frontend" 120; then
    err "Frontend failed to start. Logs:"
    tail -n 50 /tmp/frontend.log
    exit 1
fi

echo ""
echo -e "\e[32m═══════════════════════════════════════════════════════════\e[0m"
echo -e "\e[32m  All services ready — running Playwright tests...\e[0m"
echo -e "\e[32m═══════════════════════════════════════════════════════════\e[0m"
echo ""

PLAYWRIGHT_ARGS=("playwright" "test" "--config" "apps/e2e/promptly-e2e/playwright.config.ts")

if [ "$HEADED" -eq 1 ]; then
    PLAYWRIGHT_ARGS+=("--headed")
fi
if [ "$DEBUG" -eq 1 ]; then
    export PWDEBUG=1
fi
if [ ! -z "$SPEC" ]; then
    PLAYWRIGHT_ARGS+=("$SPEC")
fi

npx "${PLAYWRIGHT_ARGS[@]}"
TEST_EXIT_CODE=$?

if [ "$DEBUG" -eq 1 ]; then
    unset PWDEBUG
fi

echo ""
if [ $TEST_EXIT_CODE -eq 0 ]; then
    ok "All E2E tests passed!"
else
    err "E2E tests failed (exit code: $TEST_EXIT_CODE)"
    warn "View report: npx playwright show-report reports/e2e"
fi

exit $TEST_EXIT_CODE
