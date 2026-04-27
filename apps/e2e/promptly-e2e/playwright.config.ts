import { defineConfig, devices } from '@playwright/test';

/**
 * Promptly — E2E Test Configuration
 *
 * Tests run against the fully orchestrated local stack:
 *   - MongoDB (Docker)
 *   - Spring Boot backend (:8080)
 *   - Angular dev server (:4200)
 *
 * The run-e2e.ps1 script handles startup/teardown.
 */
export default defineConfig({
  testDir: './src/specs',
  fullyParallel: false,
  forbidOnly: !!process.env['CI'],
  retries: process.env['CI'] ? 2 : 0,
  workers: 1,
  reporter: [
    ['html', { outputFolder: '../../../reports/e2e', open: 'never' }],
    ['list'],
  ],
  timeout: 60_000,

  use: {
    baseURL: 'http://localhost:4200',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
    actionTimeout: 15_000,
    navigationTimeout: 30_000,
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],

  /* Do NOT use webServer — the run-e2e.ps1 script manages services */
});
