# Platform Overview

Welcome to **Promptly**, your enterprise-grade platform for AI Prompt Governance and Management. 

As Large Language Models (LLMs) become deeply integrated into production applications, treating prompts as "just strings in code" becomes a major liability. Promptly acts as a centralized **Control Plane** for all your AI interactions.

## Core Concepts

1. **Projects:** Everything in Promptly is scoped to a Project. A project maps to a specific environment (e.g., `Prod-Customer-Service-Bot`) or team.
2. **Prompts & Versions:** Prompts are treated like code. You create a prompt, and every change you make results in a strict, immutable Version (e.g., `v1.0.4`).
3. **Workflows:** You cannot simply push a prompt to production. Prompts must be submitted, reviewed by peers, and explicitly approved.
4. **Runtime Delivery:** Once approved, your production services retrieve the prompt dynamically from our fast Runtime API, meaning you can update prompts without redeploying your backend services!

## Navigation

* **Dashboard:** High-level metrics, showing how many prompts are currently active, in review, or rejected.
* **Registry:** The main view for all your prompt templates. You can search, filter by tags, and view the active deployed version.
* **Security Scans:** A log of all vulnerability checks executed against your prompts by our LLM guardrails.
* **Settings:** Manage project members, API keys for runtime delivery, and general application settings.
