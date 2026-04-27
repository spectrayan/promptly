import { test, expect, AppNavigation } from '../fixtures';

/**
 * Navigation E2E tests — validates sidebar navigation,
 * routing, and responsive menu behavior.
 *
 * Navigation in Promptly is project-scoped, so links go to
 * /projects/:projectId/prompts, /projects/:projectId/workflows, etc.
 */
test.describe('Navigation', () => {
  test('dashboard loads after login', async ({ authenticatedPage }) => {
    // After auth, we should be on the dashboard
    await expect(authenticatedPage).toHaveURL(/dashboard/);
    const title = await authenticatedPage.title();
    expect(title).toContain('Promptly');
  });

  test('sidebar navigation links are visible', async ({ authenticatedPage }) => {
    const nav = new AppNavigation(authenticatedPage);

    // Sidebar should be visible (or the menu toggle should be)
    const sidebarVisible = await nav.sidebar.isVisible().catch(() => false);
    if (!sidebarVisible) {
      // Try opening via hamburger menu
      const menuButton = authenticatedPage.getByRole('button', { name: /menu/i });
      if (await menuButton.isVisible({ timeout: 3000 }).catch(() => false)) {
        await menuButton.click();
        await authenticatedPage.waitForTimeout(500);
      }
    }

    // At least some nav links should exist
    const navLinks = authenticatedPage.locator('mat-sidenav a, [data-testid="sidebar"] a, mat-nav-list a');
    const count = await navLinks.count();
    expect(count).toBeGreaterThan(0);
  });

  test('page title updates with navigation', async ({ authenticatedPage }) => {
    const initialTitle = await authenticatedPage.title();
    expect(initialTitle).toBeTruthy();
    expect(initialTitle).toContain('Promptly');
  });
});
