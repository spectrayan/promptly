#!/usr/bin/env bash
# ──────────────────────────────────────────────
# Promptly — MongoDB Seed Data Loader
# Imports per-collection JSON files via mongoimport
# then creates indexes via mongosh.
#
# Usage:  ./seed-data/seed.sh
# ──────────────────────────────────────────────
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SEED_DIR="$SCRIPT_DIR"

# ── Configuration ──────────────────────────────
CONTAINER="mongo-vector"
DB="promptly"

# Detect container name (fallback to promptly-mongodb)
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
  CONTAINER="promptly-mongodb"
  if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
    echo "❌ No MongoDB container found (tried mongo-vector, promptly-mongodb)"
    exit 1
  fi
fi

echo "🌱 Seeding Promptly database (container: $CONTAINER, db: $DB)..."
echo ""

# ── Collection → JSON file mapping ──────────────
declare -A COLLECTIONS=(
  [users]="users/users.json"
  [projects]="projects/projects.json"
  [project_members]="project_members/project_members.json"
  [prompts]="prompts/prompts.json"
  [prompt_history]="prompt_history/prompt_history.json"
  [workflows]="workflows/workflows.json"
  [workflow_steps]="workflow_steps/workflow_steps.json"
  [scan_results]="scan_results/scan_results.json"
  [audit_logs]="audit_logs/audit_logs.json"
  [notifications]="notifications/notifications.json"
  [notification_project_settings]="notification_project_settings/notification_project_settings.json"
)

# ── Import each collection ──────────────────────
for collection in users projects project_members prompts prompt_history workflows workflow_steps scan_results audit_logs notifications notification_project_settings; do
  json_file="${COLLECTIONS[$collection]}"
  full_path="$SEED_DIR/$json_file"

  if [[ ! -f "$full_path" ]]; then
    echo "  ⚠️  Skipping $collection — $json_file not found"
    continue
  fi

  # Drop existing collection
  docker exec -i "$CONTAINER" mongosh "$DB" --quiet --eval "db.$collection.drop()" > /dev/null 2>&1 || true

  # Import via mongoimport (available in the atlas-local image)
  docker exec -i "$CONTAINER" mongoimport \
    --db "$DB" \
    --collection "$collection" \
    --jsonArray \
    --quiet \
    < "$full_path"

  count=$(docker exec -i "$CONTAINER" mongosh "$DB" --quiet --eval "db.$collection.countDocuments()")
  echo "  ✅ $collection: $count documents"
done

echo ""

# ── Create indexes ──────────────────────────────
echo "📇 Creating indexes..."
cat "$SEED_DIR/init.js" | docker exec -i "$CONTAINER" mongosh "$DB" --quiet

echo ""
echo "🎉 Done!"
