const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const container = 'mongo';
const db = 'promptly';
const seedDir = __dirname;

console.log(`🌱 Seeding Promptly database (container: ${container}, db: ${db})...`);

const collections = [
  "users", "projects", "project_members", "prompts", "prompt_history",
  "workflows", "workflow_steps", "scan_results", "audit_logs",
  "notifications", "notification_project_settings"
];

const files = {
  "users": "users/users.json",
  "projects": "projects/projects.json",
  "project_members": "project_members/project_members.json",
  "prompts": "prompts/prompts.json",
  "prompt_history": "prompt_history/prompt_history.json",
  "workflows": "workflows/workflows.json",
  "workflow_steps": "workflow_steps/workflow_steps.json",
  "scan_results": "scan_results/scan_results.json",
  "audit_logs": "audit_logs/audit_logs.json",
  "notifications": "notifications/notifications.json",
  "notification_project_settings": "notification_project_settings/notification_project_settings.json"
};

for (const collection of collections) {
  const relativePath = files[collection];
  const fullPath = path.join(seedDir, relativePath);

  if (!fs.existsSync(fullPath)) {
    console.log(`  ⚠️ Skipping ${collection} — ${relativePath} not found`);
    continue;
  }

  try {
    // Drop existing collection
    execSync(`docker exec -i ${container} mongosh ${db} --quiet --eval "db.${collection}.drop()"`, { stdio: 'ignore' });

    // Import via mongoimport
    const content = fs.readFileSync(fullPath);
    execSync(`docker exec -i ${container} mongoimport --db ${db} --collection ${collection} --jsonArray --quiet`, {
      input: content
    });

    const count = execSync(`docker exec -i ${container} mongosh ${db} --quiet --eval "db.${collection}.countDocuments()"`).toString().trim();
    console.log(`  ✅ ${collection}: ${count} documents`);
  } catch (err) {
    console.error(`  ❌ Error importing ${collection}:`, err.message);
  }
}

console.log('\n📇 Creating indexes...');
try {
  const initJsContent = fs.readFileSync(path.join(seedDir, 'init.js'));
  execSync(`docker exec -i ${container} mongosh ${db} --quiet`, {
    input: initJsContent
  });
  console.log('🎉 Seeding completed successfully!');
} catch (err) {
  console.error('❌ Error creating indexes:', err.message);
}
