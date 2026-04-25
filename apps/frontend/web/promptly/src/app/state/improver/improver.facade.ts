import { Injectable, inject, signal } from '@angular/core';
import { ImproverService, ImprovementResponse } from '@promptly/client';

/**
 * Improver facade — signal-based (no NgRx).
 *
 * Wraps the ImproverService SDK to provide AI-powered prompt generation
 * and refinement. Exposes reactive signals for component consumption.
 *
 * API endpoints:
 * - POST /api/v1/prompts/{promptId}/improve  → improvePrompt
 * - POST /api/v1/prompts/{promptId}/apply-improvement → applyImprovement
 */
@Injectable({ providedIn: 'root' })
export class ImproverFacade {
  private readonly api = inject(ImproverService);

  // ── Reactive state ────────────────────────────────────────────
  readonly improving = signal(false);
  readonly applying = signal(false);
  readonly suggestion = signal<ImprovementResponse | null>(null);
  readonly error = signal<string | null>(null);

  /**
   * Request AI improvement suggestions for an existing prompt.
   * Uses LLM to analyze the prompt and generate an improved version.
   *
   * @param promptId The ID of the prompt to improve
   */
  improvePrompt(promptId: string): void {
    this.improving.set(true);
    this.error.set(null);
    this.suggestion.set(null);

    this.api.improvePrompt({ promptId }).subscribe({
      next: (response: ImprovementResponse) => {
        this.suggestion.set(response);
        this.improving.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'AI improvement failed');
        this.improving.set(false);
      },
    });
  }

  /**
   * Accept the suggested improvement and create a new prompt version.
   *
   * @param promptId The ID of the prompt to apply improvement to
   * @param improvedContent The improved content to apply
   * @param author The author applying the improvement
   */
  applyImprovement(promptId: string, improvedContent: string, author: string): void {
    this.applying.set(true);
    this.error.set(null);

    this.api.applyImprovement({
      promptId,
      applyImprovementRequest: { improvedContent, author },
    }).subscribe({
      next: () => {
        this.applying.set(false);
        this.clearSuggestion();
      },
      error: (err) => {
        this.error.set(err?.error?.detail ?? 'Failed to apply improvement');
        this.applying.set(false);
      },
    });
  }

  /** Clear the current suggestion */
  clearSuggestion(): void {
    this.suggestion.set(null);
    this.error.set(null);
  }
}
