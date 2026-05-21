# Frequently Asked Questions

Common questions about Promptly, answered for both business and technical audiences.

---

## General

### What is Promptly?
Promptly is an open-source platform for managing, securing, and governing the AI prompts that power your organization's AI agents and applications. Think of it as version control + approval workflows + security scanning — purpose-built for AI prompts.

### Is Promptly free?
Yes. Promptly is open source under the **Apache License 2.0**. You can use, modify, and deploy it without licensing fees.

### Do I need to be a developer to use Promptly?
No. Promptly is designed so that product managers, compliance officers, and business users can author prompts, review changes, and monitor AI behavior through a web interface. Developers set up the initial deployment and integrations.

### What AI models does Promptly support?
Promptly supports multiple LLM providers out of the box: **OpenAI** (GPT-4, etc.), **Google Gemini**, **Anthropic** (Claude), and **Ollama** (for local/self-hosted models). You can switch providers via configuration — no code changes required.

---

## Security & Compliance

### How does the vulnerability scanner work?
When a prompt is created or edited, Promptly's AI-powered scanner analyzes it for common risks: prompt injection vulnerabilities, PII/PHI data exposure, missing safety guardrails, and toxicity concerns. Critical findings block the prompt from being submitted for review.

### Is Promptly compliant with SOC 2 / HIPAA?
Promptly provides the tooling that supports compliance — immutable audit logs, role-based access control, approval workflows, and security scanning. Whether your deployment meets a specific standard depends on your overall infrastructure and organizational controls. Promptly gives you the evidence trail auditors look for.

### Can I self-host Promptly?
Yes. Promptly is designed to be fully self-hosted. Run it on your own servers, your own cloud, or behind your firewall. Your prompt data never leaves your network unless you choose to use a cloud LLM provider for scanning/improvement features.

### Does Promptly send my prompts to external AI services?
Only when you explicitly use the AI-powered features (vulnerability scanning and quality improvement). These features call the configured LLM provider. If data sovereignty is a concern, configure Promptly to use **Ollama** with a locally-hosted model — all processing stays on your infrastructure.

---

## Deployment & Integration

### How do my AI agents get prompts from Promptly?
Your agents call the **Runtime Delivery API**: `GET /api/v1/deliver?appId=X&usecase=Y&agent=Z`. Promptly returns the latest approved and deployed prompt version. SDKs are available for **TypeScript**, **Python**, and **Java** to simplify integration.

### Can I use Promptly in my CI/CD pipeline?
Yes. Promptly's **Export/Import APIs** are designed for CI/CD integration. Export prompts from your development instance as a JSON bundle, then import them into staging or production instances. Works with GitHub Actions, GitLab CI, Jenkins, and any pipeline that can make HTTP calls.

### What databases does Promptly support?
Promptly supports **MongoDB** (default, with Atlas Vector Search), **PostgreSQL** (with pgvector), and **H2** (embedded, for local development and demos). Switch between them using Maven profiles and Spring configuration.

### How do I update prompts without downtime?
That's one of Promptly's core features. Author and approve a new prompt version through the web UI. Once deployed, your AI agents automatically receive the updated prompt on their next Runtime API call — no application redeployment needed.

---

## Team & Workflow

### How does the approval workflow work?
Prompts follow a structured lifecycle: **Draft → In Review → Approved → Deployed**. An author creates or edits a prompt (Draft), submits it for review, and a designated Reviewer approves or rejects it. Only approved prompts can be deployed to production.

### Can I skip the approval workflow?
Project Admins have the ability to bypass the workflow for emergency situations. However, all actions — including bypasses — are recorded in the immutable audit trail for accountability.

### How many projects can I create?
There is no limit. Create as many projects as you need to organize prompts by product, team, environment, or any structure that works for your organization.

### Who gets notified when a prompt needs review?
Promptly uses real-time notifications (Server-Sent Events) to alert Reviewers and Admins when a prompt is submitted for review. Notifications appear in the app's notification bell.

---

## Technical

### What tech stack does Promptly use?
- **Frontend:** Angular 21 · TypeScript · Angular Material 3 · NgRx
- **Backend:** Java 21 · Spring Boot 4.0 · Spring WebFlux (reactive)
- **AI:** Spring AI (multi-provider support)
- **Build:** Nx monorepo · Maven · pnpm
- **Containers:** Docker & Docker Compose

### Is Promptly a microservices architecture?
No. Promptly uses a **modular monolith** architecture powered by Spring Modulith. This provides the organizational benefits of microservices (clear module boundaries, independent domain models, event-driven communication) with the operational simplicity of a single deployable unit.

### Can I contribute to Promptly?
Absolutely! Promptly welcomes contributions — bug reports, feature requests, documentation improvements, and code. See the [Contributing](../operations/contributing.md) guide for details.

---

> Have a question not covered here? Open a [Discussion](https://github.com/spectrayan/promptly/discussions) on GitHub.
