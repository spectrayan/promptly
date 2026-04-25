#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — Dev Stop Script
# Kills processes running on frontend (4200) and backend (8080) ports.
# ═══════════════════════════════════════════════════════════════════════

set -uo pipefail

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

# ── Kill a process tree (parent + all children) ──
kill_tree() {
  local pid=$1
  local children
  children=$(pgrep -P "$pid" 2>/dev/null || true)
  for child in $children; do
    kill_tree "$child"
  done
  kill "$pid" 2>/dev/null || true
}

# ── Kill processes on a given port ──
kill_port() {
  local port=$1
  local label=$2
  local pids=""

  if command -v lsof &>/dev/null; then
    pids=$(lsof -iTCP:"$port" -sTCP:LISTEN -t 2>/dev/null || true)
  elif command -v ss &>/dev/null; then
    pids=$(ss -tlnp "sport = :$port" 2>/dev/null | grep -oP 'pid=\K\d+' || true)
  elif command -v netstat &>/dev/null; then
    pids=$(netstat -tlnp 2>/dev/null | grep ":$port " | grep -oP '\d+(?=/)' || true)
  fi

  if [[ -z "$pids" ]]; then
    warn "${label} — nothing running on port ${port}."
  else
    for pid in $pids; do
      kill_tree "$pid"
      ok "${label} — killed PID ${pid} (port ${port})."
    done
  fi
}

echo -e "${BOLD}Stopping Promptly dev services...${NC}"
echo ""

kill_port 4200 "Frontend"
kill_port 8080 "Backend"

echo ""
ok "All services stopped."
