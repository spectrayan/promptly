# Dashboard

The **Dashboard** is the first thing you see when you select a Project in Promptly. It provides a real-time, bird's-eye view of your AI governance posture.

## Key Metrics

The top of the dashboard displays four primary statistics:
* **Total Prompts:** The number of unique prompt identifiers in this project.
* **Deployed:** The number of prompts currently active in production.
* **In Review:** Prompts that have been drafted and submitted, waiting for a peer reviewer to approve or reject them.
* **Security Alerts:** Any active vulnerabilities or failed scans that require immediate attention.

## Activity Feed

The lower section of the dashboard shows a chronological feed of all domain events within the project. This acts as an audit log. You'll see:
* When a user creates a new draft (`PromptDraftCreated`)
* When a prompt is submitted for review (`PromptSubmittedForReview`)
* When a reviewer approves a prompt (`PromptApproved`)
* When a prompt is deployed or rolled back.

This ensures complete transparency and accountability for all AI changes made by your team.
