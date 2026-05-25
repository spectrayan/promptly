# 🚀 Runtime Delivery — Deep Dive

The Runtime Delivery API provides low-latency prompt retrieval for AI agents and backend services at runtime.

---

## Delivery Flow

```mermaid
flowchart LR
    Agent["AI Agent"] -->|"GET /deliver\n?appId=X&usecase=Y"| API["Delivery API"]
    API --> Lookup["Resolve Prompt\nby Parameters"]
    Lookup --> Return["Return Latest\nDeployed Version"]

    style Agent fill:#9E9E9E,color:#fff,stroke:#616161
    style API fill:#2196F3,color:#fff,stroke:#1565C0
    style Return fill:#4CAF50,color:#fff,stroke:#388E3C
```

---

## Query Parameters

| Parameter | Required | Description | Example |
|-----------|:--------:|-------------|---------|
| `appId` | ✅ | Application identifier | `customer-service` |
| `usecase` | ✅ | Use case within the app | `greeting` |
| `agent` | ❌ | Specific agent name | `support-bot` |

---

## Example

```bash
curl -H "Authorization: Bearer <TOKEN>" \
  "https://promptly.example.com/api/v1/deliver?appId=customer-service&usecase=greeting"
```

```json
{
  "id": "p-cs-greeting",
  "name": "Customer Service Greeting",
  "content": "You are a helpful customer service assistant...",
  "version": 3,
  "status": "DEPLOYED"
}
```

---

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/deliver` | Fetch deployed prompt by `appId`, `usecase`, and optional `agent` |
