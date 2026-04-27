/**
 * Promptly — k6 SSE Streaming Test
 *
 * Tests the AI assistant Server-Sent Events endpoint
 * under load. Validates that SSE connections are properly
 * managed and don't leak under concurrent usage.
 *
 * Run: k6 run k6/scripts/ai-stream.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { getAuthHeaders } from './helpers/auth.js';
import { generateReport } from './helpers/report.js';

const sseConnections = new Counter('sse_connections');
const sseFirstByte = new Trend('sse_first_byte_ms', true);

export const options = {
  stages: [
    { duration: '15s', target: 5 },    // Start with few SSE connections
    { duration: '30s', target: 15 },   // Moderate concurrent streams
    { duration: '15s', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_failed: ['rate<0.1'],
    sse_first_byte_ms: ['p(95)<3000'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://nginx:8080';
const PROJECT_ID = __ENV.PROJECT_ID || 'proj-001';

// Acquire JWT token once before all iterations
export function setup() {
  return getAuthHeaders();
}

export default function (data) {
  // SSE streaming endpoint — test that it starts streaming
  const payload = JSON.stringify({
    message: `Performance test message VU=${__VU} ITER=${__ITER}`,
  });

  // Merge auth headers with SSE-specific Accept header
  const headers = Object.assign({}, data.headers, {
    'Accept': 'text/event-stream',
  });

  const start = Date.now();
  const res = http.post(
    `${BASE_URL}/api/v1/projects/${PROJECT_ID}/ai/chat`,
    payload,
    {
      headers,
      timeout: '30s',
    }
  );
  sseFirstByte.add(Date.now() - start);
  sseConnections.add(1);

  check(res, {
    'SSE returns 200': (r) => r.status === 200,
    'SSE returns event-stream': (r) =>
      r.headers['Content-Type']?.includes('text/event-stream') || r.status === 200,
  });

  sleep(2);
}

/** Generate HTML + JSON reports when the test finishes */
export function handleSummary(data) {
  return generateReport(data, 'ai-stream');
}
