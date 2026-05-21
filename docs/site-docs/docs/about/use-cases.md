# Use Cases

Promptly is designed for any organization that uses AI in production — whether you have a single chatbot or hundreds of specialized agents. Below are common scenarios where teams use Promptly to bring governance, safety, and operational control to their AI systems.

---

## Why Risk Mitigation Matters

The rapid adoption of generative AI has introduced a new category of security and compliance risks that traditional tools are not equipped to handle:

- **OWASP Top 10 for LLMs** lists prompt injection as the **#1 risk** facing LLM applications today
- **65% of organizations** using generative AI have experienced at least one security incident related to their AI systems (industry reports, 2024)
- The **average cost of an AI-related data breach** is 12% higher than traditional breaches due to the unpredictable nature of AI outputs and the difficulty of containment
- **Regulatory fines** for non-compliant AI systems under the EU AI Act and GDPR can reach **6% of global annual revenue**
- **Shadow AI** — ungoverned prompts deployed without oversight — is present in an estimated 60% of enterprises with active AI programs

> **Promptly is the safety net between your AI prompts and production.** It ensures every prompt is versioned, scanned for vulnerabilities, peer-reviewed, and auditable before it reaches your users.

Organizations that deploy Promptly gain:

- **Proactive risk detection** — catch vulnerabilities before they become incidents
- **Demonstrable governance** — prove to regulators that AI operations are controlled
- **Operational confidence** — ship prompt changes faster because you know they've been validated
- **Incident response readiness** — when something goes wrong, trace it back to the exact change

---

## 🤖 Customer Service & Support Bots

**The Challenge:**
Your customer-facing chatbot uses dozens of prompts — greetings, escalation logic, refund policies, troubleshooting flows. Product managers want to tweak the tone weekly, but every change currently requires a code deployment.

**How Promptly Helps:**
- **Product managers update prompts directly** through the Promptly UI, no code changes needed.
- The **approval workflow** ensures a peer review before any change goes live.
- The **vulnerability scanner** automatically checks that no prompt leaks customer PII.
- The **runtime delivery API** serves the latest approved version — zero downtime, zero redeployments.

> **Result:** Faster iteration on customer experience, with guardrails that prevent regressions.

---

## 🏥 Healthcare & Life Sciences

**The Challenge:**
Your medical triage AI assistant uses prompts that must comply with HIPAA regulations. Auditors require a full history of every instruction change, and any prompt that references patient data must be carefully reviewed.

**How Promptly Helps:**
- **Immutable audit trail** records every change with timestamp, author, and approval status — ready for regulatory audits.
- **Security scanning** automatically flags prompts that risk exposing PHI (Protected Health Information).
- **Role-based workflows** ensure that clinical content is reviewed by authorized medical staff before deployment.
- **Version rollback** allows instant recovery if a prompt introduces problematic behavior.

> **Result:** Compliant AI operations with a defensible audit trail.

---

## 💰 Financial Services & Banking

**The Challenge:**
AI agents handle customer inquiries about account balances, loan applications, and investment advice. Regulatory bodies require demonstrable controls over AI behavior, and any unreviewed change is a compliance violation.

**How Promptly Helps:**
- **Governance workflows** enforce that every prompt change goes through compliance review before reaching production.
- **Audit logs** provide a complete, tamper-proof record for SOC 2 and internal audits.
- **Prompt injection detection** prevents adversarial attacks that could trick the AI into revealing account information.
- **Multi-project workspaces** separate prompts by business line (retail banking, wealth management, insurance) with independent RBAC.

> **Result:** Meet regulatory obligations while accelerating AI adoption across business lines.

---

## 🛒 E-Commerce & Retail

**The Challenge:**
Your product recommendation engine, shopping assistant, and marketing copy generator each rely on different prompts. During peak sales events, you need to rapidly update AI behavior — seasonal messaging, promotional guardrails, and tone adjustments.

**How Promptly Helps:**
- **Fast prompt updates** via the Runtime Delivery API let marketing teams deploy new messaging in minutes, not days.
- **Version history and rollback** ensure you can instantly revert if a promotional prompt underperforms.
- **Semantic search** helps teams discover and reuse existing prompts instead of creating duplicates.
- **AI quality improvement** automatically refines prompts for clarity and conversion effectiveness.

> **Result:** Agile AI-driven marketing with full version control and instant rollback capability.

---

## 🏢 Enterprise AI Platforms & Internal Tools

**The Challenge:**
Your organization runs multiple AI-powered internal tools — HR assistants, IT support bots, document summarizers, and code review agents. Each tool has its own prompts, maintained by different teams, with no central visibility.

**How Promptly Helps:**
- **Centralized prompt registry** provides a single pane of glass across all AI tools and teams.
- **Project-based organization** keeps each tool's prompts isolated with their own access controls and workflows.
- **Semantic duplicate detection** identifies redundant prompts across teams, reducing maintenance burden.
- **Export/Import APIs** enable GitOps-style promotion across dev, staging, and production environments.

> **Result:** Organization-wide visibility and control over all AI behavior from a single platform.

---

## 🔧 Multi-Agent Orchestration Systems

**The Challenge:**
You're building a system where multiple AI agents collaborate — a planning agent, a research agent, a validation agent, and a summarization agent. Each agent has a system prompt that defines its role, constraints, and output format. Changes to one prompt can cascade through the entire system.

**How Promptly Helps:**
- **Agent-aware delivery** (`appId`, `usecase`, `agent` parameters) fetches the right prompt for each agent at runtime.
- **Version pinning** ensures stable agent behavior while new prompt versions are being tested.
- **Diff viewer** shows exactly what changed between versions, making cascading impact analysis straightforward.
- **Workflow approvals** prevent untested prompt changes from destabilizing the multi-agent pipeline.

> **Result:** Stable, governed multi-agent systems with clear change management.

---

## 🧪 AI Safety & Red Teaming

**The Challenge:**
Your red team regularly tests AI systems for adversarial vulnerabilities. They need to track which prompts have been tested, what vulnerabilities were found, and whether remediations were applied.

**How Promptly Helps:**
- **Automated vulnerability scanning** runs on every prompt version, providing a baseline security assessment.
- **Scan history** is linked to each prompt version, showing the security posture over time.
- **Fix-in-Editor workflow** lets authors apply remediation suggestions directly from the scan report.
- **Audit trail** documents when scans were run, what was found, and when fixes were deployed.

> **Result:** A structured, repeatable approach to AI safety testing with full traceability.

---

## 🛡️ AI Security & Risk Management

**The Challenge:**
Your organization deploys AI at scale but lacks visibility into the security posture of individual prompts. New vulnerability classes are discovered regularly, and you need confidence that your prompts are resilient against evolving threats.

**How Promptly Helps:**

- **Pre-deployment vulnerability scanning** — Every prompt is scanned across 16 vulnerability categories before reaching production. No prompt goes live without a security assessment.
- **Injection attack prevention** — The scanner detects and reports prompt injection risks (the #1 LLM vulnerability) with specific remediation guidance, allowing teams to fix issues before they become incidents.
- **Continuous compliance monitoring** — Track regulatory exposure (GDPR, HIPAA, EU AI Act) across all prompts in a project. Scanner findings include `REGULATORY_VIOLATION` and `PHI_EXPOSURE` categories that map directly to compliance requirements.
- **Incident response** — When a vulnerability is discovered in the wild, instantly identify all affected prompts via version history, scan history, and audit logs. Trace which prompt versions were deployed when, and roll back to known-safe versions immediately.

> **Result:** A proactive security posture that catches vulnerabilities before they become breaches.

---

## 🏦 Regulated Industries (Healthcare, Finance, Legal)

**The Challenge:**
Regulatory frameworks (HIPAA, SOC 2, GDPR, the EU AI Act) require demonstrable controls over AI systems. Auditors need evidence that changes are tracked, reviewed, and secured — not just promises that "we follow best practices."

**How Promptly Helps:**

- **Audit trail requirements** — Complete version history showing who changed what, who approved it, when scans were run, and what the results were. Every action is timestamped and attributed to a specific user.
- **Separation of duties** — Authors cannot approve their own prompts. This is enforced by RBAC at the platform level — not a policy document, but a technical control that cannot be bypassed.
- **Evidence for compliance audits** — Export full lifecycle history including scan results, review decisions, deployment records, and rollback events. Auditors get a single, comprehensive artifact.
- **PII/PHI protection** — The scanner specifically checks for PII/PHI exposure patterns in prompts, reporting `PHI_EXPOSURE` findings when prompts lack proper data handling instructions.

> **Result:** Audit-ready AI governance with technical controls that satisfy even the most stringent regulators.

---

## 🏢 Enterprise AI Governance

**The Challenge:**
AI adoption is accelerating across your organization, but governance has not kept pace. Teams create prompts in ad-hoc ways — some in code, some in configuration files, some in third-party platforms — with no central visibility or control.

**How Promptly Helps:**

- **Prompt sprawl control** — Centralize all AI prompts in a single platform instead of having them scattered across codebases, config files, and SaaS tools. One source of truth for all AI instructions.
- **Quality gates** — Enforce peer review + security scan before any prompt goes live. No prompt reaches production without passing through automated and human review gates.
- **Cost optimization** — Track which models and prompts are deployed across environments. Identify redundant prompts, unused versions, and opportunities to consolidate.
- **Shadow AI elimination** — Give teams a governed platform that is easy to use (reducing the motivation for shadow AI) while ensuring every prompt flows through security scanning and approval workflows.

> **Result:** Enterprise-wide AI governance without slowing down innovation.

---

## 🔄 AI Ops & Production Safety

**The Challenge:**
AI behavior in production is ultimately determined by prompts. When something goes wrong — a chatbot gives bad advice, a summarizer hallucinates, an agent takes an unexpected action — you need to understand what changed and recover quickly.

**How Promptly Helps:**

- **Safe rollback** — If a prompt causes issues in production, instantly revert to a known-good version via the UI or API. The previous version is immediately served to all consumers.
- **A/B testing governance** — Track which prompt versions are deployed in which environments. Use version pinning to control exactly which variant each consumer receives.
- **Blast radius control** — Version-level deployment means only specific prompt changes go live, not bulk updates. Each change is isolated, reviewed, and independently deployable.
- **Incident correlation** — When AI behavior changes unexpectedly, trace it back to specific prompt version changes via the audit trail. See exactly what changed, when, and who approved it.

> **Result:** Production AI operations with the same safety controls you expect for code deployments.

---

## Summary

| Use Case | Key Value |
|----------|-----------|
| Customer Service Bots | Faster iteration without code deployments |
| Healthcare | HIPAA-compliant audit trail and PHI protection |
| Financial Services | Regulatory governance with SOC 2 readiness |
| E-Commerce | Agile prompt updates during peak events |
| Enterprise AI Platforms | Centralized visibility across all AI tools |
| Multi-Agent Systems | Stable, governed agent orchestration |
| AI Safety & Red Teaming | Structured security testing with traceability |
| AI Security & Risk Management | Proactive vulnerability detection across 16 categories |
| Regulated Industries | Audit-ready governance with technical controls |
| Enterprise AI Governance | Shadow AI elimination and prompt sprawl control |
| AI Ops & Production Safety | Safe rollback and incident correlation |

No matter your industry or team size, if AI is part of your product — **Promptly ensures it's managed, secure, and auditable.**
