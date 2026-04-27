import { test as base, expect, type Page } from '@playwright/test';

/**
 * Authentication fixture for Promptly E2E tests.
 *
 * Authenticates via the backend JWT API (/api/v1/auth/login)
 * and injects the token into localStorage so the Angular app
 * picks it up automatically. Falls back to the UI login form
 * if the API approach fails.
 *
 * Credentials default to seed-data values; override with env vars.
 *
 * IMPORTANT: Do NOT use waitForLoadState('networkidle') — the app
 * opens SSE connections that keep the network permanently active.
 */

// Seed data defaults (matches seed-data/users/users.json + login page test credentials)
const DEFAULT_EMAIL = 'alice@promptly.ai';
const DEFAULT_PASSWORD = 'password123';

// LocalStorage keys used by the Angular app (see auth.guard.ts and auth.effects.ts)
const TOKEN_KEY = 'promptly_access_token';
const REFRESH_KEY = 'promptly_refresh_token';

const API_BASE = 'http://localhost:8080';

export type AuthFixtures = {
  authenticatedPage: Page;
};

export const test = base.extend<AuthFixtures>({
  authenticatedPage: async ({ page }, use) => {
    const email = process.env['E2E_USER_EMAIL'] || DEFAULT_EMAIL;
    const password = process.env['E2E_USER_PASSWORD'] || DEFAULT_PASSWORD;

    // ── Strategy 1: API-based auth (fast, reliable) ────────
    try {
      const loginResponse = await page.request.post(`${API_BASE}/api/v1/auth/login`, {
        data: { email, password },
      });

      if (loginResponse.ok()) {
        const authData = await loginResponse.json();
        const { accessToken, refreshToken } = authData;

        // Navigate to app origin first (localStorage is origin-scoped)
        await page.goto('/', { waitUntil: 'commit' });

        // Inject auth tokens using the EXACT keys the Angular app expects
        await page.evaluate(
          ({ accessToken, refreshToken, tokenKey, refreshKey }) => {
            localStorage.setItem(tokenKey, accessToken);
            localStorage.setItem(refreshKey, refreshToken);
          },
          { accessToken, refreshToken, tokenKey: TOKEN_KEY, refreshKey: REFRESH_KEY },
        );

        // Navigate to dashboard — the auth guard will now find the token
        await page.goto('/dashboard', { waitUntil: 'domcontentloaded' });

        // Wait for the app to load authenticated content
        await page.waitForSelector(
          'text=/Welcome|Dashboard/i',
          { timeout: 15_000 },
        ).catch(() => {});

        // Verify we're past the login page
        const url = page.url();
        if (!url.includes('/login') && !url.includes('/register')) {
          await use(page);
          return;
        }
      }
    } catch {
      // API auth failed — fall back to UI login
    }

    // ── Strategy 2: UI login form fallback ─────────────────
    await page.goto('/login', { waitUntil: 'domcontentloaded' });

    await page.locator('#login-email').fill(email);
    await page.locator('#login-password').fill(password);
    await page.locator('#login-submit').click();

    // Wait for navigation away from login
    await page.waitForURL('**/dashboard**', { timeout: 15_000 });

    await use(page);
  },
});

export { expect };
