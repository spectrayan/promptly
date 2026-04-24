# Seed Data

MongoDB seed data for the Promptly platform. Use for local development and E2E testing.

## Quick Start

```bash
# With local mongosh
mongosh promptly --file seed-data/init.js

# With Docker
docker exec -i promptly-mongo mongosh promptly < seed-data/init.js
```

## What's Included

| Collection     | Count | Description                                    |
|----------------|-------|------------------------------------------------|
| `prompts`      | 5     | Diverse prompts across 5 projects              |
| `workflows`    | 4     | Workflows in all states (PENDING, APPROVED, IN_REVIEW, REJECTED) |
| `scan_results` | 4     | Scan results (pass, warn)                      |
| `audit_logs`   | 10    | Full audit trail covering all entity operations |

## Data Relationships

- `wf-001` → `p-002` (SQL Query Generator → PRODUCTION promotion, PENDING)
- `wf-002` → `p-005` (Content Moderation → PRODUCTION, APPROVED)
- `wf-003` → `p-001` (Support Classifier → STAGING, IN_REVIEW)
- `wf-004` → `p-003` (Code Review → STAGING, REJECTED)

## Reset

Running `init.js` always drops and re-creates all collections (idempotent).
