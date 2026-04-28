/**
 * Promptly — k6 Smoke Test
 *
 * Quick validation that the perf stack is healthy before running
 * full load tests. 1 VU, 10 seconds.
 *
 * If the smoke test fails, the orchestrator script will NOT
 * proceed to run the heavier performance tests.
 *
 * Run: k6 run k6/scripts/smoke.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { requireAuth } from './helpers/auth.js';
import { generateReport } from './helpers/report.js';

export const options = {
  vus: 1,
  duration: '10s',
  thresholds: {
    http_req_duration: ['p(99)<2000'],
    http_req_failed: ['rate<0.05'],
    checks: ['rate>0.95'],           // 95% of all checks must pass
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://nginx:8080';
const PROJECT_ID = __ENV.PROJECT_ID || 'proj-001';

// Acquire JWT token once before all iterations — aborts test on failure
export function setup() {
  return requireAuth();
}

export default function (data) {
  // Health check (public endpoint)
  const healthRes = http.get(`${BASE_URL}/actuator/health`);
  check(healthRes, {
    'health endpoint returns 200': (r) => r.status === 200,
    'health status is UP': (r) => {
      try {
        return JSON.parse(r.body).status === 'UP';
      } catch {
        return false;
      }
    },
  });

  sleep(1);

  // Projects list (authenticated)
  const projectsRes = http.get(`${BASE_URL}/api/v1/projects`, {
    headers: data.headers,
  });
  check(projectsRes, {
    'projects returns 2xx': (r) => r.status >= 200 && r.status < 300,
  });

  sleep(0.5);

  // Prompts list (authenticated, filtered by project)
  const promptsRes = http.get(`${BASE_URL}/api/v1/prompts?projectId=${PROJECT_ID}`, {
    headers: data.headers,
  });
  check(promptsRes, {
    'prompts returns 200': (r) => r.status === 200,
  });

  sleep(0.5);
}

/** Generate HTML + JSON reports when the test finishes */
export function handleSummary(data) {
  return generateReport(data, 'smoke');
}
