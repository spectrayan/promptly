import { computed, Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { CreatePromptRequest, UpdatePromptRequest } from '@promptly/client';
import {
  selectAllPrompts,
  selectSelectedPrompt,
  selectPromptVersions,
  selectPromptsLoading,
  selectPromptsSaving,
  selectPromptsError,
  selectPromptCount,
} from './prompts.selectors';
import * as PromptsActions from './prompts.actions';

/**
 * Facade for the Prompt Registry feature — the **single API surface** for all
 * prompt-related state access and command dispatch.
 *
 * Uses NgRx store internally. Exposes Angular signals for `OnPush` components.
 * Components must inject this facade instead of `@promptly/client` services directly.
 *
 * @remarks
 * Also used by the Settings page to manage `__system__` project prompts.
 *
 * @see {@link ../../shared/constants/system.constants.ts} for system prompt naming
 */
@Injectable({ providedIn: 'root' })
export class PromptsFacade {
  private readonly store = inject(Store);

  // ── Read (signals for OnPush) ─────────────────────────────────
  readonly prompts    = this.store.selectSignal(selectAllPrompts);
  readonly selected   = this.store.selectSignal(selectSelectedPrompt);
  readonly versions   = this.store.selectSignal(selectPromptVersions);
  readonly loading    = this.store.selectSignal(selectPromptsLoading);
  readonly saving     = this.store.selectSignal(selectPromptsSaving);
  readonly error      = this.store.selectSignal(selectPromptsError);

  // ── Derived ───────────────────────────────────────────────────
  readonly count = this.store.selectSignal(selectPromptCount);

  // ── Commands ──────────────────────────────────────────────────
  loadPrompts(projectId?: string): void { this.store.dispatch(PromptsActions.loadPrompts({ projectId })); }
  loadPrompt(id: string): void { this.store.dispatch(PromptsActions.loadPrompt({ id })); }

  createPrompt(request: CreatePromptRequest): void {
    this.store.dispatch(PromptsActions.createPrompt({ request }));
  }

  updatePrompt(id: string, request: UpdatePromptRequest): void {
    this.store.dispatch(PromptsActions.updatePrompt({ id, request }));
  }

  deletePrompt(id: string): void {
    this.store.dispatch(PromptsActions.deletePrompt({ id }));
  }

  rollbackPrompt(id: string, targetVersion: number): void {
    this.store.dispatch(PromptsActions.rollbackPrompt({ id, targetVersion }));
  }

  clearSelection(): void {
    this.store.dispatch(PromptsActions.clearSelectedPrompt());
  }
}
