/**
 * Promptly — k6 Prompt CRUD Load Test (Scenario-Based)
 *
 * Uses k6 scenarios to apply DIFFERENT load profiles per API:
 *
 *   ┌──────────────────┬────────┬────────────────────────────┐
 *   │ Scenario         │ Peak   │ Pattern                    │
 *   ├──────────────────┼────────┼────────────────────────────┤
 *   │ list_prompts     │ 100 VU │ Heavy read traffic (GET)   │
 *   │ get_prompt       │ 80 VU  │ Detail page views (GET)    │
 *   │ create_prompt    │ 20 VU  │ Moderate writes (POST)     │
 *   │ update_prompt    │ 10 VU  │ Light edits (PUT)          │
 *   │ delete_prompt    │ 5 VU   │ Rare deletes (DELETE)      │
 *   └──────────────────┴────────┴────────────────────────────┘
 *
 * Adjust the VU numbers in options.scenarios below to match
 * your expected production traffic ratios.
 *
 * Run: k6 run k6/scripts/prompt-crud.js
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { requireAuth } from './helpers/auth.js';
import { generateReport } from './helpers/report.js';

// ── Custom Metrics (per-scenario) ───────────────────────
const promptListDuration = new Trend('prompt_list_duration', true);
const promptGetDuration = new Trend('prompt_get_duration', true);
const promptCreateDuration = new Trend('prompt_create_duration', true);
const promptUpdateDuration = new Trend('prompt_update_duration', true);
const promptDeleteDuration = new Trend('prompt_delete_duration', true);
const errorRate = new Rate('prompt_error_rate');
const promptsCreated = new Counter('prompts_created');

// ── Scenarios ───────────────────────────────────────────
// Each scenario has its own executor, VU count, and function.
// Modify 'target' values to match your expected traffic ratios.
export const options = {
  scenarios: {
    // ── GET /prompts — highest traffic (50% of 1000 = 500 req/s peak) ──
    list_prompts: {
      executor: 'ramping-arrival-rate',
      exec: 'listPrompts',
      timeUnit: '1s',
      preAllocatedVUs: 100,
      maxVUs: 200,
      stages: [
        { duration: '30s', target: 50 },    // warm up
        { duration: '1m',  target: 300 },   // ramp to moderate
        { duration: '1m',  target: 500 },   // peak: 500 req/s
        { duration: '30s', target: 0 },     // cool down
      ],
    },

    // ── GET /prompts/:id — second highest (30% = 300 req/s peak) ──
    get_prompt: {
      executor: 'ramping-arrival-rate',
      exec: 'getPrompt',
      startTime: '10s',
      timeUnit: '1s',
      preAllocatedVUs: 60,
      maxVUs: 150,
      stages: [
        { duration: '30s', target: 30 },
        { duration: '1m',  target: 200 },
        { duration: '1m',  target: 300 },   // peak: 300 req/s
        { duration: '30s', target: 0 },
      ],
    },

    // ── POST /prompts — moderate writes (12% = 120 req/s peak) ──
    create_prompt: {
      executor: 'ramping-arrival-rate',
      exec: 'createPrompt',
      startTime: '10s',
      timeUnit: '1s',
      preAllocatedVUs: 30,
      maxVUs: 80,
      stages: [
        { duration: '30s', target: 10 },
        { duration: '1m',  target: 60 },
        { duration: '1m',  target: 120 },   // peak: 120 req/s
        { duration: '30s', target: 0 },
      ],
    },

    // ── PUT /prompts/:id — light edits (6% = 60 req/s peak) ──
    update_prompt: {
      executor: 'ramping-arrival-rate',
      exec: 'updatePrompt',
      startTime: '15s',
      timeUnit: '1s',
      preAllocatedVUs: 15,
      maxVUs: 40,
      stages: [
        { duration: '30s', target: 5 },
        { duration: '1m',  target: 30 },
        { duration: '1m',  target: 60 },    // peak: 60 req/s
        { duration: '30s', target: 0 },
      ],
    },

    // ── DELETE /prompts/:id — rare (2% = 20 req/s peak) ──
    delete_prompt: {
      executor: 'ramping-arrival-rate',
      exec: 'deletePrompt',
      startTime: '20s',
      timeUnit: '1s',
      preAllocatedVUs: 10,
      maxVUs: 30,
      stages: [
        { duration: '30s', target: 2 },
        { duration: '1m',  target: 10 },
        { duration: '1m',  target: 20 },    // peak: 20 req/s
        { duration: '30s', target: 0 },
      ],
    },
  },

  thresholds: {
    // ── Global thresholds ──
    http_req_failed: ['rate<0.01'],
    prompt_error_rate: ['rate<0.05'],

    // ── Per-API latency thresholds (relaxed for high-load testing) ──
    prompt_list_duration: ['p(95)<800', 'p(99)<2000'],
    prompt_get_duration: ['p(95)<500', 'p(99)<1500'],
    prompt_create_duration: ['p(95)<1000', 'p(99)<3000'],
    prompt_update_duration: ['p(95)<1000', 'p(99)<3000'],
    prompt_delete_duration: ['p(95)<800', 'p(99)<2000'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://nginx:8080';
const PROJECT_ID = __ENV.PROJECT_ID || 'proj-001';

// ── Shared setup — acquire JWT once ─────────────────────
export function setup() {
  const authData = requireAuth();

  // Pre-create a pool of prompts for GET/PUT/DELETE scenarios to target
  const seedIds = [];
  for (let i = 0; i < 10; i++) {
    const res = http.post(
      `${BASE_URL}/api/v1/prompts`,
      JSON.stringify({
        name: `perf-seed-${i}-${Date.now()}`,
        description: 'Seed prompt for perf read/update/delete tests',
        projectId: PROJECT_ID,
        content: 'You are a helpful assistant used for performance testing.',
        author: 'alice@promptly.ai',
      }),
      { headers: authData.headers }
    );
    if (res.status === 201) {
      try { seedIds.push(JSON.parse(res.body).id); } catch { /* skip */ }
    }
  }

  return { headers: authData.headers, seedIds };
}

// ═══════════════════════════════════════════════════════════
// Scenario Functions — each is called by its own VU pool
// ═══════════════════════════════════════════════════════════

/** GET /api/v1/prompts — list all prompts (heaviest traffic) */
export function listPrompts(data) {
  const start = Date.now();
  const res = http.get(
    `${BASE_URL}/api/v1/prompts?projectId=${PROJECT_ID}`,
    { headers: data.headers }
  );
  promptListDuration.add(Date.now() - start);

  const ok = check(res, { 'list: 200': (r) => r.status === 200 });
  errorRate.add(!ok);

  sleep(randomBetween(0.5, 2));
}

/** GET /api/v1/prompts/:id — get a specific prompt */
export function getPrompt(data) {
  const id = pickRandom(data.seedIds);
  if (!id) return;

  const start = Date.now();
  const res = http.get(
    `${BASE_URL}/api/v1/prompts/${id}`,
    { headers: data.headers }
  );
  promptGetDuration.add(Date.now() - start);

  const ok = check(res, { 'get: 200': (r) => r.status === 200 });
  errorRate.add(!ok);

  sleep(randomBetween(0.3, 1.5));
}

/** POST /api/v1/prompts — create a new prompt */
export function createPrompt(data) {
  const payload = JSON.stringify({
    name: `perf-prompt-${__VU}-${__ITER}-${Date.now()}`,
    description: `Performance test prompt VU=${__VU} ITER=${__ITER}`,
    projectId: PROJECT_ID,
    content: 'You are a helpful assistant. Respond clearly and concisely.',
    author: 'alice@promptly.ai',
  });

  const start = Date.now();
  const res = http.post(
    `${BASE_URL}/api/v1/prompts`,
    payload,
    { headers: data.headers }
  );
  promptCreateDuration.add(Date.now() - start);

  const ok = check(res, {
    'create: 201': (r) => r.status === 201,
    'create: has id': (r) => {
      try { return !!JSON.parse(r.body).id; } catch { return false; }
    },
  });
  errorRate.add(!ok);
  if (ok) promptsCreated.add(1);

  sleep(randomBetween(1, 3));
}

/** PUT /api/v1/prompts/:id — update an existing prompt */
export function updatePrompt(data) {
  const id = pickRandom(data.seedIds);
  if (!id) return;

  const payload = JSON.stringify({
    content: `Updated by perf test — VU=${__VU} ITER=${__ITER} @ ${Date.now()}`,
    changeMessage: 'perf test update',
    author: 'alice@promptly.ai',
  });

  const start = Date.now();
  const res = http.put(
    `${BASE_URL}/api/v1/prompts/${id}`,
    payload,
    { headers: data.headers }
  );
  promptUpdateDuration.add(Date.now() - start);

  const ok = check(res, { 'update: 200': (r) => r.status === 200 });
  errorRate.add(!ok);

  sleep(randomBetween(1, 4));
}

/** DELETE /api/v1/prompts/:id — delete a prompt */
export function deletePrompt(data) {
  // Create a throw-away prompt to delete (don't deplete the seed pool)
  const createRes = http.post(
    `${BASE_URL}/api/v1/prompts`,
    JSON.stringify({
      name: `perf-delete-${__VU}-${__ITER}-${Date.now()}`,
      description: 'Ephemeral prompt for delete perf test',
      projectId: PROJECT_ID,
      content: 'Temporary prompt — will be deleted immediately.',
      author: 'alice@promptly.ai',
    }),
    { headers: data.headers }
  );

  let id;
  try { id = JSON.parse(createRes.body).id; } catch { return; }
  if (!id) return;

  const deleteHeaders = Object.assign({}, data.headers, { 'Accept': '*/*' });

  const start = Date.now();
  const res = http.del(
    `${BASE_URL}/api/v1/prompts/${id}`,
    null,
    { headers: deleteHeaders }
  );
  promptDeleteDuration.add(Date.now() - start);

  const ok = check(res, { 'delete: 204': (r) => r.status === 204 || r.status === 200 });
  errorRate.add(!ok);

  sleep(randomBetween(2, 5));
}

// ── Helpers ─────────────────────────────────────────────
function pickRandom(arr) {
  if (!arr || arr.length === 0) return null;
  return arr[Math.floor(Math.random() * arr.length)];
}

function randomBetween(min, max) {
  return min + Math.random() * (max - min);
}

/** Generate HTML + JSON reports when the test finishes */
export function handleSummary(data) {
  return generateReport(data, 'prompt-crud');
}
