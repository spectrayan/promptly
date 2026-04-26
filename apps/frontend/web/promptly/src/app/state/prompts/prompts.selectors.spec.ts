import { selectAllPrompts, selectSelectedPrompt, selectPromptsLoading, selectPromptsSaving, selectPromptsError, selectPromptVersions, selectPromptCount, selectPromptsByProject } from './prompts.selectors';
import { PromptsState } from './prompts.state';
import { PromptSummaryResponse, PromptResponse, VersionResponse } from '@promptly/client';

/**
 * Unit tests for prompts NgRx selectors.
 * Selectors are pure functions — no TestBed needed.
 */
describe('Prompts Selectors', () => {

  const mockSummary1: PromptSummaryResponse = {
    id: 'p-1', name: 'Prompt 1', projectId: 'proj-1', status: 'DRAFT',
  } as PromptSummaryResponse;

  const mockSummary2: PromptSummaryResponse = {
    id: 'p-2', name: 'Prompt 2', projectId: 'proj-2', status: 'APPROVED',
  } as PromptSummaryResponse;

  const mockPrompt: PromptResponse = {
    id: 'p-1', name: 'Prompt 1', projectId: 'proj-1',
  } as PromptResponse;

  const mockVersion: VersionResponse = {
    versionNumber: 1, content: 'Hello',
  } as VersionResponse;

  const state: PromptsState = {
    prompts: [mockSummary1, mockSummary2],
    selectedPrompt: mockPrompt,
    versions: [mockVersion],
    loading: false,
    saving: true,
    error: 'some error',
  };

  it('selectAllPrompts should return all prompts', () => {
    expect(selectAllPrompts.projector(state)).toEqual([mockSummary1, mockSummary2]);
  });

  it('selectSelectedPrompt should return selected', () => {
    expect(selectSelectedPrompt.projector(state)).toEqual(mockPrompt);
  });

  it('selectPromptsLoading should return loading flag', () => {
    expect(selectPromptsLoading.projector(state)).toBe(false);
  });

  it('selectPromptsSaving should return saving flag', () => {
    expect(selectPromptsSaving.projector(state)).toBe(true);
  });

  it('selectPromptsError should return error', () => {
    expect(selectPromptsError.projector(state)).toBe('some error');
  });

  it('selectPromptVersions should return versions array', () => {
    expect(selectPromptVersions.projector(state)).toEqual([mockVersion]);
  });

  it('selectPromptCount should return count of prompts', () => {
    expect(selectPromptCount.projector([mockSummary1, mockSummary2])).toBe(2);
  });

  it('selectPromptsByProject should filter by projectId', () => {
    const selector = selectPromptsByProject('proj-1');
    expect(selector.projector([mockSummary1, mockSummary2])).toEqual([mockSummary1]);
  });

  it('selectPromptsByProject should return empty for unknown project', () => {
    const selector = selectPromptsByProject('unknown');
    expect(selector.projector([mockSummary1, mockSummary2])).toEqual([]);
  });
});
