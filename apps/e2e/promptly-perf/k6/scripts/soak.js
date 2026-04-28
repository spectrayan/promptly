/**
 * Promptly — k6 API Soak Test
 *
 * Sustained moderate load over a longer period to detect
 * memory leaks, connection pool exhaustion, and degradation.
 *
 * Run: k6 run k6/scripts/soak.js
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend } from 'k6/metrics';
import { requireAuth } from './helpers/auth.js';
import { generateReport } from './helpers/report.js';

const responseTrend = new Trend('api_response_time', true);

export const options = {
  stages: [
    { duration: '1m',  target: 20 },   // Ramp up
    { duration: '5m',  target: 20 },   // Sustain
    { duration: '1m',  target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    http_req_failed: ['rate<0.02'],
    api_response_time: ['p(95)<800'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://nginx:8080';
const PROJECT_ID = __ENV.PROJECT_ID || 'proj-001';

// Acquire JWT token once before all iterations
export function setup() {
  return requireAuth();
}

export default function (data) {
  const headers = data.headers;

  group('Health Check', () => {
    const res = http.get(`${BASE_URL}/actuator/health`);
    check(res, { 'health 200': (r) => r.status === 200 });
  });

  group('List Projects', () => {
    const start = Date.now();
    const res = http.get(`${BASE_URL}/api/v1/projects`, { headers });
    responseTrend.add(Date.now() - start);
    check(res, { 'projects 200': (r) => r.status === 200 });
  });

  sleep(0.5);

  group('List Prompts', () => {
    const start = Date.now();
    const res = http.get(
      `${BASE_URL}/api/v1/prompts?projectId=${PROJECT_ID}`,
      { headers }
    );
    responseTrend.add(Date.now() - start);
    check(res, { 'prompts 200': (r) => r.status === 200 });
  });

  sleep(0.5);

  group('List Workflows', () => {
    const start = Date.now();
    const res = http.get(
      `${BASE_URL}/api/v1/workflows?projectId=${PROJECT_ID}`,
      { headers }
    );
    responseTrend.add(Date.now() - start);
    check(res, { 'workflows 2xx': (r) => r.status >= 200 && r.status < 300 });
  });

  sleep(1);
}

/** Generate HTML + JSON reports when the test finishes */
export function handleSummary(data) {
  return generateReport(data, 'soak');
}
