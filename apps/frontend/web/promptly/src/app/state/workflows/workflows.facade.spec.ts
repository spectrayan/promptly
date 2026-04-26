import { TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { WorkflowsFacade } from './workflows.facade';
import * as WfActions from './workflows.actions';
import {
  selectAllWorkflows,
  selectWorkflowsLoading,
  selectWorkflowsError,
  selectPendingWorkflows,
  selectWorkflowCount,
} from './workflows.selectors';
import { SubmitReviewRequest, ApproveRejectRequest } from '@promptly/client';

describe('WorkflowsFacade', () => {
  let facade: WorkflowsFacade;
  let store: MockStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorkflowsFacade,
        provideMockStore({
          selectors: [
            { selector: selectAllWorkflows, value: [] },
            { selector: selectWorkflowsLoading, value: false },
            { selector: selectWorkflowsError, value: null },
            { selector: selectPendingWorkflows, value: [] },
            { selector: selectWorkflowCount, value: 0 },
          ],
        }),
      ],
    });

    facade = TestBed.inject(WorkflowsFacade);
    store = TestBed.inject(MockStore);
    vi.spyOn(store, 'dispatch');
  });

  // ── Read ──
  describe('read signals', () => {
    it('workflows defaults to empty array', () => {
      expect(facade.workflows()).toEqual([]);
    });

    it('pending defaults to empty array', () => {
      expect(facade.pending()).toEqual([]);
    });

    it('loading defaults to false', () => {
      expect(facade.loading()).toBe(false);
    });

    it('error defaults to null', () => {
      expect(facade.error()).toBeNull();
    });

    it('count defaults to 0', () => {
      expect(facade.count()).toBe(0);
    });
  });

  // ── Commands ──
  describe('commands', () => {
    it('dispatches loadWorkflows', () => {
      facade.loadWorkflows('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(
        WfActions.loadWorkflows({ projectId: 'proj-1' })
      );
    });

    it('dispatches loadWorkflows without projectId', () => {
      facade.loadWorkflows();
      expect(store.dispatch).toHaveBeenCalledWith(
        WfActions.loadWorkflows({ projectId: undefined })
      );
    });

    it('dispatches submitForReview', () => {
      const request = { promptId: 'p-1' } as SubmitReviewRequest;
      facade.submitForReview(request);
      expect(store.dispatch).toHaveBeenCalledWith(
        WfActions.submitForReview({ request })
      );
    });

    it('dispatches approveWorkflow', () => {
      const request = { comment: 'LGTM' } as ApproveRejectRequest;
      facade.approveWorkflow('wf-1', request);
      expect(store.dispatch).toHaveBeenCalledWith(
        WfActions.approveWorkflow({ id: 'wf-1', request })
      );
    });

    it('dispatches rejectWorkflow', () => {
      const request = { actor: 'reviewer@test.com', comment: 'Needs changes', reason: 'Policy violation' } as ApproveRejectRequest;
      facade.rejectWorkflow('wf-1', request);
      expect(store.dispatch).toHaveBeenCalledWith(
        WfActions.rejectWorkflow({ id: 'wf-1', request })
      );
    });
  });
});
