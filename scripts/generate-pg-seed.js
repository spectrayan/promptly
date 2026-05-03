const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const crypto = require('crypto');

const SEED_DIR = path.join(__dirname, '../seed-data');

function readJson(file) {
    if (!fs.existsSync(file)) return null;
    return JSON.parse(fs.readFileSync(file, 'utf-8'));
}

function esc(val) {
    if (val === null || val === undefined) return 'NULL';
    if (typeof val === 'number') return val;
    if (typeof val === 'boolean') return val ? 'TRUE' : 'FALSE';
    if (typeof val === 'object') {
        if (val.$date) return `'${val.$date}'`;
        return `'${JSON.stringify(val).replace(/'/g, "''")}'`;
    }
    return `'${val.toString().replace(/'/g, "''")}'`;
}

function processUsers() {
    const users = readJson(path.join(SEED_DIR, 'users/users.json'));
    if (!users) return '';
    return users.map(u => `INSERT INTO users (id, email, display_name, password_hash, avatar_url, org_role, status, last_login_at, created_at, updated_at) VALUES (${esc(u._id)}, ${esc(u.email)}, ${esc(u.displayName)}, ${esc(u.passwordHash)}, ${esc(u.avatarUrl)}, ${esc(u.orgRole)}, ${esc(u.status)}, ${esc(u.lastLoginAt)}, ${esc(u.createdAt)}, ${esc(u.updatedAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processProjects() {
    const projects = readJson(path.join(SEED_DIR, 'projects/projects.json'));
    if (!projects) return '';
    return projects.map(p => `INSERT INTO projects (id, name, description, tags, created_by, created_at, updated_at) VALUES (${esc(p._id)}, ${esc(p.name)}, ${esc(p.description)}, ${esc(p.tags || [])}, ${esc(p.createdBy)}, ${esc(p.createdAt)}, ${esc(p.updatedAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processProjectMembers() {
    const members = readJson(path.join(SEED_DIR, 'project_members/project_members.json'));
    if (!members) return '';
    return members.map(m => `INSERT INTO project_members (id, project_id, user_id, role, added_by, added_at) VALUES (${esc(m._id)}, ${esc(m.projectId)}, ${esc(m.userId)}, ${esc(m.role)}, ${esc(m.addedBy)}, ${esc(m.addedAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processPrompts() {
    const prompts = readJson(path.join(SEED_DIR, 'prompts/prompts.json'));
    if (!prompts) return '';
    return prompts.map(p => `INSERT INTO prompts (id, name, description, project_id, content_format, tags, metadata_model, metadata_temperature, metadata_max_tokens, metadata_system_context, current_version, status, version, created_at, updated_at, created_by, updated_by) VALUES (${esc(p._id)}, ${esc(p.name)}, ${esc(p.description)}, ${esc(p.projectId)}, ${esc(p.contentFormat)}, ${esc(p.tags || [])}, ${esc(p.metadata?.model)}, ${esc(p.metadata?.temperature)}, ${esc(p.metadata?.maxTokens)}, ${esc(p.metadata?.systemContext)}, ${esc(p.currentVersion)}, ${esc(p.status)}, 0, ${esc(p.createdAt)}, ${esc(p.updatedAt)}, ${esc(p.createdBy)}, ${esc(p.updatedBy)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processPromptHistory() {
    const history = readJson(path.join(SEED_DIR, 'prompt_history/prompt_history.json'));
    if (!history) return '';
    return history.map(h => `INSERT INTO prompt_history (id, prompt_id, version_number, content, change_message, created_by, created_at) VALUES (${esc(h._id)}, ${esc(h.promptId)}, ${esc(h.versionNumber)}, ${esc(h.content)}, ${esc(h.changeMessage)}, ${esc(h.createdBy)}, ${esc(h.createdAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processWorkflows() {
    const workflows = readJson(path.join(SEED_DIR, 'workflows/workflows.json'));
    if (!workflows) return '';
    return workflows.map(w => {
        const wfInsert = `INSERT INTO workflows (id, project_id, prompt_id, prompt_version, type, status, current_step, requested_by, version, created_at, updated_at) VALUES (${esc(w._id)}, ${esc(w.projectId)}, ${esc(w.promptId)}, ${esc(w.promptVersion)}, ${esc(w.type)}, ${esc(w.status)}, ${esc(w.currentStep)}, ${esc(w.requestedBy)}, 0, ${esc(w.createdAt)}, ${esc(w.updatedAt)}) ON CONFLICT DO NOTHING;`;
        let stepsInsert = '';
        if (w.steps && w.steps.length > 0) {
            stepsInsert = w.steps.map(s => `INSERT INTO workflow_steps (id, workflow_id, step, role, assigned_to, action, comment, acted_at) VALUES (${esc(s._id || crypto.randomUUID())}, ${esc(w._id)}, ${esc(s.step)}, ${esc(s.role)}, ${esc(s.assignedTo)}, ${esc(s.action)}, ${esc(s.comment)}, ${esc(s.actedAt)}) ON CONFLICT DO NOTHING;`).join('\n');
        }
        return wfInsert + '\n' + stepsInsert;
    }).join('\n');
}

function processScanResults() {
    const scans = readJson(path.join(SEED_DIR, 'scan_results/scan_results.json'));
    if (!scans) return '';
    return scans.map(s => `INSERT INTO scan_results (id, project_id, prompt_id, prompt_version, overall_score, status, llm_provider, llm_model, scanned_by, findings, scanned_at, version, created_at, updated_at) VALUES (${esc(s._id)}, ${esc(s.projectId)}, ${esc(s.promptId)}, ${esc(s.promptVersion)}, ${esc(s.overallScore)}, ${esc(s.status)}, ${esc(s.llmProvider)}, ${esc(s.llmModel)}, ${esc(s.scannedBy)}, ${esc(s.findings || [])}, ${esc(s.scannedAt)}, 0, ${esc(s.createdAt)}, ${esc(s.updatedAt || s.createdAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processAuditLogs() {
    const logs = readJson(path.join(SEED_DIR, 'audit_logs/audit_logs.json'));
    if (!logs) return '';
    return logs.map(a => `INSERT INTO audit_logs (id, project_id, action, resource_type, resource_id, resource_version, actor_user_id, actor_email, actor_role, details, timestamp, created_at, updated_at) VALUES (${esc(a._id)}, ${esc(a.projectId)}, ${esc(a.action)}, ${esc(a.resourceType)}, ${esc(a.resourceId)}, ${esc(a.resourceVersion)}, ${esc(a.actorUserId)}, ${esc(a.actorEmail)}, ${esc(a.actorRole)}, ${esc(a.details || {})}, ${esc(a.timestamp)}, ${esc(a.createdAt)}, ${esc(a.updatedAt || a.createdAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processNotifications() {
    const notifs = readJson(path.join(SEED_DIR, 'notifications/notifications.json'));
    if (!notifs) return '';
    return notifs.map(n => `INSERT INTO notifications (id, user_id, project_id, type, title, message, icon, payload, is_read, created_at) VALUES (${esc(n._id)}, ${esc(n.userId)}, ${esc(n.projectId)}, ${esc(n.type)}, ${esc(n.title)}, ${esc(n.message)}, ${esc(n.icon)}, ${esc(n.payload || {})}, ${esc(n.read)}, ${esc(n.createdAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

function processNotificationProjectSettings() {
    const settings = readJson(path.join(SEED_DIR, 'notification_project_settings/notification_project_settings.json'));
    if (!settings) return '';
    return settings.map(s => `INSERT INTO project_notification_settings (id, project_id, enabled_events, updated_at) VALUES (${esc(s._id)}, ${esc(s.projectId)}, ${esc(s.enabledEvents || [])}, ${esc(s.updatedAt)}) ON CONFLICT DO NOTHING;`).join('\n');
}

const sql = [
    processUsers(),
    processProjects(),
    processProjectMembers(),
    processPrompts(),
    processPromptHistory(),
    processWorkflows(),
    processScanResults(),
    processAuditLogs(),
    processNotifications(),
    processNotificationProjectSettings()
].join('\n\n');

fs.writeFileSync(path.join(__dirname, 'pg-seed.sql'), sql);
console.log('pg-seed.sql generated');
