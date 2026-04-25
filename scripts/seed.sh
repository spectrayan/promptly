#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — Seed Database
# Imports per-collection JSON files via mongoimport then creates indexes.
# Usage: ./scripts/seed.sh
# ═══════════════════════════════════════════════════════════════════════

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
SEED_DIR="$ROOT_DIR/seed-data"
CONTAINER="promptly-mongodb"
DB="test"

GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m'

log()  { echo -e "${CYAN}[seed]${NC} $1"; }
ok()   { echo -e "${GREEN}[seed]${NC} $1"; }
warn() { echo -e "${YELLOW}[seed]${NC} $1"; }
err()  { echo -e "${RED}[seed]${NC} $1"; }

# Detect container
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
  CONTAINER="mongo-vector"
  if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
    err "No MongoDB container found (tried promptly-mongodb, mongo-vector)."
    exit 1
  fi
fi

log "Seeding database '${DB}' (container: $CONTAINER)..."
echo ""

# ── Collection → JSON file mapping ──
declare -A COLLECTIONS=(
  [users]="users/users.json"
  [projects]="projects/projects.json"
  [project_members]="project_members/project_members.json"
  [prompts]="prompts/prompts.json"
  [workflows]="workflows/workflows.json"
  [scan_results]="scan_results/scan_results.json"
  [audit_logs]="audit_logs/audit_logs.json"
)

ORDER=(users projects project_members prompts workflows scan_results audit_logs)

for collection in "${ORDER[@]}"; do
  json_file="${COLLECTIONS[$collection]}"
  full_path="$SEED_DIR/$json_file"

  if [[ ! -f "$full_path" ]]; then
    warn "Skipping $collection — $json_file not found"
    continue
  fi

  # Drop existing collection
  docker exec -i "$CONTAINER" mongosh "$DB" --quiet --eval "db.$collection.drop()" > /dev/null 2>&1 || true

  # Import via mongoimport
  docker exec -i "$CONTAINER" mongoimport \
    --db "$DB" \
    --collection "$collection" \
    --jsonArray \
    --quiet \
    < "$full_path"

  count=$(docker exec -i "$CONTAINER" mongosh "$DB" --quiet --eval "db.$collection.countDocuments()")
  ok "$collection: $count documents"
done

echo ""

# ── Create indexes ──
log "Creating indexes..."
cat "$SEED_DIR/init.js" | docker exec -i "$CONTAINER" mongosh "$DB" --quiet

echo ""
ok "Done!"
