# 📋 Prompt Registry — Deep Dive

!!! tip "See also"
    For a user-guide view of the registry, see the [Prompt Registry](../user-guide/prompt-registry.md) page.

The Prompt Registry is Promptly's core module — the single source of truth for all AI prompt templates. Every prompt is an **aggregate root** with immutable versioning.

---

## Domain Model

```mermaid
classDiagram
    class Prompt {
        +String id
        +String projectId
        +String name
        +String description
        +String content
        +String[] tags
        +PromptStatus status
        +int version
        +PromptVersion[] versions
        +PromptFormat format
        +String createdBy
        +Instant createdAt
        +Instant updatedAt
    }

    class PromptVersion {
        +int versionNumber
        +String content
        +String changeDescription
        +String author
        +Instant createdAt
    }

    class PromptStatus {
        <<enumeration>>
        DRAFT
        IN_REVIEW
        APPROVED
        DEPLOYED
        ARCHIVED
    }

    Prompt "1" --> "*" PromptVersion : versions
    Prompt --> PromptStatus : status
```

---

## Event-Driven Integration

When a prompt is created or updated, domain events trigger downstream processing:

```mermaid
flowchart LR
    Prompt["Prompt Registry"] -->|"PromptCreated\nPromptUpdated"| Scanner["Scanner\n(auto-scan)"]
    Prompt -->|"PromptCreated\nPromptUpdated"| Search["Search\n(update embedding)"]
    Prompt -->|"PromptCreated\nPromptUpdated"| Audit["Audit\n(log event)"]
    Prompt -->|"PromptCreated\nPromptUpdated"| Notification["Notification\n(notify team)"]

    style Prompt fill:#2196F3,color:#fff,stroke:#1565C0
    style Scanner fill:#FF9800,color:#fff,stroke:#E65100
    style Search fill:#4CAF50,color:#fff,stroke:#388E3C
    style Audit fill:#9C27B0,color:#fff,stroke:#6A1B9A
    style Notification fill:#9E9E9E,color:#fff,stroke:#616161
```

---

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/prompts` | Create a new prompt |
| `GET` | `/api/v1/prompts` | List prompts (filtered, paginated) |
| `GET` | `/api/v1/prompts/{id}` | Get prompt detail |
| `PUT` | `/api/v1/prompts/{id}` | Update prompt (creates new version) |
| `DELETE` | `/api/v1/prompts/{id}` | Delete prompt |
| `GET` | `/api/v1/prompts/{id}/versions` | List all versions |
| `GET` | `/api/v1/prompts/{id}/versions/{v}` | Get specific version |
| `POST` | `/api/v1/prompts/{id}/rollback/{v}` | Rollback to version |

---

## Hexagonal Architecture

```
prompt/
├── domain/model/              # Prompt, PromptVersion (pure POJOs)
├── application/
│   ├── port/in/               # PromptUseCase
│   ├── port/out/              # PromptPersistencePort
│   ├── service/               # PromptApplicationService
│   └── listener/              # PromptStatusEventListener
└── infrastructure/
    ├── web/                   # REST controller
    └── persistence/           # MongoAdapter, Document, Mapper
```
