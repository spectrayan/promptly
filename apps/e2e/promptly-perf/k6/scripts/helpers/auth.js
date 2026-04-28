/**
 * Promptly — k6 Auth Helper
 *
 * Acquires a JWT token from the backend login API.
 * Retries on transient failures (5xx) to handle cold-start race conditions.
 * Returns null if login ultimately fails so callers can abort gracefully.
 *
 * Usage:
 *   import { getAuthHeaders, requireAuth } from './helpers/auth.js';
 *   export function setup() { return requireAuth(); }
 *   export default function (data) {
 *     const res = http.get(url, { headers: data.headers });
 *   }
 */
import http from 'k6/http';
import { check, fail } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://nginx:8080';
const EMAIL    = __ENV.AUTH_EMAIL    || 'alice@promptly.ai';
const PASSWORD = __ENV.AUTH_PASSWORD || 'password123';
const MAX_RETRIES = 5;
const RETRY_DELAY_MS = 2000;

/**
 * Calls POST /api/v1/auth/login and returns headers with
 * the Bearer token pre-set for subsequent requests.
 *
 * Retries up to MAX_RETRIES times on 5xx errors to handle
 * cold-start or transient failures.
 *
 * @returns {{ headers: object, token: string } | null}
 */
export function getAuthHeaders() {
  let lastStatus = 0;
  let lastBody = '';

  for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
    const loginRes = http.post(
      `${BASE_URL}/api/v1/auth/login`,
      JSON.stringify({ email: EMAIL, password: PASSWORD }),
      {
        headers: { 'Content-Type': 'application/json' },
        timeout: '30s',
      }
    );

    lastStatus = loginRes.status;
    lastBody = loginRes.body;

    const ok = check(loginRes, {
      'login returns 200': (r) => r.status === 200,
      'login returns accessToken': (r) => {
        try { return !!JSON.parse(r.body).accessToken; }
        catch { return false; }
      },
    });

    if (ok) {
      const { accessToken } = JSON.parse(loginRes.body);
      console.log(`Login successful on attempt ${attempt}`);
      return {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': `Bearer ${accessToken}`,
        },
        token: accessToken,
        authenticated: true,
      };
    }

    // Only retry on 5xx or 0 (timeout/connection refused) — 4xx means bad credentials
    if (loginRes.status >= 400 && loginRes.status < 500) {
      console.error(`Login failed with client error ${loginRes.status}: ${loginRes.body}`);
      break;
    }

    console.warn(`Login attempt ${attempt}/${MAX_RETRIES} failed (${loginRes.status}). Retrying in ${RETRY_DELAY_MS}ms...`);

    // k6 doesn't have a blocking sleep in setup, use a busy-wait
    const deadline = Date.now() + RETRY_DELAY_MS;
    while (Date.now() < deadline) { /* wait */ }
  }

  console.error(`Login failed after ${MAX_RETRIES} attempts: ${lastStatus} — ${lastBody}`);
  return {
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    token: null,
    authenticated: false,
  };
}

/**
 * Same as getAuthHeaders() but calls fail() if login doesn't succeed,
 * which aborts the test immediately. Use this when there's no point
 * running the test without authentication.
 */
export function requireAuth() {
  const auth = getAuthHeaders();
  if (!auth || !auth.authenticated) {
    fail('Authentication failed — cannot proceed with test. Check backend logs for the root cause.');
  }
  return auth;
}
