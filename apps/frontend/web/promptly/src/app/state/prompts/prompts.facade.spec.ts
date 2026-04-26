import { TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { PromptsFacade } from './prompts.facade';
import * as PromptsActions from './prompts.actions';
import {
  selectAllPrompts,
  selectSelectedPrompt,
  selectPromptVersions,
  selectPromptsLoading,
  selectPromptsSaving,
  selectPromptsError,
  selectPromptCount,
} from './prompts.selectors';
import { CreatePromptRequest, UpdatePromptRequest } from '@promptly/client';

describe('PromptsFacade', () => {
  let facade: PromptsFacade;
  let store: MockStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PromptsFacade,
        provideMockStore({
          selectors: [
            { selector: selectAllPrompts, value: [] },
            { selector: selectSelectedPrompt, value: null },
            { selector: selectPromptVersions, value: [] },
            { selector: selectPromptsLoading, value: false },
            { selector: selectPromptsSaving, value: false },
            { selector: selectPromptsError, value: null },
            { selector: selectPromptCount, value: 0 },
          ],
        }),
      ],
    });

    facade = TestBed.inject(PromptsFacade);
    store = TestBed.inject(MockStore);
    vi.spyOn(store, 'dispatch');
  });

  // ── Read (signal selectors) ─────────────────────────────────
  describe('read signals', () => {
    it('exposes prompts signal', () => {
      expect(facade.prompts()).toEqual([]);
    });

    it('exposes selected signal', () => {
      expect(facade.selected()).toBeNull();
    });

    it('exposes versions signal', () => {
      expect(facade.versions()).toEqual([]);
    });

    it('exposes loading signal', () => {
      expect(facade.loading()).toBe(false);
    });

    it('exposes saving signal', () => {
      expect(facade.saving()).toBe(false);
    });

    it('exposes error signal', () => {
      expect(facade.error()).toBeNull();
    });

    it('exposes count signal', () => {
      expect(facade.count()).toBe(0);
    });
  });

  // ── Commands ────────────────────────────────────────────────
  describe('commands', () => {
    it('dispatches loadPrompts with projectId', () => {
      facade.loadPrompts('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.loadPrompts({ projectId: 'proj-1' })
      );
    });

    it('dispatches loadPrompts without projectId', () => {
      facade.loadPrompts();
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.loadPrompts({ projectId: undefined })
      );
    });

    it('dispatches loadPrompt', () => {
      facade.loadPrompt('p-123');
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.loadPrompt({ id: 'p-123' })
      );
    });

    it('dispatches createPrompt', () => {
      const request = { name: 'Test', content: 'Hello' } as CreatePromptRequest;
      facade.createPrompt(request);
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.createPrompt({ request })
      );
    });

    it('dispatches updatePrompt', () => {
      const request = { content: 'Updated' } as UpdatePromptRequest;
      facade.updatePrompt('p-123', request);
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.updatePrompt({ id: 'p-123', request })
      );
    });

    it('dispatches deletePrompt', () => {
      facade.deletePrompt('p-123');
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.deletePrompt({ id: 'p-123' })
      );
    });

    it('dispatches rollbackPrompt', () => {
      facade.rollbackPrompt('p-123', 3);
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.rollbackPrompt({ id: 'p-123', targetVersion: 3 })
      );
    });

    it('dispatches clearSelectedPrompt', () => {
      facade.clearSelection();
      expect(store.dispatch).toHaveBeenCalledWith(
        PromptsActions.clearSelectedPrompt()
      );
    });
  });
});
