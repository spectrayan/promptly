# ===========================================================================
# Promptly — Nginx Performance Test Image
#
# Bundles the Angular production build + nginx config into a single image.
# Used by the K8s perf stack to serve static frontend files and proxy API.
#
# Build context: repo root
# Build: docker build -t promptly-nginx:perf -f apps/e2e/promptly-perf/nginx/Dockerfile .
# ===========================================================================

FROM nginx:alpine

# Copy nginx config (from K8s ConfigMap source-of-truth is inline,
# but this Dockerfile bakes the standalone config for Docker Compose fallback)
COPY apps/e2e/promptly-perf/nginx/nginx.conf /etc/nginx/nginx.conf

# Copy Angular production build output
COPY dist/promptly/browser /usr/share/nginx/html

EXPOSE 8080 4200

HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/health || exit 1
