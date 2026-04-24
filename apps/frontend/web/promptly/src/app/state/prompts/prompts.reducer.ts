import { createReducer, on } from '@ngrx/store';
import { PromptsState, initialPromptsState } from './prompts.state';
import * as PromptsActions from './prompts.actions';

export const promptsReducer = createReducer(
  initialPromptsState,

  // ── Load All ────────────────────────────────────────────────────
  on(PromptsActions.loadPrompts, (state): PromptsState => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(PromptsActions.loadPromptsSuccess, (state, { prompts }): PromptsState => ({
    ...state,
    prompts,
    loading: false,
  })),
  on(PromptsActions.loadPromptsFailure, (state, { error }): PromptsState => ({
    ...state,
    loading: false,
    error,
  })),

  // ── Load One ────────────────────────────────────────────────────
  on(PromptsActions.loadPrompt, (state): PromptsState => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(PromptsActions.loadPromptSuccess, (state, { prompt }): PromptsState => ({
    ...state,
    selectedPrompt: prompt,
    loading: false,
  })),
  on(PromptsActions.loadPromptFailure, (state, { error }): PromptsState => ({
    ...state,
    loading: false,
    error,
  })),

  // ── Create ──────────────────────────────────────────────────────
  on(PromptsActions.createPrompt, (state): PromptsState => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(PromptsActions.createPromptSuccess, (state, { prompt }): PromptsState => ({
    ...state,
    prompts: [{ ...prompt } as any, ...state.prompts],
    selectedPrompt: prompt,
    saving: false,
  })),
  on(PromptsActions.createPromptFailure, (state, { error }): PromptsState => ({
    ...state,
    saving: false,
    error,
  })),

  // ── Update ──────────────────────────────────────────────────────
  on(PromptsActions.updatePrompt, (state): PromptsState => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(PromptsActions.updatePromptSuccess, (state, { prompt }): PromptsState => ({
    ...state,
    selectedPrompt: prompt,
    prompts: state.prompts.map(p => p.id === prompt.id ? { ...p, ...prompt } as any : p),
    saving: false,
  })),
  on(PromptsActions.updatePromptFailure, (state, { error }): PromptsState => ({
    ...state,
    saving: false,
    error,
  })),

  // ── Delete ──────────────────────────────────────────────────────
  on(PromptsActions.deletePromptSuccess, (state, { id }): PromptsState => ({
    ...state,
    prompts: state.prompts.filter(p => p.id !== id),
    selectedPrompt: state.selectedPrompt?.id === id ? null : state.selectedPrompt,
  })),

  // ── Rollback ────────────────────────────────────────────────────
  on(PromptsActions.rollbackPrompt, (state): PromptsState => ({
    ...state,
    saving: true,
  })),
  on(PromptsActions.rollbackPromptSuccess, (state, { prompt }): PromptsState => ({
    ...state,
    selectedPrompt: prompt,
    saving: false,
  })),
  on(PromptsActions.rollbackPromptFailure, (state, { error }): PromptsState => ({
    ...state,
    saving: false,
    error,
  })),

  // ── Versions ─────────────────────────────────────────────────────
  on(PromptsActions.loadVersionsSuccess, (state, { versions }): PromptsState => ({
    ...state,
    versions,
  })),

  // ── Clear Selection ─────────────────────────────────────────────
  on(PromptsActions.clearSelectedPrompt, (state): PromptsState => ({
    ...state,
    selectedPrompt: null,
    versions: [],
  })),
);
