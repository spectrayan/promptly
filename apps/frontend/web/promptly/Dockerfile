# ===========================================================================
# Promptly Frontend -- Multi-stage Production Dockerfile
# ===========================================================================
# Produces a lean Nginx image serving the Angular SPA.
#
# Build:  docker build -t promptly-frontend -f apps/frontend/web/promptly/Dockerfile .
# Run:    docker run -p 4200:80 promptly-frontend

# -- Stage 1: Build the Angular app -----------------------------------------
FROM node:22-alpine AS build

# Install pnpm
RUN corepack enable && corepack prepare pnpm@10.11.0 --activate

WORKDIR /app

# Copy dependency manifests first (better caching)
COPY package.json pnpm-lock.yaml .npmrc ./

# Install dependencies
RUN pnpm install --frozen-lockfile

# Copy entire workspace (Nx needs root context)
COPY . .

# Build the production bundle
RUN npx nx build promptly --configuration=production --skip-nx-cache

# -- Stage 2: Serve with Nginx -----------------------------------------------
FROM nginx:1.27-alpine AS runtime

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY apps/frontend/web/promptly/nginx.conf /etc/nginx/conf.d/promptly.conf

# Copy built assets from build stage
COPY --from=build /app/dist/promptly/browser /usr/share/nginx/html

# Security: non-root user
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=10s \
  CMD wget -qO- http://localhost:80/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
