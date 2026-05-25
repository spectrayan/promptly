# 🔍 Semantic Search — Deep Dive

The Semantic Search module provides embedding-based vector search for prompt discovery, similar prompt detection, and duplicate prevention using MongoDB Atlas Vector Search.

---

## How It Works

```mermaid
flowchart TD
    subgraph Indexing ["Automatic Indexing"]
        Event["PromptCreated /\nPromptUpdated"] --> Embed["Generate Embedding\n(Spring AI)"]
        Embed --> Store["Store Vector"]
    end

    subgraph Querying ["Search Flow"]
        Query["Search Query"] --> QEmbed["Generate Query Embedding"]
        QEmbed --> VSearch["Cosine Similarity Search"]
        VSearch --> Results["Ranked Results"]
    end

    style Indexing fill:#E3F2FD,color:#333,stroke:#1565C0
    style Querying fill:#E8F5E9,color:#333,stroke:#2E7D32
```

---

## Features

| Feature | Endpoint | Description |
|---------|----------|-------------|
| **Natural Language Search** | `GET /api/v1/search?q=...` | Find prompts by meaning, not keywords |
| **Similar Prompts** | `GET /api/v1/search/prompts/{id}/similar` | Discover related prompts |
| **Duplicate Detection** | `POST /api/v1/search/prompts/check-duplicates` | Prevent redundant prompts |

---

## Event-Driven Indexing

`EmbeddingOnPromptUpdatedListener` automatically reindexes when prompts change:

| Event | Action |
|-------|--------|
| `PromptCreated` | Generate and store embedding |
| `PromptUpdated` | Regenerate embedding |

---

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/search?q=...&projectId=X` | Semantic search |
| `GET` | `/api/v1/search/prompts/{id}/similar` | Find similar prompts |
| `POST` | `/api/v1/search/prompts/check-duplicates` | Check for duplicates |
