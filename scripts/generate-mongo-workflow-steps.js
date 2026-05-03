const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const SEED_DIR = path.join(__dirname, '../seed-data');
const workflowsFile = path.join(SEED_DIR, 'workflows/workflows.json');
const stepsFile = path.join(SEED_DIR, 'workflow_steps/workflow_steps.json');

if (!fs.existsSync(workflowsFile)) process.exit(0);
const workflows = JSON.parse(fs.readFileSync(workflowsFile, 'utf-8'));

const allSteps = [];
workflows.forEach(w => {
    if (w.steps && w.steps.length > 0) {
        w.steps.forEach(s => {
            allSteps.push({
                _id: s._id || crypto.randomUUID(),
                workflowId: w._id,
                step: s.step,
                role: s.role,
                assignedTo: s.assignedTo,
                action: s.action,
                comment: s.comment,
                actedAt: s.actedAt
            });
        });
        delete w.steps;
    }
});

fs.writeFileSync(workflowsFile, JSON.stringify(workflows, null, 2));

if (!fs.existsSync(path.dirname(stepsFile))) {
    fs.mkdirSync(path.dirname(stepsFile), { recursive: true });
}
fs.writeFileSync(stepsFile, JSON.stringify(allSteps, null, 2));
console.log('Generated workflow_steps.json');
