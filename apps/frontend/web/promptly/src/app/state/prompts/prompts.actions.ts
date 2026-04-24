import { createAction, props } from '@ngrx/store';
import {
  CreatePromptRequest,
  PromptResponse,
  PromptSummaryResponse,
  UpdatePromptRequest,
  VersionResponse,
} from '@promptly/client';

// ── Load All ────────────────────────────────────────────────────────
export const loadPrompts = createAction('[Prompts] Load Prompts');
export const loadPromptsSuccess = createAction('[Prompts] Load Prompts Success', props<{ prompts: PromptSummaryResponse[] }>());
export const loadPromptsFailure = createAction('[Prompts] Load Prompts Failure', props<{ error: string }>());

// ── Load One ────────────────────────────────────────────────────────
export const loadPrompt = createAction('[Prompts] Load Prompt', props<{ id: string }>());
export const loadPromptSuccess = createAction('[Prompts] Load Prompt Success', props<{ prompt: PromptResponse }>());
export const loadPromptFailure = createAction('[Prompts] Load Prompt Failure', props<{ error: string }>());

// ── Create ──────────────────────────────────────────────────────────
export const createPrompt = createAction('[Prompts] Create Prompt', props<{ request: CreatePromptRequest }>());
export const createPromptSuccess = createAction('[Prompts] Create Prompt Success', props<{ prompt: PromptResponse }>());
export const createPromptFailure = createAction('[Prompts] Create Prompt Failure', props<{ error: string }>());

// ── Update ──────────────────────────────────────────────────────────
export const updatePrompt = createAction('[Prompts] Update Prompt', props<{ id: string; request: UpdatePromptRequest }>());
export const updatePromptSuccess = createAction('[Prompts] Update Prompt Success', props<{ prompt: PromptResponse }>());
export const updatePromptFailure = createAction('[Prompts] Update Prompt Failure', props<{ error: string }>());

// ── Delete ──────────────────────────────────────────────────────────
export const deletePrompt = createAction('[Prompts] Delete Prompt', props<{ id: string }>());
export const deletePromptSuccess = createAction('[Prompts] Delete Prompt Success', props<{ id: string }>());
export const deletePromptFailure = createAction('[Prompts] Delete Prompt Failure', props<{ error: string }>());

// ── Rollback ────────────────────────────────────────────────────────
export const rollbackPrompt = createAction('[Prompts] Rollback Prompt', props<{ id: string; targetVersion: number }>());
export const rollbackPromptSuccess = createAction('[Prompts] Rollback Prompt Success', props<{ prompt: PromptResponse }>());
export const rollbackPromptFailure = createAction('[Prompts] Rollback Prompt Failure', props<{ error: string }>());

// ── Versions ────────────────────────────────────────────────────────
export const loadVersions = createAction('[Prompts] Load Versions', props<{ id: string }>());
export const loadVersionsSuccess = createAction('[Prompts] Load Versions Success', props<{ versions: VersionResponse[] }>());
export const loadVersionsFailure = createAction('[Prompts] Load Versions Failure', props<{ error: string }>());

// ── Clear Selection ─────────────────────────────────────────────────
export const clearSelectedPrompt = createAction('[Prompts] Clear Selected Prompt');
