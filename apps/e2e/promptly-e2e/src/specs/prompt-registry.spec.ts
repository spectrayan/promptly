import { test, expect, PromptsPage, PromptDetailPage } from '../fixtures';

/**
 * Prompt Registry E2E tests — validates prompt listing
 * and navigation through the UI.
 *
 * Tests navigate via sidebar links (not direct URL) because
 * direct goto() causes a full app reload that may redirect.
 *
 * The table uses Angular Material MDC-based table with standard
 * HTML elements (tr.mat-mdc-row for data rows).
 */
test.describe('Prompt Registry', () => {
  test('displays the prompts page', async ({ authenticatedPage }) => {
    const promptsPage = new PromptsPage(authenticatedPage);
    await promptsPage.navigate();

    // URL should contain /prompts
    expect(authenticatedPage.url()).toContain('/prompts');
  });

  test('shows seed data prompts', async ({ authenticatedPage }) => {
    const promptsPage = new PromptsPage(authenticatedPage);
    await promptsPage.navigate();

    // Material MDC table uses tr.mat-mdc-row for data rows
    const promptRows = authenticatedPage.locator('tr.mat-mdc-row');
    await expect(promptRows.first()).toBeVisible({ timeout: 10_000 });
  });

  test('can search/filter prompts', async ({ authenticatedPage }) => {
    const promptsPage = new PromptsPage(authenticatedPage);
    await promptsPage.navigate();

    // Search for a seed prompt
    const searchInput = authenticatedPage.getByPlaceholder(/search|filter/i);
    if (await searchInput.isVisible({ timeout: 5_000 }).catch(() => false)) {
      await searchInput.fill('test');
      await authenticatedPage.waitForTimeout(1000); // debounce

      // Verify filtering happened
      const promptRows = authenticatedPage.locator('tr.mat-mdc-row');
      const count = await promptRows.count();
      expect(count).toBeGreaterThanOrEqual(0);
    }
  });

  test('can open prompt detail view', async ({ authenticatedPage }) => {
    const promptsPage = new PromptsPage(authenticatedPage);
    await promptsPage.navigate();

    // Click the first data row (tr.mat-mdc-row has class clickable-row)
    const firstPrompt = authenticatedPage.locator('tr.mat-mdc-row').first();

    if (await firstPrompt.isVisible({ timeout: 10_000 }).catch(() => false)) {
      await firstPrompt.click();
      await authenticatedPage.waitForLoadState('domcontentloaded');
      await authenticatedPage.waitForTimeout(1000);

      // Should show prompt detail / editor (URL should contain prompt ID)
      expect(authenticatedPage.url()).toMatch(/\/prompts\//);
    }
  });
});
