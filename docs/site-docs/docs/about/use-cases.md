# Use Cases

Promptly is designed for any organization that uses AI in production — whether you have a single chatbot or hundreds of specialized agents. Below are common scenarios where teams use Promptly to bring governance, safety, and operational control to their AI systems.

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

No matter your industry or team size, if AI is part of your product — **Promptly ensures it's managed, secure, and auditable.**
