# ADR-004: Spring Modulith — Module Boundaries & Dependency Rules

**Status:** Accepted  
**Date:** 2026-04-26  
**Authors:** Spectrayan Team

---

## Context

Promptly's backend is a single Spring Boot 4 application containing 12+ bounded contexts (Prompt, Workflow, Auth, Project, Notification, LLM Config, Scanner, Improver, Search, Delivery, Audit, Export). Without explicit boundary enforcement, cross-module dependencies degrade into a tangle where every module reaches into every other module's internals.

We needed a mechanism to:
1. Declare and enforce which modules may depend on which
2. Allow shared infrastructure without creating a monolithic "commons" module
3. Support fine-grained sub-module access (e.g., expose only domain models)

## Decision

Use **Spring Modulith** annotations on `package-info.java` to declare explicit module boundaries and enforce them via architecture tests.

### Module Dependency Map

```mermaid
graph TB
    subgraph Open ["Open Modules (shared infrastructure)"]
        SHARED["shared"]
        INFRA["infrastructure"]
    end

    subgraph Core ["Core Domain Modules"]
        AUTH["auth"]
        PROJECT["project"]
        PROMPT["prompt"]
        WORKFLOW["workflow"]
    end

    subgraph AI ["AI Feature Modules"]
        SCANNER["scanner"]
        IMPROVER["improver"]
        SEARCH["search"]
    end

    subgraph Platform ["Platform Modules"]
        LLMCONFIG["llmconfig"]
        NOTIFICATION["notification"]
        DELIVERY["delivery"]
        AUDIT["audit"]
        EXPORT["export"]
    end

    AUTH --> SHARED
    AUTH --> INFRA
    PROJECT --> SHARED
    PROMPT --> SHARED & INFRA & PROJECT
    WORKFLOW --> SHARED & INFRA
    SCANNER --> SHARED & INFRA & PROMPT
    IMPROVER --> SHARED & INFRA & PROMPT
    SEARCH --> SHARED & INFRA & PROMPT
    DELIVERY --> SHARED & INFRA & PROMPT
    LLMCONFIG --> SHARED
    NOTIFICATION --> SHARED & PROJECT
    AUDIT --> SHARED & INFRA & PROMPT & WORKFLOW & SCANNER
    EXPORT --> SHARED & INFRA & PROMPT & WORKFLOW & SCANNER

    style SHARED fill:#FFF3E0,color:#333,stroke:#E65100
    style INFRA fill:#FFF3E0,color:#333,stroke:#E65100
    style PROMPT fill:#E3F2FD,color:#333,stroke:#1565C0
    style WORKFLOW fill:#E3F2FD,color:#333,stroke:#1565C0
    style AUTH fill:#E8F5E9,color:#333,stroke:#2E7D32
    style PROJECT fill:#E8F5E9,color:#333,stroke:#2E7D32
```

### Module Types

| Module | Type | `allowedDependencies` |
|--------|------|-----------------------|
| `shared` | `OPEN` | — (visible to all) |
| `infrastructure` | `OPEN` | — (visible to all) |
| `auth` | Standard | `shared`, `infrastructure` |
| `project` | Standard | (none declared — relies on shared) |
| `prompt` | Standard | `shared`, `infrastructure`, `project` |
| `workflow` | Standard | `shared`, `infrastructure` |
| `scanner` | Standard | `shared`, `infrastructure`, `prompt`, `prompt::domain-model` |
| `improver` | Standard | `shared`, `infrastructure`, `prompt`, `prompt::domain-model` |
| `search` | Standard | `shared`, `infrastructure`, `prompt`, `prompt::domain-model` |
| `delivery` | Standard | `shared`, `infrastructure`, `prompt`, `prompt::domain-model` |
| `llmconfig` | Standard | `shared`, `shared::config` |
| `notification` | Standard | `shared`, `project` |
| `audit` | Standard | `shared`, `infrastructure`, `prompt`, `workflow`, `scanner` |
| `export` | Standard | `shared`, `infrastructure`, `shared::config`, `prompt`, `workflow`, `scanner` |

### Named Interfaces

The `prompt` module exposes a `NamedInterface("domain-model")` on its `domain.model` package, allowing downstream modules (scanner, improver, search, delivery) to reference `Prompt` and `PromptVersion` domain objects without accessing the full module.

```mermaid
graph LR
    subgraph prompt ["prompt module"]
        API["Public API (default)"]
        DM["domain-model (NamedInterface)"]
    end

    SCANNER["scanner"] --> DM
    IMPROVER["improver"] --> DM
    SEARCH["search"] --> DM
    DELIVERY["delivery"] --> DM

    style DM fill:#E3F2FD,color:#333,stroke:#1565C0
    style API fill:#90CAF9,color:#333,stroke:#1565C0
```

### Inter-Module Communication

Modules communicate via **Spring Modulith application events**, not direct method calls across boundaries:

```java
// Prompt module publishes:
public record PromptCreated(String promptId, String projectId) {}
public record PromptUpdated(String promptId, int version) {}

// Audit module listens:
@ApplicationModuleListener
void on(PromptCreated event) { /* create audit entry */ }
```

## Consequences

### Positive
- Compilation-time dependency validation via `@ApplicationModuleTest`
- Clear visual map of what depends on what
- New developers can read `package-info.java` files to understand module boundaries

### Negative
- Adding a new cross-module dependency requires editing `package-info.java` — intentional friction
- Some test failures are caused by Modulith context loading, not actual bugs (known issue)
