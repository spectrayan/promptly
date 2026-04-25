#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — Dev Startup Script
# Starts MongoDB (Docker), Spring Boot backend, and Angular frontend.
# ═══════════════════════════════════════════════════════════════════════

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# ── Colors ──
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

log()  { echo -e "${CYAN}[promptly]${NC} $1"; }
ok()   { echo -e "${GREEN}[promptly]${NC} $1"; }
warn() { echo -e "${YELLOW}[promptly]${NC} $1"; }
err()  { echo -e "${RED}[promptly]${NC} $1"; }

# ── Cleanup on exit ──
BACKEND_PID=""
FRONTEND_PID=""

cleanup() {
  echo ""
  log "Shutting down..."
  [[ -n "$FRONTEND_PID" ]] && kill "$FRONTEND_PID" 2>/dev/null && log "Stopped frontend (PID $FRONTEND_PID)"
  [[ -n "$BACKEND_PID" ]]  && kill "$BACKEND_PID"  2>/dev/null && log "Stopped backend  (PID $BACKEND_PID)"
  ok "All services stopped."
}
trap cleanup EXIT INT TERM

cd "$ROOT_DIR"

# ── Java ──────────────────────────────────────────────────────────────
# Project requires Java 21 — override JAVA_HOME if system default differs
JAVA21_HOME="/home/linuxbrew/.linuxbrew/opt/openjdk@21"
if [[ -d "$JAVA21_HOME" ]]; then
  export JAVA_HOME="$JAVA21_HOME"
  export PATH="$JAVA_HOME/bin:$PATH"
  ok "Using Java 21 from $JAVA_HOME"
else
  warn "Java 21 not found at $JAVA21_HOME — using system default: ${JAVA_HOME:-not set}"
fi

# ═══════════════════════════════════════════════════════════════════════
# 1. Infrastructure — MongoDB
# ═══════════════════════════════════════════════════════════════════════
MONGO_PORT=27017

if ss -tlnp 2>/dev/null | grep -q ":${MONGO_PORT} " || lsof -iTCP:"$MONGO_PORT" -sTCP:LISTEN &>/dev/null; then
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

npx nx serve promptly &
FRONTEND_PID=$!

ok "Frontend starting (PID $FRONTEND_PID) — http://localhost:4200"

# ═══════════════════════════════════════════════════════════════════════
# Wait
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

wait
