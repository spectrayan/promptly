# Promptly Platform — Current State & What's Next

> Resumed from conversation `33c302ed` (April 23, 2026)

---

## ✅ What's Already Built

### Backend (135 Java source files, 10 modules)

| Module | Status | Key Features |
|--------|--------|-------------|
| **shared** | ✅ | AggregateRoot, DomainEvent, Specification, Security, Mongo, CORS, OpenAPI, Async configs |
| **auth** | ✅ | JWT auth, login/register, dual-mode LOCAL/OIDC, user management |
| **project** | ✅ | CRUD, membership, RBAC roles (VIEWER→ADMIN), authorization service |
| **prompt** | ✅ | CRUD + versioning + rollback, PromptModuleApi (cross-module), events |
| **workflow** | ✅ | Multi-step approval state machine (submit→review→approve/reject) |
| **scanner** | ✅ | LLM vulnerability scan via Spring AI, auto-triggered on prompt events |
| **improver** | ✅ | LLM-powered prompt improvement with generate + apply |
| **delivery** | ✅ | Runtime prompt fetching by appId/usecase for AI agents |
| **audit** | ✅ | Central event listener consuming ALL domain events → immutable log |
| **search** | ✅ | Semantic search with embedding, similar prompts, duplicate detection |

### Frontend (53 TypeScript files, 7 feature modules)

| Feature | Status | Key Components |
|---------|--------|---------------|
| **auth** | ✅ | Login/register pages, auth guard, JWT session |
| **dashboard** | ✅ | Personalized greeting, project-aware stats, gradient icons |
| **prompts** | ✅ | List, detail, full-page create with AI assist, version diff viewer |
| **workflows** | ✅ | Workflow list and detail pages |
| **scanner** | ✅ | Scan results page |
| **search** | ✅ | Semantic search page |
| **audit** | ✅ | Audit log viewer |

### Infrastructure
| Item | Status |
|------|--------|
| Docker Compose (MongoDB + Redis) | ✅ |
| OpenAPI spec (split into versioned files) | ✅ |
| Angular SDK generated from spec | ✅ |
| Java API interfaces generated from spec | ✅ |
| Mock API interceptor for frontend dev | ✅ |
| MongoDB seed data (users, projects, prompts, workflows, scans, audit) | ✅ |
| M3 Material tokens (dark/light theme toggle) | ✅ |
| GCP-style project selector in top navbar | ✅ |
| Sidebar collapse/expand with chevron at bottom | ✅ |
| User avatar + menu in top-right navbar | ✅ |

---

## 🔲 What's Remaining (from the Implementation Plan)

### Phase 2 — Missing Items
- [ ] **Monaco Editor** integration for prompt editing (currently using textarea)
- [ ] **Environment promotion** flow (dev→staging→prod) in the workflow module
- [ ] **AOP-based audit** capture (currently using manual event listeners)

### Phase 3 — Missing Items
- [ ] **Redis cache** for Runtime Delivery API (DeliveryApplicationService currently hits MongoDB directly)
- [ ] **LLM provider management** — backend CRUD for `llm_configs` collection
- [ ] **Configurable Spring AI** — switch providers at runtime based on `llm_configs`

### Phase 4 — Missing Items
- [ ] **Notification system** (email/Slack webhooks for workflow events)
- [ ] **Audit log export** (CSV/JSON for SOC2/HIPAA compliance)
- [ ] **API key management** for runtime delivery
- [ ] **LLM provider management UI** in settings
- [ ] **E2E testing** (backend integration tests + Cypress/Playwright)
- [ ] **Production Docker setup** (multi-stage builds, nginx config)
- [ ] **Deployment docs**

### General Polish
- [ ] The `Specification.java` pattern (file currently open) — not yet used in any domain logic
- [ ] MapStruct warnings (unmapped target properties in Workflow, ScanResult, Audit mappers)
- [ ] Bundle size optimization (budget warning on build)

---

## 💡 Suggested Next Steps (Priority Order)

1. **Monaco Editor** — Biggest UX gap. The prompt editor should use Monaco for syntax highlighting, line numbers, and a professional editing experience.
2. **Redis cache for Delivery** — Important for production performance.
3. **LLM Config CRUD** — Enable runtime LLM provider switching.
4. **Settings pages** — LLM config + API key management UI.
5. **E2E tests** — Backend integration tests at minimum.
