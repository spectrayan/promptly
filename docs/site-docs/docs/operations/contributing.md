# Contributing to Promptly

Thank you for your interest in contributing to Promptly! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Making Changes](#making-changes)
- [Coding Standards](#coding-standards)
- [Pull Request Process](#pull-request-process)
- [Reporting Issues](#reporting-issues)

## Code of Conduct

This project adheres to the [Contributor Covenant Code of Conduct](https://github.com/spectrayan/promptly/blob/main/CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to [support@spectrayan.com](mailto:support@spectrayan.com).

## Getting Started

1. **Fork** the repository on GitHub
2. **Clone** your fork locally
3. **Create a branch** for your change
4. **Make your changes** with appropriate tests
5. **Submit a pull request**

## Development Setup

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ (JDK) |
| Node.js | 22+ |
| pnpm | 10+ |
| Maven | 3.9+ |
| Docker | Latest |

### First-Time Setup

```bash
# Clone your fork
git clone https://github.com/<your-username>/promptly.git
cd promptly

# Install frontend dependencies
pnpm install

# Start MongoDB (Atlas Local with vector search)
docker compose up -d

# Seed the database
docker exec -i promptly-mongodb mongosh promptly < seed-data/mongodb/init.js

# Generate API code from OpenAPI spec
pnpm run build:openapi

# Start the platform
pnpm run start:all
```

### Running Individually

```bash
# Backend only (Spring Boot on :8080)
pnpm run start:backend

# Frontend only (Angular on :4200)
pnpm run start:frontend
```

### Running Tests

```bash
# Backend unit tests
cd apps/backend/core && mvn test

# Frontend unit tests
cd apps/frontend/web/promptly && npx ng test --watch=false
```

## Making Changes

### Branch Naming

Use descriptive branch names with a type prefix:

```
feat/add-notification-preferences
fix/sse-reconnect-race-condition
refactor/config-properties-hierarchy
docs/contributing-guide
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat(backend): add notification count endpoint
fix(frontend): gate SSE connection behind auth state
refactor(config): consolidate @Value into PromptlyProperties
docs: add contributing guide
chore: update gitignore
```

**Format:** `<type>(<scope>): <description>`

| Type | Purpose |
|------|---------|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code restructuring (no behavior change) |
| `docs` | Documentation only |
| `test` | Adding or updating tests |
| `chore` | Build, CI, tooling changes |
| `perf` | Performance improvement |

## Coding Standards

### Backend (Java / Spring)

- **Java 21** — use records, sealed classes, pattern matching where appropriate
- **Spring Modulith** — respect module boundaries; modules communicate via events only
- **Hexagonal architecture** — domain core must be framework-free POJOs
- **Reactive** — use `Mono`/`Flux` throughout; no blocking calls
- **Configuration** — use `PromptlyProperties` hierarchy, never `@Value`
- **API-first** — changes to REST endpoints start in the OpenAPI spec, then regenerate
- **Testing** — new features require unit tests; use `@WebFluxTest` for controllers

### Frontend (Angular / TypeScript)

- **Angular 18+** — standalone components, signals, new control flow
- **NgRx** — all state management via store, effects, and facades
- **Material 3** — use Angular Material components with the design system
- **TypeScript strict mode** — no `any` types, proper null checks
- **Generated SDK** — use `@promptly/client` for API calls, never raw `HttpClient`

### API Changes

If your change modifies a REST API:

1. Update the OpenAPI spec in `libs/shared/openapi-spec/src/main/resources/openapi/`
2. Run `pnpm run build:openapi` to regenerate server interfaces and client SDKs
3. Implement the generated interface in your controller
4. Update any affected SDK consumers

## Pull Request Process

1. **Ensure your branch is up to date** with `main`
2. **All tests pass** — CI will verify this automatically
3. **Fill out the PR template** — describe what changed and why
4. **Link related issues** — use `Closes #123` or `Fixes #456`
5. **One approval required** — a maintainer will review your PR
6. **Squash merge** — PRs are squash-merged to keep history clean

### PR Checklist

- [ ] Code follows the project's coding standards
- [ ] Tests added/updated for the change
- [ ] Documentation updated if needed
- [ ] OpenAPI spec updated if API changed
- [ ] No hardcoded secrets or credentials
- [ ] Commit messages follow Conventional Commits

## Reporting Issues

### Bug Reports

Use the [Bug Report template](https://github.com/spectrayan/promptly/issues/new?template=bug_report.md) and include:

- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, Java version, browser)
- Relevant logs or screenshots

### Feature Requests

Use the [Feature Request template](https://github.com/spectrayan/promptly/issues/new?template=feature_request.md) and describe:

- The problem you're trying to solve
- Your proposed solution
- Any alternatives you've considered

## Questions?

- **General questions:** Open a [Discussion](https://github.com/spectrayan/promptly/discussions)
- **Bug reports:** Open an [Issue](https://github.com/spectrayan/promptly/issues)
- **Security vulnerabilities:** See [Security Policy](security.md)
- **Email:** [developer@spectrayan.com](mailto:developer@spectrayan.com)

---

Thank you for contributing to Promptly! 🚀
