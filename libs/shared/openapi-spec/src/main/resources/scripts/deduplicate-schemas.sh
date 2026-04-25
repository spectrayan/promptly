#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────────────
# deduplicate-schemas.sh
#
# Post-processes bundled OpenAPI YAML to:
#   Phase 1 — replace inline object schema $refs with canonical PascalCase names
#   Phase 2 — replace inlined enum properties with $ref pointers
#
# Usage: ./deduplicate-schemas.sh <path-to-bundled-openapi.yaml>
# ──────────────────────────────────────────────────────────────────────
set -euo pipefail

SPEC="${1:?Usage: deduplicate-schemas.sh <bundled-openapi.yaml>}"

if [[ ! -f "$SPEC" ]]; then
  echo "Bundled spec not found at: $SPEC — skipping deduplication"
  exit 0
fi

# ── Collect schema names ────────────────────────────────────────────
mapfile -t CANONICAL < <(grep -E '^\s{4}[A-Z]\w+:\s*$' "$SPEC" | sed 's/^\s*//' | sed 's/:\s*$//')
mapfile -t INLINE    < <(grep -E '^\s{4}[a-z]\w+:\s*$' "$SPEC" | sed 's/^\s*//' | sed 's/:\s*$//')

echo "Found ${#CANONICAL[@]} canonical schemas, ${#INLINE[@]} inline schemas"

# ── Phase 1: Match inline object schemas to canonical by property signature ──

get_property_sig() {
  local schema_name="$1"
  local file="$2"
  # Extract property names under the schema and sort them
  awk -v name="$schema_name" '
    BEGIN { found=0; in_props=0 }
    /^    [A-Za-z]/ && found && !in_props { exit }
    $0 ~ "^    " name ":$" { found=1; next }
    found && /^      properties:/ { in_props=1; next }
    in_props && /^        [a-zA-Z]/ { sub(/:.*/, "", $0); gsub(/^ +/, "", $0); props[NR]=$0 }
    in_props && /^      [a-zA-Z]/ && !/^        / { exit }
  END {
    n=asorti(props, sorted)
    for (i=1; i<=n; i++) { printf "%s%s", props[sorted[i]], (i<n?",":"") }
  }' "$file"
}

declare -A CANONICAL_SIGS
for schema in "${CANONICAL[@]}"; do
  sig=$(get_property_sig "$schema" "$SPEC")
  if [[ -n "$sig" ]]; then
    CANONICAL_SIGS["$sig"]="$schema"
  fi
done

REPLACE_COUNT=0
CONTENT=$(cat "$SPEC")

for inline in "${INLINE[@]}"; do
  sig=$(get_property_sig "$inline" "$SPEC")
  if [[ -n "$sig" && -n "${CANONICAL_SIGS[$sig]+_}" ]]; then
    canonical="${CANONICAL_SIGS[$sig]}"
    echo "  Mapping: $inline -> $canonical"
    # Replace all $ref pointers
    CONTENT=$(echo "$CONTENT" | sed "s|\"#/components/schemas/${inline}\"|\"#/components/schemas/${canonical}\"|g")
    # Remove the inline schema definition block
    CONTENT=$(echo "$CONTENT" | awk -v name="$inline" '
      BEGIN { skip=0 }
      /^    [A-Za-z]/ { if (skip) skip=0 }
      $0 ~ "^    " name ":$" { skip=1 }
      !skip { print }
    ')
    ((REPLACE_COUNT++)) || true
  fi
done

echo "Phase 1: removed $REPLACE_COUNT inline object schemas"

# ── Phase 2: Replace inlined enum properties with $ref pointers ──

# Build map: sorted enum values → canonical enum schema name
declare -A ENUM_SIGS

get_enum_sig() {
  local schema_name="$1"
  local file="$2"
  awk -v name="$schema_name" '
    BEGIN { found=0; in_enum=0 }
    $0 ~ "^    " name ":$" { found=1; next }
    found && /^      enum:/ { in_enum=1; next }
    in_enum && /^      - / { sub(/^      - /, ""); vals[NR]=$0 }
    in_enum && !/^      -/ && !/^$/ { exit }
    found && /^    [A-Za-z]/ && !/^      / { exit }
  END {
    n=asorti(vals, sorted)
    for (i=1; i<=n; i++) { printf "%s%s", vals[sorted[i]], (i<n?",":"") }
  }' "$file"
}

# Re-parse from current content
TMPFILE=$(mktemp)
echo "$CONTENT" > "$TMPFILE"

for schema in "${CANONICAL[@]}"; do
  sig=$(get_enum_sig "$schema" "$TMPFILE")
  if [[ -n "$sig" ]]; then
    ENUM_SIGS["$sig"]="$schema"
  fi
done

echo "Found ${#ENUM_SIGS[@]} canonical enum schemas"

# Phase 2 replacement: scan for inlined enums in property blocks and replace
# This is complex in bash, so we use a Python one-liner if available, otherwise skip
if command -v python3 &>/dev/null; then
  ENUM_JSON=$(python3 -c "
import json, sys
d = {}
$(for key in "${!ENUM_SIGS[@]}"; do echo "d['$key']='${ENUM_SIGS[$key]}'"; done)
print(json.dumps(d))
")
  
  python3 -c "
import re, json, sys

with open('$TMPFILE', 'r') as f:
    content = f.read()

enum_map = json.loads('$ENUM_JSON')
lines = content.split('\n')
output = []
i = 0
replaced = 0

while i < len(lines):
    line = lines[i]
    # Match a property line at 8-space indent
    m = re.match(r'^(\s{8})(\w+):\s*$', line)
    if m:
        prop_indent = m.group(1)
        prop_name = m.group(2)
        child_indent = prop_indent + '  '
        
        # Look ahead for inline enum
        j = i + 1
        enum_values = []
        has_enum = False
        prop_end = j
        
        while j < len(lines):
            nl = lines[j]
            if nl.startswith(child_indent) or nl.strip() == '':
                if re.match(rf'^{re.escape(child_indent)}enum:\s*$', nl):
                    has_enum = True
                if has_enum:
                    em = re.match(rf'^{re.escape(child_indent)}-\s+(\S+)', nl)
                    if em:
                        enum_values.append(em.group(1))
                prop_end = j
                j += 1
            else:
                break
        
        if has_enum and enum_values:
            key = ','.join(sorted(enum_values))
            if key in enum_map:
                canonical = enum_map[key]
                output.append(f'{prop_indent}{prop_name}:')
                output.append(f'{child_indent}\$ref: \"#/components/schemas/{canonical}\"')
                replaced += 1
                i = prop_end + 1
                continue
    
    output.append(line)
    i += 1

with open('$TMPFILE', 'w') as f:
    f.write('\n'.join(output))

print(f'Phase 2: replaced {replaced} inlined enum properties with \$ref pointers')
"
fi

cp "$TMPFILE" "$SPEC"
rm -f "$TMPFILE"

echo "Deduplication complete"
