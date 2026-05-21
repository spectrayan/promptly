# Why Promptly?

## The Problem: Prompts Are the New Business Logic

Every modern enterprise is racing to embed AI into its products, workflows, and customer interactions. At the heart of every AI-powered feature is a **prompt** — the set of instructions that shapes how the AI behaves, responds, and makes decisions.

But here's the uncomfortable truth: **most organizations treat prompts like throwaway strings.**

They live in scattered places — hardcoded in application source files, buried in Notion pages, copy-pasted across Slack threads, or sitting in random JSON configs that nobody owns. When something goes wrong — a chatbot leaks customer data, an agent gives harmful advice, or a subtle wording change tanks conversion rates — there is **no way to trace what changed, who changed it, or why.**

This is exactly the gap that existed for feature flags before LaunchDarkly, for secrets before HashiCorp Vault, and for infrastructure before Terraform.

**Prompts deserve the same rigor.**

---

## What Happens Without Prompt Governance?

| Risk | Real-World Consequence |
|------|------------------------|
| 🔓 **Prompt Injection Attacks** | A malicious user manipulates your prompt to make the AI ignore instructions, exfiltrate data, or perform unauthorized actions. |
| 🕵️ **PII & PHI Data Exposure** | Your prompt accidentally instructs the AI to include sensitive customer information in its response, violating GDPR, HIPAA, or CCPA. |
| 🎲 **Uncontrolled Changes** | A developer tweaks a prompt in production on a Friday afternoon — no review, no approval, no rollback plan. Customer experience degrades. |
| 📋 **Compliance Failures** | Your auditor asks for a history of all AI behavior changes. You have nothing to show — no changelog, no approvals, no audit trail. |
| 🔄 **Costly Redeployments** | Updating a single prompt requires a full CI/CD pipeline run, container rebuild, and production deployment — taking hours instead of seconds. |
| 🧩 **Prompt Sprawl** | Teams duplicate and fork prompts across repositories. There's no single source of truth, leading to inconsistent AI behavior across channels. |

These are not hypothetical risks. They are happening today at organizations of every size.

---

## How Promptly Solves This

Promptly is the **control plane for AI behavior** — a centralized platform where teams author, review, secure, deploy, and audit every prompt that powers their AI systems.

> Think of it as: **GitHub for versioning** + **LaunchDarkly for runtime delivery** + **Snyk for security scanning** — purpose-built for AI prompts.

### 🏗️ Centralized Prompt Registry
Stop searching across repos, wikis, and Slack messages. Every prompt lives in one place with full CRUD, tagging, and organization by project.

### 🔒 Governance & Approval Workflows
No prompt reaches production without going through a structured review process: **Draft → Review → Approve → Deploy**. Role-based access ensures the right people are making the right decisions.

### 🛡️ Automated Security Scanning
Every prompt is automatically scanned for vulnerabilities — prompt injection risks, PII/PHI leakage, missing guardrails, and toxicity concerns. Critical issues block deployment.

### 🧠 AI-Powered Quality Improvement
Not sure if your prompt is well-structured? Promptly's built-in AI assistant rewrites and improves prompts for clarity, safety, and effectiveness — right inside the editor.

### 🚀 Runtime Delivery API
Update prompts in production **without redeploying a single line of code**. Your AI agents fetch the latest approved prompt version via a low-latency API call.

### 📜 Immutable Audit Trail
Every action — creation, edit, scan, review, approval, deployment — is recorded in an append-only audit log. Full compliance readiness for SOC 2, HIPAA, ISO 27001, and regulated industries.

### 🏠 Self-Hostable & Open Source
Your prompts are your intellectual property. Run Promptly on your own infrastructure — your data never leaves your network. No vendor lock-in, no surprise pricing.

---

## Who Is Promptly For?

| Role | How Promptly Helps |
|------|--------------------|
| **AI / ML Engineers** | Manage prompts like code — with versioning, diff viewers, rollbacks, and a Monaco-powered editor. |
| **Product Managers** | Update AI behavior (tone, guardrails, feature flags) without waiting for engineering sprints. |
| **Security & Compliance Teams** | Automated vulnerability scanning and immutable audit logs provide the evidence needed for regulatory audits. |
| **Engineering Managers** | Governance workflows ensure that no prompt goes to production without peer review and sign-off. |
| **DevOps / Platform Teams** | CI/CD-friendly Export/Import APIs integrate seamlessly with existing deployment pipelines. |
| **CTO / VP Engineering** | Reduce risk exposure from ungoverned AI systems. Gain visibility into AI behavior across the organization. |

---

## The Bottom Line

AI is no longer experimental — it's in production, talking to your customers, processing sensitive data, and making decisions that affect your business. The prompts driving that AI deserve the same governance, security, and operational rigor that you apply to your code, your infrastructure, and your feature flags.

**Promptly gives you that control.**

> *"You wouldn't deploy code without version control, tests, and a review process. Why would you deploy an AI prompt any differently?"*
