# 📤 Export / Import — Deep Dive

The Export / Import module enables CI/CD-driven prompt deployment across environments (dev → staging → production).

---

## Cross-Environment Promotion

```mermaid
flowchart LR
    DEV["Promptly DEV"] -->|"Export API"| CICD["CI/CD Pipeline"]
    CICD -->|"Import API"| STG["Promptly STAGING"]
    CICD -->|"Import API"| PROD["Promptly PROD"]

    style DEV fill:#FF9800,color:#fff,stroke:#E65100
    style CICD fill:#9E9E9E,color:#fff,stroke:#616161
    style STG fill:#2196F3,color:#fff,stroke:#1565C0
    style PROD fill:#4CAF50,color:#fff,stroke:#388E3C
```

---

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/exchange/export/prompts/{id}` | Export a single prompt |
| `GET` | `/api/v1/exchange/export/project/{projectId}` | Export all prompts in a project |
| `POST` | `/api/v1/exchange/import` | Import a prompt bundle |

---

## GitHub Actions Example

```yaml
name: Promote Prompts
on: workflow_dispatch
jobs:
  promote:
    runs-on: ubuntu-latest
    steps:
      - name: Export from DEV
        run: |
          curl -H "Authorization: Bearer $DEV_TOKEN" \
            "$DEV_URL/api/v1/exchange/export/project/my-project" -o prompts.json
      - name: Import to PROD
        run: |
          curl -X POST -H "Authorization: Bearer $PROD_TOKEN" \
            -H "Content-Type: application/json" \
            -d @prompts.json "$PROD_URL/api/v1/exchange/import"
```
