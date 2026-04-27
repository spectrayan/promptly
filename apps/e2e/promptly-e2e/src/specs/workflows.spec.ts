import { test, expect, WorkflowsPage } from '../fixtures';

/**
 * Workflow module E2E tests — validates workflow listing
 * and basic workflow interactions.
 *
 * The table uses Angular Material MDC-based table with standard
 * HTML elements (tr.mat-mdc-row for data rows).
 */
test.describe('Workflows', () => {
  test('displays the workflows page', async ({ authenticatedPage }) => {
    const workflowsPage = new WorkflowsPage(authenticatedPage);
    await workflowsPage.navigate();

    // URL should contain /workflows
    expect(authenticatedPage.url()).toContain('/workflows');
  });

  test('shows seed data workflows', async ({ authenticatedPage }) => {
    const workflowsPage = new WorkflowsPage(authenticatedPage);
    await workflowsPage.navigate();

    // Material MDC table uses tr.mat-mdc-row for data rows
    const workflowRows = authenticatedPage.locator('tr.mat-mdc-row');
    await expect(workflowRows.first()).toBeVisible({ timeout: 10_000 });
  });
});
