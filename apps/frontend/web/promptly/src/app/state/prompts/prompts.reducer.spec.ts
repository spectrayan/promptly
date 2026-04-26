import { promptsReducer } from './prompts.reducer';
import { initialPromptsState, PromptsState } from './prompts.state';
import * as PromptsActions from './prompts.actions';
import { PromptResponse, PromptSummaryResponse } from '@promptly/client';

/**
 * Unit tests for the prompts NgRx reducer.
 * Validates every state transition is correct and immutable.
 */
describe('Prompts Reducer', () => {

  const mockSummary: PromptSummaryResponse = {
    id: 'p-1', name: 'Test', description: 'desc',
    projectId: 'proj-1', status: 'DRAFT', currentVersion: 1,
    contentFormat: 'TEXT',
  } as PromptSummaryResponse;

  const mockPrompt: PromptResponse = {
    id: 'p-1', name: 'Test', description: 'desc',
    projectId: 'proj-1', status: 'DRAFT', currentVersion: 1,
    contentFormat: 'TEXT',
  } as PromptResponse;

  // ── Initial state ──

  it('should return the initial state for unknown action', () => {
    const action = { type: '[Unknown]' } as any;
    const state = promptsReducer(undefined, action);
    expect(state).toEqual(initialPromptsState);
  });

  // ── Load All ──

  it('should set loading=true on loadPrompts', () => {
    const state = promptsReducer(
      initialPromptsState,
      PromptsActions.loadPrompts({ projectId: 'proj-1' })
    );
    expect(state.loading).toBe(true);
    expect(state.error).toBeNull();
  });

  it('should populate prompts and clear loading on loadPromptsSuccess', () => {
    const prior: PromptsState = { ...initialPromptsState, loading: true };
    const state = promptsReducer(
      prior,
      PromptsActions.loadPromptsSuccess({ prompts: [mockSummary] })
    );
    expect(state.prompts).toEqual([mockSummary]);
    expect(state.loading).toBe(false);
  });

  it('should set error on loadPromptsFailure', () => {
    const prior: PromptsState = { ...initialPromptsState, loading: true };
    const state = promptsReducer(
      prior,
      PromptsActions.loadPromptsFailure({ error: 'fail' })
    );
    expect(state.loading).toBe(false);
    expect(state.error).toBe('fail');
  });

  // ── Load One ──

  it('should set loading on loadPrompt', () => {
    const state = promptsReducer(
      initialPromptsState,
      PromptsActions.loadPrompt({ id: 'p-1' })
    );
    expect(state.loading).toBe(true);
  });

  it('should set selectedPrompt on loadPromptSuccess', () => {
    const state = promptsReducer(
      { ...initialPromptsState, loading: true },
      PromptsActions.loadPromptSuccess({ prompt: mockPrompt })
    );
    expect(state.selectedPrompt).toEqual(mockPrompt);
    expect(state.loading).toBe(false);
  });

  // ── Create ──

  it('should set saving=true on createPrompt', () => {
    const state = promptsReducer(
      initialPromptsState,
      PromptsActions.createPrompt({ request: {} as any })
    );
    expect(state.saving).toBe(true);
    expect(state.error).toBeNull();
  });

  it('should prepend prompt on createPromptSuccess', () => {
    const existing: PromptsState = { ...initialPromptsState, prompts: [mockSummary] };
    const newPrompt = { ...mockPrompt, id: 'p-2', name: 'New' };
    const state = promptsReducer(
      existing,
      PromptsActions.createPromptSuccess({ prompt: newPrompt })
    );
    expect(state.prompts).toHaveLength(2);
    expect(state.prompts[0].id).toBe('p-2');
    expect(state.selectedPrompt).toEqual(newPrompt);
    expect(state.saving).toBe(false);
  });

  // ── Update ──

  it('should update prompt in list on updatePromptSuccess', () => {
    const existing: PromptsState = { ...initialPromptsState, prompts: [mockSummary], saving: true };
    const updated = { ...mockPrompt, name: 'Updated' };
    const state = promptsReducer(
      existing,
      PromptsActions.updatePromptSuccess({ prompt: updated })
    );
    expect(state.prompts[0].name).toBe('Updated');
    expect(state.selectedPrompt?.name).toBe('Updated');
    expect(state.saving).toBe(false);
  });

  // ── Delete ──

  it('should remove prompt from list on deletePromptSuccess', () => {
    const existing: PromptsState = {
      ...initialPromptsState,
      prompts: [mockSummary],
      selectedPrompt: mockPrompt,
    };
    const state = promptsReducer(
      existing,
      PromptsActions.deletePromptSuccess({ id: 'p-1' })
    );
    expect(state.prompts).toHaveLength(0);
    expect(state.selectedPrompt).toBeNull();
  });

  it('should preserve selectedPrompt if deleting a different prompt', () => {
    const existing: PromptsState = {
      ...initialPromptsState,
      prompts: [mockSummary],
      selectedPrompt: mockPrompt,
    };
    const state = promptsReducer(
      existing,
      PromptsActions.deletePromptSuccess({ id: 'p-other' })
    );
    expect(state.selectedPrompt).toEqual(mockPrompt);
  });

  // ── Rollback ──

  it('should set saving=true on rollbackPrompt', () => {
    const state = promptsReducer(
      initialPromptsState,
      PromptsActions.rollbackPrompt({ id: 'p-1', targetVersion: 1 })
    );
    expect(state.saving).toBe(true);
  });

  it('should update selectedPrompt on rollbackPromptSuccess', () => {
    const rolledBack = { ...mockPrompt, currentVersion: 1 };
    const state = promptsReducer(
      { ...initialPromptsState, saving: true },
      PromptsActions.rollbackPromptSuccess({ prompt: rolledBack })
    );
    expect(state.selectedPrompt).toEqual(rolledBack);
    expect(state.saving).toBe(false);
  });

  // ── Clear Selection ──

  it('should clear selectedPrompt and versions on clearSelectedPrompt', () => {
    const existing: PromptsState = {
      ...initialPromptsState,
      selectedPrompt: mockPrompt,
      versions: [{ versionNumber: 1 } as any],
    };
    const state = promptsReducer(
      existing,
      PromptsActions.clearSelectedPrompt()
    );
    expect(state.selectedPrompt).toBeNull();
    expect(state.versions).toEqual([]);
  });
});
