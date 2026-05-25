# 🔐 Auth & RBAC — Deep Dive

!!! tip "See also"
    For a comprehensive description of the RBAC design decisions, see [ADR-002: Project RBAC](../architecture/adrs/002-project-rbac.md).

Promptly supports **dual-mode authentication** (LOCAL + OIDC) and **project-scoped RBAC** with five roles designed for enterprise AI governance.

---

## Authentication Modes

```mermaid
flowchart LR
    subgraph LOCAL ["LOCAL Mode (Built-in)"]
        L1["POST /auth/login"] --> L2["Promptly JWT"]
    end
    subgraph OIDC ["OIDC Mode (Enterprise)"]
        O1["External IdP"] --> O2["IdP JWT"]
    end
    L2 --> R["Project Membership"]
    O2 --> R
    R --> ROLE["Role Resolved"]

    style LOCAL fill:#E3F2FD,color:#333,stroke:#1565C0
    style OIDC fill:#E8F5E9,color:#333,stroke:#2E7D32
    style ROLE fill:#FF9800,color:#fff,stroke:#E65100
```

---

## Role Matrix

| Action | Viewer | Author | Reviewer | Approver | Admin |
|--------|:------:|:------:|:--------:|:--------:|:-----:|
| View prompts | ✅ | ✅ | ✅ | ✅ | ✅ |
| Create / edit | — | ✅ | — | — | ✅ |
| Submit for review | — | ✅ | — | — | ✅ |
| Review & request changes | — | — | ✅ | ✅ | ✅ |
| Approve / reject | — | — | — | ✅ | ✅ |
| Trigger scans | — | ✅ | ✅ | ✅ | ✅ |
| Delete prompts | — | — | — | — | ✅ |
| Manage members | — | — | — | — | ✅ |

---

## REST API

### Authentication (LOCAL mode)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/register` | Register user |
| `POST` | `/api/v1/auth/login` | Login → JWT |
| `POST` | `/api/v1/auth/refresh` | Refresh JWT |
| `GET` | `/api/v1/auth/me` | Current user |
| `GET` | `/api/v1/users` | List users |

### Project Members

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/projects/{id}/members` | List members |
| `POST` | `/api/v1/projects/{id}/members` | Add member |
| `PUT` | `/api/v1/projects/{id}/members/{mid}` | Update role |
| `DELETE` | `/api/v1/projects/{id}/members/{mid}` | Remove member |

---

## Configuration

```yaml
promptly:
  auth:
    provider: local           # local | oidc
```
