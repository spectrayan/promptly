# 🔔 Real-Time Notifications — Deep Dive

!!! tip "See also"
    For the SSE architecture decision, see [ADR-009: SSE Notifications](../architecture/adrs/009-sse-notifications.md).

The Notifications module delivers per-user, real-time notifications via Server-Sent Events (SSE).

---

## Architecture

```mermaid
flowchart LR
    Events["Domain Events"] --> Listener["NotificationEventListener"]
    Listener --> Persist["Create & Persist"]
    Persist --> SSE["Push via SSE"]
    SSE --> Browser["Browser Toast"]

    style Listener fill:#FF9800,color:#fff,stroke:#E65100
    style SSE fill:#2196F3,color:#fff,stroke:#1565C0
    style Browser fill:#4CAF50,color:#fff,stroke:#388E3C
```

---

## Notification Types

| Source | Notification | Recipients |
|--------|-------------|------------|
| **Workflow** | Prompt submitted for review | Reviewers & Approvers |
| **Workflow** | Prompt approved / rejected | Prompt Author |
| **Scanner** | Scan completed | Prompt Author |
| **Project** | New member added | Project Admins |

---

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/notifications` | List notifications (paginated) |
| `GET` | `/api/v1/notifications/count` | Unread count |
| `PATCH` | `/api/v1/notifications/{id}` | Mark as read |
| `POST` | `/api/v1/notifications/mark-all-read` | Mark all as read |
| `GET` | `/api/v1/notifications/preferences` | Get preferences |
| `PUT` | `/api/v1/notifications/preferences` | Update preferences |
| `GET` | `/api/v1/notifications/project-settings/{pid}` | Project settings |
| `PUT` | `/api/v1/notifications/project-settings/{pid}` | Update project settings |

---

## SSE Streaming

The frontend establishes a persistent SSE connection. The backend uses Spring WebFlux's `Flux<ServerSentEvent>` for non-blocking, per-user event streaming.
