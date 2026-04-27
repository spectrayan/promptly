import { test, expect } from '../fixtures';

/**
 * Smoke test — validates that the full stack is running and
 * the Angular app loads successfully.
 */
test.describe('Smoke Tests', () => {
  test('app loads and shows the main page', async ({ page }) => {
    // Register console error listener BEFORE navigation
    const errors: string[] = [];
    page.on('console', (msg) => {
      if (msg.type() === 'error') errors.push(msg.text());
    });

    await page.goto('/', { waitUntil: 'domcontentloaded' });

    // The app should render — we may be on login or dashboard
    const title = await page.title();
    expect(title).toBeTruthy();

    // Allow time for any deferred errors
    await page.waitForTimeout(2000);

    // No unhandled console errors (ignore favicon 404s)
    expect(errors.filter((e) => !e.includes('favicon'))).toHaveLength(0);
  });

  test('backend health check responds', async ({ request }) => {
    const response = await request.get('http://localhost:8080/actuator/health');
    expect(response.ok()).toBeTruthy();

    const body = await response.json();
    expect(body.status).toBe('UP');
  });

  test('auth login API works', async ({ request }) => {
    const response = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: {
        email: 'alice@promptly.ai',
        password: 'password123',
      },
    });
    expect(response.ok()).toBeTruthy();

    const body = await response.json();
    expect(body.accessToken).toBeTruthy();
    expect(body.user).toBeTruthy();
    expect(body.user.email).toBe('alice@promptly.ai');
  });

  test('API returns projects list when authenticated', async ({ request }) => {
    // Login first to get a token
    const loginRes = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: { email: 'alice@promptly.ai', password: 'password123' },
    });
    const { accessToken } = await loginRes.json();

    const response = await request.get('http://localhost:8080/api/v1/projects', {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    expect(response.ok()).toBeTruthy();
  });
});
