#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — Seed Database
# Loads seed-data/init.js into the running MongoDB container.
# Usage: ./scripts/seed.sh
# ═══════════════════════════════════════════════════════════════════════

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
SEED_FILE="$ROOT_DIR/seed-data/init.js"
CONTAINER="promptly-mongodb"
DB="test"

GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

if [[ ! -f "$SEED_FILE" ]]; then
  echo -e "${RED}[seed]${NC} Seed file not found: $SEED_FILE"
  exit 1
fi

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
  echo -e "${RED}[seed]${NC} Container '${CONTAINER}' is not running. Start it first with ./scripts/start-dev.sh"
  exit 1
fi

echo -e "${CYAN}[seed]${NC} Seeding database '${DB}' from ${SEED_FILE}..."
cat "$SEED_FILE" | docker exec -i "$CONTAINER" mongosh "$DB" --quiet

if [[ $? -eq 0 ]]; then
  echo -e "${GREEN}[seed]${NC} Database seeded successfully."
else
  echo -e "${RED}[seed]${NC} Seeding failed."
  exit 1
fi
