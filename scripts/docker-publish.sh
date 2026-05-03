#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════
# Promptly — Docker Hub Publish Script
# ═══════════════════════════════════════════════════════════════════════
# Builds backend JAR + frontend bundle locally, then assembles and
# pushes the unified Docker image to Docker Hub.
#
# Prerequisites:
#   1. Docker CLI
#   2. docker login (run once: docker login -u spectrayan)
#   3. Maven installed (for backend JAR)
#   4. Node.js + pnpm installed (for frontend build)
#
# Usage:
#   ./scripts/docker-publish.sh                  # Build & push :latest
#   ./scripts/docker-publish.sh 1.0.0            # Build & push :1.0.0 + :latest
#   ./scripts/docker-publish.sh 1.0.0 --no-push  # Build only (dry-run)
# ═══════════════════════════════════════════════════════════════════════

set -euo pipefail

# ── Configuration ────────────────────────────────────────────────────
DOCKERHUB_NAMESPACE="spectrayan"
IMAGE="${DOCKERHUB_NAMESPACE}/promptly"
VERSION="${1:-latest}"
NO_PUSH="${2:-}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# ── Colors ───────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log()  { echo -e "${CYAN}[promptly]${NC} $*"; }
ok()   { echo -e "${GREEN}  ✓${NC} $*"; }
warn() { echo -e "${YELLOW}  ⚠${NC} $*"; }
err()  { echo -e "${RED}  ✗${NC} $*" >&2; }

# ── Pre-flight checks ───────────────────────────────────────────────
log "🐳 Promptly Docker Hub Publisher"
echo ""
log "Image:    ${IMAGE}"
log "Version:  ${VERSION}"
echo ""

if ! command -v docker &>/dev/null; then
    err "Docker CLI not found. Install Docker first."
    exit 1
fi

if ! command -v mvn &>/dev/null; then
    err "Maven not found. Install Maven first."
    exit 1
fi

if ! command -v npx &>/dev/null; then
    err "Node.js / npx not found. Install Node.js first."
    exit 1
fi

# ── Step 1: Build backend JAR ────────────────────────────────────────
log "📦 Building backend JAR..."
(cd "${PROJECT_ROOT}" && mvn package -pl apps/backend/core -am -DskipTests -B -q)
ok "Backend JAR built → apps/backend/core/target/"

# ── Step 2: Build frontend bundle ────────────────────────────────────
log "🎨 Building frontend bundle..."
(cd "${PROJECT_ROOT}" && npx nx build promptly --configuration=production --skip-nx-cache)
ok "Frontend built → dist/promptly/browser/"

# ── Step 3: Assemble Docker image ────────────────────────────────────
log "🔨 Assembling Docker image..."
IMAGE_TAGS="-t ${IMAGE}:${VERSION}"
if [ "${VERSION}" != "latest" ]; then
    IMAGE_TAGS="${IMAGE_TAGS} -t ${IMAGE}:latest"
fi

docker build \
    ${IMAGE_TAGS} \
    -f "${PROJECT_ROOT}/Dockerfile" \
    "${PROJECT_ROOT}"
ok "Image assembled: ${IMAGE}:${VERSION}"

# ── Step 4: Push to Docker Hub ───────────────────────────────────────
if [ "${NO_PUSH}" = "--no-push" ]; then
    warn "Skipping push (--no-push flag set)"
else
    log "🚀 Pushing to Docker Hub..."

    docker push "${IMAGE}:${VERSION}"
    ok "Pushed ${IMAGE}:${VERSION}"

    if [ "${VERSION}" != "latest" ]; then
        docker push "${IMAGE}:latest"
        ok "Pushed ${IMAGE}:latest"
    fi
fi

echo ""
log "✅ Done!"
echo ""
log "Pull:"
echo "  docker pull ${IMAGE}:${VERSION}"
echo ""
log "Run (standalone — MongoDB required separately):"
echo "  docker run -p 3000:3000 \\"
echo "    -e MONGODB_URI=mongodb://host.docker.internal:27017/promptly \\"
echo "    ${IMAGE}:${VERSION}"
echo ""
log "Run with Docker Compose:"
echo "  PROMPTLY_VERSION=${VERSION} docker compose -f docker-compose.prod.yml up -d"
