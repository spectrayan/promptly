#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — Dev Startup Script
# Starts MongoDB (Docker), Spring Boot backend, and Angular frontend.
# Press Ctrl+C to gracefully stop all services.
# ═══════════════════════════════════════════════════════════════════════

# Don't use set -e — it causes the script to exit on Ctrl+C before
# the trap handler can run. We handle errors explicitly.
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# ── Colors ──
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

log()  { echo -e "${CYAN}[promptly]${NC} $1"; }
ok()   { echo -e "${GREEN}[promptly]${NC} $1"; }
warn() { echo -e "${YELLOW}[promptly]${NC} $1"; }
err()  { echo -e "${RED}[promptly]${NC} $1"; }

# ── Track child PIDs ──
BACKEND_PID=""
FRONTEND_PID=""
SHUTTING_DOWN=0

# ── Kill a process and all its children ──
kill_tree() {
  local pid=$1
  # Get all child PIDs recursively
  local children
  children=$(pgrep -P "$pid" 2>/dev/null || true)
  for child in $children; do
    kill_tree "$child"
  done
  kill "$pid" 2>/dev/null || true
}

# ── Port-based kill (fallback — catches orphaned processes) ──
kill_port() {
  local port=$1
  local label=$2
  local pids

  # Try multiple methods to find processes on the port
  if command -v lsof &>/dev/null; then
    pids=$(lsof -iTCP:"$port" -sTCP:LISTEN -t 2>/dev/null || true)
  elif command -v ss &>/dev/null; then
    pids=$(ss -tlnp "sport = :$port" 2>/dev/null | grep -oP 'pid=\K\d+' || true)
  elif command -v netstat &>/dev/null; then
    pids=$(netstat -tlnp 2>/dev/null | grep ":$port " | grep -oP '\d+(?=/)' || true)
  fi

  if [[ -n "$pids" ]]; then
    for pid in $pids; do
      kill_tree "$pid"
      log "Stopped $label (PID $pid, port $port)"
    done
  fi
}

# ── Cleanup on exit ──
cleanup() {
  # Prevent re-entry
  [[ "$SHUTTING_DOWN" -eq 1 ]] && return
  SHUTTING_DOWN=1

  echo ""
  log "Shutting down..."

  # First: kill tracked PIDs and their process trees
  if [[ -n "$FRONTEND_PID" ]]; then
    kill_tree "$FRONTEND_PID"
    log "Stopped frontend (PID $FRONTEND_PID)"
  fi
  if [[ -n "$BACKEND_PID" ]]; then
    kill_tree "$BACKEND_PID"
    log "Stopped backend (PID $BACKEND_PID)"
  fi

  # Second: port-based fallback to catch anything the tree-kill missed
  sleep 1
  kill_port 4200 "frontend (port cleanup)"
  kill_port 8080 "backend (port cleanup)"

  ok "All services stopped."
}

# Trap SIGINT (Ctrl+C), SIGTERM, and EXIT
trap cleanup INT TERM EXIT

cd "$ROOT_DIR"

# ── Java ──────────────────────────────────────────────────────────────
# Project requires Java 21 — auto-detect or override
if [[ -n "${JAVA_HOME:-}" ]] && "$JAVA_HOME/bin/java" -version 2>&1 | grep -q "21\."; then
  ok "Using Java 21 from JAVA_HOME=$JAVA_HOME"
else
  # Try common Java 21 locations
  for candidate in \
    "/home/linuxbrew/.linuxbrew/opt/openjdk@21" \
    "/usr/lib/jvm/java-21-openjdk-amd64" \
    "/usr/lib/jvm/java-21" \
    "$HOME/.sdkman/candidates/java/current"; do
    if [[ -d "$candidate" ]]; then
      export JAVA_HOME="$candidate"
      export PATH="$JAVA_HOME/bin:$PATH"
      ok "Using Java 21 from $JAVA_HOME"
      break
    fi
  done
  if ! java -version 2>&1 | grep -q "21\."; then
    warn "Java 21 not detected — using system default: $(java -version 2>&1 | head -1)"
  fi
fi

# ═══════════════════════════════════════════════════════════════════════
# 1. Infrastructure — MongoDB
# ═══════════════════════════════════════════════════════════════════════
MONGO_PORT=27017

is_port_open() {
  if command -v ss &>/dev/null; then
    ss -tlnp 2>/dev/null | grep -q ":${1} "
  elif command -v lsof &>/dev/null; then
    lsof -iTCP:"$1" -sTCP:LISTEN &>/dev/null
  elif command -v netstat &>/dev/null; then
    netstat -tlnp 2>/dev/null | grep -q ":${1} "
  else
    return 1
  fi
}

if is_port_open "$MONGO_PORT"; then
  ok "MongoDB already running on port ${MONGO_PORT} — skipping Docker."
else
  log "MongoDB not detected on port ${MONGO_PORT}. Starting via Docker..."
  if command -v docker &>/dev/null; then
    docker compose up -d 2>/dev/null || docker-compose up -d 2>/dev/null || warn "Docker Compose failed — is Docker running?"
    ok "Infrastructure ready."
  else
    err "Docker not found and MongoDB is not running. Please start MongoDB on port ${MONGO_PORT}."
    exit 1
  fi
fi

# ═══════════════════════════════════════════════════════════════════════
# 2. Backend — Spring Boot (dev profile)
# ═══════════════════════════════════════════════════════════════════════
log "Starting backend (Spring Boot)..."

mvn spring-boot:run \
  -f apps/backend/core/pom.xml \
  -Dspring-boot.run.profiles=dev \
  -q &
BACKEND_PID=$!

ok "Backend starting (PID $BACKEND_PID) — http://localhost:8080"

# ═══════════════════════════════════════════════════════════════════════
# 3. Frontend — Angular dev server
# ═══════════════════════════════════════════════════════════════════════
log "Starting frontend (Angular)..."

node_modules/.bin/nx serve promptly &
FRONTEND_PID=$!

ok "Frontend starting (PID $FRONTEND_PID) — http://localhost:4200"

# ═══════════════════════════════════════════════════════════════════════
# Wait — Ctrl+C triggers the trap which runs cleanup()
# ═══════════════════════════════════════════════════════════════════════
echo ""
echo -e "${BOLD}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}  Promptly Dev Environment${NC}"
echo -e "  ${CYAN}Frontend${NC}  → http://localhost:4200"
echo -e "  ${CYAN}Backend${NC}   → http://localhost:8080"
echo -e "  ${CYAN}MongoDB${NC}   → localhost:27017"
echo -e "${BOLD}═══════════════════════════════════════════════════════════${NC}"
echo -e "  Press ${YELLOW}Ctrl+C${NC} to stop all services"
echo ""

# Wait for both background processes. The || true prevents
# set -u/pipefail from aborting on the signal interrupt.
wait $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
