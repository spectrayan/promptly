import { type Page, type Locator } from '@playwright/test';

/**
 * Page Object Models for Promptly E2E tests.
 * Each POM encapsulates selectors and common actions for a page.
 *
 * All content routes are project-scoped (e.g. /projects/:id/prompts).
 * POMs default to seed-data project 'proj-001' (customer-ops).
 *
 * IMPORTANT: Promptly uses SSE (Server-Sent Events) for real-time updates.
 * Do NOT use waitForLoadState('networkidle') — SSE keeps the connection
 * open permanently. Use 'domcontentloaded' or 'load' instead.
 *
 * Navigation should be done via sidebar links, not direct URL goto(),
 * because goto() causes a full app reload that re-triggers session
 * restore and may redirect to the dashboard.
 */

const DEFAULT_PROJECT_ID = 'proj-001';

// ── Dashboard / Projects Page ─────────────────────────────
export class ProjectsPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly projectCards: Locator;
  readonly projectDropdown: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.getByRole('heading', { name: /welcome|dashboard/i });
    this.projectCards = page.locator('[data-testid="project-card"], mat-card, .stat-card');
    this.projectDropdown = page.locator('button').filter({ hasText: /arrow_drop_down/ }).first();
  }

  async navigate() {
    await this.page.goto('/dashboard', { waitUntil: 'domcontentloaded' });
    // Wait for main content to appear
    await this.heading.waitFor({ state: 'visible', timeout: 15_000 });
  }

  async selectProject(name: string) {
    // Use the project dropdown in the top navbar
    await this.projectDropdown.click();
    await this.page.getByText(name, { exact: false }).click();
    await this.page.waitForLoadState('domcontentloaded');
  }
}

// ── Prompts Page ──────────────────────────────────────────
export class PromptsPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly promptList: Locator;
  readonly createPromptButton: Locator;
  readonly searchInput: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.locator('h1, h2').filter({ hasText: /prompts/i }).first();
    this.promptList = page.locator('mat-table, table, [data-testid="prompt-list"]');
    this.createPromptButton = page.getByRole('button', { name: /create|new prompt/i });
    this.searchInput = page.getByPlaceholder(/search|filter/i);
  }

  /** Navigate to prompts page via sidebar link (avoids full reload) */
  async navigate(_projectId?: string) {
    // Click the Prompts sidebar link
    const promptsLink = this.page.getByRole('link', { name: 'Prompts', exact: true });
    await promptsLink.click();
    await this.page.waitForLoadState('domcontentloaded');
    // Wait for heading or table to appear
    await this.page.waitForTimeout(2000);
  }

  async createPrompt(name: string, content: string) {
    await this.createPromptButton.click();
    await this.page.getByLabel(/name/i).fill(name);

    // Monaco editor — click to focus, then type
    const editor = this.page.locator('.monaco-editor');
    if (await editor.isVisible()) {
      await editor.click();
      await this.page.keyboard.type(content);
    }
  }

  async searchPrompt(query: string) {
    await this.searchInput.fill(query);
    await this.page.waitForTimeout(500); // debounce
  }
}

// ── Prompt Detail Page ────────────────────────────────────
export class PromptDetailPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly versionHistory: Locator;
  readonly saveButton: Locator;
  readonly deleteButton: Locator;
  readonly editor: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.getByRole('heading').first();
    this.versionHistory = page.locator('[data-testid="version-history"]');
    this.saveButton = page.getByRole('button', { name: /save/i });
    this.deleteButton = page.getByRole('button', { name: /delete/i });
    this.editor = page.locator('.monaco-editor');
  }

  async editContent(newContent: string) {
    await this.editor.click();
    await this.page.keyboard.press('Control+A');
    await this.page.keyboard.type(newContent);
  }

  async save() {
    await this.saveButton.click();
    await this.page.waitForLoadState('domcontentloaded');
  }
}

// ── Workflows Page ────────────────────────────────────────
export class WorkflowsPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly workflowList: Locator;
  readonly createWorkflowButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.locator('h1, h2').filter({ hasText: /workflows/i }).first();
    this.workflowList = page.locator('mat-table, table, [data-testid="workflow-list"]');
    this.createWorkflowButton = page.getByRole('button', { name: /create|new workflow/i });
  }

  /** Navigate to workflows page via sidebar link (avoids full reload) */
  async navigate(_projectId?: string) {
    // Click the Workflows sidebar link
    const workflowsLink = this.page.getByRole('link', { name: 'Workflows', exact: true });
    await workflowsLink.click();
    await this.page.waitForLoadState('domcontentloaded');
    await this.page.waitForTimeout(2000);
  }
}

// ── Navigation Helper ─────────────────────────────────────
export class AppNavigation {
  readonly page: Page;
  readonly sidebar: Locator;

  constructor(page: Page) {
    this.page = page;
    this.sidebar = page.locator('mat-sidenav, [data-testid="sidebar"]');
  }

  async navigateToSection(name: string) {
    const link = this.sidebar.getByText(name, { exact: false });
    if (await link.isVisible()) {
      await link.click();
    } else {
      // Try the hamburger menu first
      const menuButton = this.page.getByRole('button', { name: /menu/i });
      if (await menuButton.isVisible()) {
        await menuButton.click();
        await this.sidebar.getByText(name, { exact: false }).click();
      }
    }
    await this.page.waitForLoadState('domcontentloaded');
  }
}
