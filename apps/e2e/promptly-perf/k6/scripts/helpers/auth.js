/**
 * Promptly — k6 Auth Helper
 *
 * Acquires a JWT token from the backend login API.
 * The token is cached for the VU lifetime via setup().
 *
 * Usage:
 *   import { getAuthHeaders } from './helpers/auth.js';
 *   export function setup() { return getAuthHeaders(); }
 *   export default function (data) {
 *     const res = http.get(url, { headers: data.headers });
 *   }
 */
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://nginx:8080';
const EMAIL    = __ENV.AUTH_EMAIL    || 'alice@promptly.ai';
const PASSWORD = __ENV.AUTH_PASSWORD || 'password123';

/**
 * Calls POST /api/v1/auth/login and returns headers with
 * the Bearer token pre-set for subsequent requests.
 */
export function getAuthHeaders() {
  const loginRes = http.post(
    `${BASE_URL}/api/v1/auth/login`,
    JSON.stringify({ email: EMAIL, password: PASSWORD }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const ok = check(loginRes, {
    'login returns 200': (r) => r.status === 200,
    'login returns accessToken': (r) => {
      try { return !!JSON.parse(r.body).accessToken; }
      catch { return false; }
    },
  });

  if (!ok) {
    console.error(`Login failed: ${loginRes.status} — ${loginRes.body}`);
    return { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } };
  }

  const { accessToken } = JSON.parse(loginRes.body);
  return {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Authorization': `Bearer ${accessToken}`,
    },
  };
}
