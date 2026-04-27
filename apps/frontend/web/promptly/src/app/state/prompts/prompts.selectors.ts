/** Memoized selectors for the Prompt Registry feature slice. Consumed by PromptsFacade. */
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { PromptsState } from './prompts.state';

export const selectPromptsState = createFeatureSelector<PromptsState>('prompts');

export const selectAllPrompts = createSelector(selectPromptsState, s => s.prompts);
export const selectSelectedPrompt = createSelector(selectPromptsState, s => s.selectedPrompt);
export const selectPromptsLoading = createSelector(selectPromptsState, s => s.loading);
export const selectPromptsSaving = createSelector(selectPromptsState, s => s.saving);
export const selectPromptsError = createSelector(selectPromptsState, s => s.error);

export const selectPromptVersions = createSelector(
  selectPromptsState,
  state => state.versions
);

export const selectPromptCount = createSelector(selectAllPrompts, prompts => prompts.length);

export const selectPromptsByProject = (projectId: string) =>
  createSelector(selectAllPrompts, prompts => prompts.filter(p => p.projectId === projectId));
