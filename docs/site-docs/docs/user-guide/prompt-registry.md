# Prompt Registry

The **Prompt Registry** is the core of Promptly. It acts as the version control system for your AI prompts.

## Managing Prompts

When you navigate to the Registry, you will see a list of all prompt templates available in the current project.

### 1. Creating a New Prompt
Click "New Prompt" to open the creation dialog. You will need to provide:
* **Name:** A unique identifier for the prompt (e.g., `customer-support-greeting`).
* **Content:** The actual prompt text. You can use Handlebars-style variables like `{{user_name}}` which the Runtime API will interpolate.
* **Model Configuration:** Select the target LLM (e.g., GPT-4, Claude 3, Gemini 1.5 Pro) and configure parameters like `temperature` and `max_tokens`.

### 2. Versioning
Every time you make a change to a prompt, a new immutable version is created. This allows you to safely rollback if a prompt causes regressions in production.
* Example: Version `1` is deployed. You create a draft for Version `2`. Once Version `2` is approved and deployed, Version `1` is archived but preserved in the history.

### 3. Editor Experience
The registry includes an integrated Monaco Editor (the same editor powering VS Code). It provides:
* Syntax highlighting for Handlebars variables.
* Auto-completion for known parameters.
* Inline linting for potential issues (e.g., unbalanced brackets).

## Runtime Delivery

Once a prompt version is in the `DEPLOYED` state, it is immediately available to your backend services via the Promptly Runtime API:

```http
GET /api/v1/runtime/prompts/customer-support-greeting
Authorization: Bearer <PROJECT_API_KEY>
```

The API uses a blazing-fast Redis cache, ensuring your application never waits more than a few milliseconds to retrieve the latest, safest prompt version.
