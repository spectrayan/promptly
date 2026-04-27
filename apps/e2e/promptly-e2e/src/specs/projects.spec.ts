import { test, expect, ProjectsPage } from '../fixtures';

/**
 * Projects module E2E tests — validates the dashboard displays
 * project information and seed data is accessible.
 *
 * Note: Promptly uses a project-scoped dashboard, not a separate
 * project list page. The project selector is in the top navbar.
 */
test.describe('Projects', () => {
  test('displays the dashboard page', async ({ authenticatedPage }) => {
    const projectsPage = new ProjectsPage(authenticatedPage);
    await projectsPage.navigate();

    await expect(projectsPage.heading).toBeVisible();
  });

  test('shows project stats on dashboard', async ({ authenticatedPage }) => {
    const projectsPage = new ProjectsPage(authenticatedPage);
    await projectsPage.navigate();

    // Dashboard should show stat cards
    const statCards = authenticatedPage.locator('.stat-card, mat-card');
    await expect(statCards.first()).toBeVisible({ timeout: 10_000 });
  });

  test('project dropdown is visible in navbar', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/dashboard', { waitUntil: 'domcontentloaded' });
    await authenticatedPage.waitForTimeout(2000);

    // The project dropdown should be visible in the top bar
    const projectButton = authenticatedPage.locator('button').filter({ hasText: /arrow_drop_down/ }).first();
    await expect(projectButton).toBeVisible({ timeout: 10_000 });
  });

  test('sidebar shows project navigation links', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/dashboard', { waitUntil: 'domcontentloaded' });
    await authenticatedPage.waitForTimeout(2000);

    // Use exact name to avoid matching "Search Prompts" quick action
    const promptsLink = authenticatedPage.getByRole('link', { name: 'Prompts', exact: true });
    await expect(promptsLink).toBeVisible({ timeout: 10_000 });
  });
});
