const fs = require('fs');
const path = require('path');

const SEED_DIR = path.join(__dirname, '../seed-data');
const HEALTH_PROMPTS_DIR = path.join('D:/git/spectrayan-health/apps/backend/ai/src/health_ai/prompts');

// Update projects.json
const projectsFile = path.join(SEED_DIR, 'projects/projects.json');
const projects = JSON.parse(fs.readFileSync(projectsFile, 'utf8'));
if (!projects.find(p => p._id === 'proj-health')) {
  projects.push({
    "_id": "proj-health",
    "name": "spectrayan-health",
    "description": "Healthcare AI prompts for ABA, CBT, OT, and ST therapies",
    "tags": ["healthcare", "therapy", "clinical"],
    "createdBy": "usr-001",
    "createdAt": { "$date": "2026-05-01T08:00:00Z" },
    "updatedAt": { "$date": "2026-05-20T10:00:00Z" }
  });
  fs.writeFileSync(projectsFile, JSON.stringify(projects, null, 2));
  console.log('Updated projects.json');
}

// Update prompts.json
const promptsFile = path.join(SEED_DIR, 'prompts/prompts.json');
const prompts = JSON.parse(fs.readFileSync(promptsFile, 'utf8'));

const therapies = [
  { id: 'aba', name: 'ABA Support Assistant', tags: ['aba', 'autism', 'behavior'] },
  { id: 'cbt', name: 'CBT Support Assistant', tags: ['cbt', 'mental-health', 'anxiety'] },
  { id: 'occupational', name: 'OT Support Assistant', tags: ['ot', 'motor', 'sensory'] },
  { id: 'speech', name: 'Speech-Language Support Assistant', tags: ['speech', 'language', 'communication'] }
];

therapies.forEach((therapy, index) => {
  const promptId = `p-health-${therapy.id}`;
  if (!prompts.find(p => p._id === promptId)) {
    const mdContent = fs.readFileSync(path.join(HEALTH_PROMPTS_DIR, therapy.id, 'full_prompt.md'), 'utf8');
    prompts.push({
      "_id": promptId,
      "name": therapy.name,
      "description": `Educational support tool and goal planning for ${therapy.id.toUpperCase()}`,
      "projectId": "proj-health",
      "contentFormat": "MARKDOWN",
      "tags": therapy.tags,
      "metadata": { "model": "gemini-1.5-pro", "temperature": 0.2, "maxTokens": 2048, "systemContext": "Healthcare educational tool" },
      "currentVersion": 1,
      "content": mdContent,
      "activeEnvironment": "STAGING",
      "status": "APPROVED",
      "version": 0,
      "createdBy": "usr-001",
      "updatedBy": "usr-001",
      "createdAt": { "$date": "2026-05-01T10:00:00Z" },
      "updatedAt": { "$date": "2026-05-20T12:00:00Z" }
    });
  }
});
fs.writeFileSync(promptsFile, JSON.stringify(prompts, null, 2));
console.log('Updated prompts.json');

// Update scan_results.json
const scansFile = path.join(SEED_DIR, 'scan_results/scan_results.json');
const scans = JSON.parse(fs.readFileSync(scansFile, 'utf8'));

therapies.forEach((therapy, index) => {
  const promptId = `p-health-${therapy.id}`;
  const scanId = `scan-health-${therapy.id}`;
  if (!scans.find(s => s._id === scanId)) {
    const isWarn = therapy.id === 'cbt' || therapy.id === 'aba';
    scans.push({
      "_id": scanId,
      "projectId": "proj-health",
      "promptId": promptId,
      "promptVersion": 1,
      "overallScore": isWarn ? 0.75 : 0.96,
      "status": isWarn ? "WARN" : "PASS",
      "llmProvider": "google",
      "llmModel": "gemini-1.5-pro",
      "scannedBy": "system",
      "scannedAt": { "$date": "2026-05-20T14:00:00Z" },
      "findings": isWarn ? [
        {
          "type": "pii_risk",
          "severity": "MEDIUM",
          "title": "Potential PII Exposure Risk",
          "description": "Healthcare prompts often receive PII (PHI) in user inputs. Ensure adequate redaction guardrails are active.",
          "remediation": "Enable the PII redaction scanner or integrate a DLP tool in the inference pipeline."
        }
      ] : [],
      "createdAt": { "$date": "2026-05-20T14:00:00Z" }
    });
  }
});
fs.writeFileSync(scansFile, JSON.stringify(scans, null, 2));
console.log('Updated scan_results.json');
