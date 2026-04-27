# Architecture Decision Records — Index

**Project:** Promptly  
**Maintained by:** Spectrayan Team

---

This directory contains Architecture Decision Records (ADRs) documenting the
key technical decisions made during the design and implementation of the
Promptly AI Prompt Governance Platform.

## ADR Template Standard

All ADRs follow a consistent format:
- **Authors** are always **"Spectrayan Team"**
- **All diagrams** use Mermaid (no ASCII art)
- **Status** is one of: `Proposed`, `Accepted`, `Deprecated`, `Superseded`

## Index

| # | Title | Status | Key Topic |
|---|-------|--------|-----------|
| [001](001-hybrid-state-management.md) | Hybrid State Management Architecture | Accepted | NgRx vs. Signals, Facade pattern |
| [002](002-project-rbac-model.md) | Project Organization & RBAC | Accepted | Multi-tenant roles, approval workflow |
| [003](003-hexagonal-naming-conventions.md) | Hexagonal Naming Conventions | Accepted | Package structure, DDD layers |
| [004](004-spring-modulith-boundaries.md) | Spring Modulith — Module Boundaries | Accepted | Module dependency map, named interfaces |
| [005](005-system-prompt-administration.md) | System Prompt Administration | Accepted | `__system__` project, seeder pattern |
| [006](006-contract-first-api-design.md) | Contract-First API Design | Accepted | OpenAPI codegen, SDK generation |
| [007](007-specification-pattern.md) | Specification Pattern for Business Rules | Accepted | Domain validation, rule composition |
| [008](008-reactive-persistence.md) | Reactive Persistence with WebFlux & MongoDB | Accepted | Non-blocking I/O, document mapping |
| [009](009-sse-notifications.md) | Real-Time Notifications via SSE | Accepted | Server-Sent Events, NgRx integration |

## High-Level Architecture

```mermaid
graph TB
    subgraph Frontend ["Angular 21 Frontend"]
        UI["Pages & Components"]
        FAC["Facades (10)"]
        NGRX["NgRx Store"]
        SIG["Signals"]
        SDK["@promptly/client SDK"]
    end

    subgraph Backend ["Spring Boot 4 Backend"]
        WEB["WebFlux Controllers"]
        APP["Application Services"]
        DOM["Domain Aggregates"]
        PORTS["Ports (in/out)"]
        ADAPT["Infrastructure Adapters"]
    end

    subgraph Data ["Data Layer"]
        MONGO[(MongoDB)]
        LLM["LLM APIs\n(Gemini / OpenAI)"]
    end

    UI --> FAC
    FAC --> NGRX & SIG
    NGRX --> SDK
    SIG --> SDK
    SDK -->|"HTTP"| WEB
    WEB --> APP
    APP --> DOM
    APP --> PORTS
    PORTS -.->|"implemented by"| ADAPT
    ADAPT --> MONGO
    ADAPT --> LLM

    style UI fill:#9E9E9E,color:#fff,stroke:#616161
    style FAC fill:#FF9800,color:#fff,stroke:#E65100
    style NGRX fill:#4CAF50,color:#fff,stroke:#388E3C
    style SDK fill:#7B1FA2,color:#fff,stroke:#4A148C
    style WEB fill:#2196F3,color:#fff,stroke:#1565C0
    style DOM fill:#E3F2FD,stroke:#1565C0
    style MONGO fill:#9C27B0,color:#fff,stroke:#6A1B9A
```
