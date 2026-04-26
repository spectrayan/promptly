import { workflowsReducer } from './workflows.reducer';
import { initialWorkflowsState, WorkflowsState } from './workflows.state';
import * as WfActions from './workflows.actions';
import { WorkflowResponse } from '@promptly/client';

/**
 * Unit tests for the workflows NgRx reducer.
 */
describe('Workflows Reducer', () => {

  const mockWorkflow: WorkflowResponse = {
    id: 'wf-1', promptId: 'p-1', status: 'PENDING', projectId: 'proj-1',
  } as WorkflowResponse;

  const approvedWorkflow: WorkflowResponse = {
    ...mockWorkflow, status: 'APPROVED',
  };

  const rejectedWorkflow: WorkflowResponse = {
    ...mockWorkflow, status: 'REJECTED',
  };

  it('should return initial state for unknown action', () => {
    const state = workflowsReducer(undefined, { type: '[Unknown]' } as any);
    expect(state).toEqual(initialWorkflowsState);
  });

  // ── Load ──

  it('should set loading=true on loadWorkflows', () => {
    const state = workflowsReducer(
      initialWorkflowsState,
      WfActions.loadWorkflows({ projectId: 'proj-1' })
    );
    expect(state.loading).toBe(true);
    expect(state.error).toBeNull();
  });

  it('should populate workflows on loadWorkflowsSuccess', () => {
    const state = workflowsReducer(
      { ...initialWorkflowsState, loading: true },
      WfActions.loadWorkflowsSuccess({ workflows: [mockWorkflow] })
    );
    expect(state.workflows).toEqual([mockWorkflow]);
    expect(state.loading).toBe(false);
  });

  it('should set error on loadWorkflowsFailure', () => {
    const state = workflowsReducer(
      { ...initialWorkflowsState, loading: true },
      WfActions.loadWorkflowsFailure({ error: 'Network error' })
    );
    expect(state.loading).toBe(false);
    expect(state.error).toBe('Network error');
  });

  // ── Submit For Review ──

  it('should prepend workflow on submitForReviewSuccess', () => {
    const existing: WorkflowsState = {
      ...initialWorkflowsState,
      workflows: [{ ...mockWorkflow, id: 'wf-old' } as WorkflowResponse],
    };
    const newWf = { ...mockWorkflow, id: 'wf-new' };
    const state = workflowsReducer(
      existing,
      WfActions.submitForReviewSuccess({ workflow: newWf })
    );
    expect(state.workflows).toHaveLength(2);
    expect(state.workflows[0].id).toBe('wf-new');
  });

  // ── Approve ──

  it('should update workflow status in list on approveWorkflowSuccess', () => {
    const existing: WorkflowsState = {
      ...initialWorkflowsState,
      workflows: [mockWorkflow],
      selectedWorkflow: mockWorkflow,
    };
    const state = workflowsReducer(
      existing,
      WfActions.approveWorkflowSuccess({ workflow: approvedWorkflow })
    );
    expect(state.workflows[0].status).toBe('APPROVED');
    expect(state.selectedWorkflow?.status).toBe('APPROVED');
  });

  it('should not update selectedWorkflow if different workflow approved', () => {
    const existing: WorkflowsState = {
      ...initialWorkflowsState,
      workflows: [mockWorkflow],
      selectedWorkflow: { ...mockWorkflow, id: 'wf-other' } as WorkflowResponse,
    };
    const state = workflowsReducer(
      existing,
      WfActions.approveWorkflowSuccess({ workflow: approvedWorkflow })
    );
    expect(state.selectedWorkflow?.id).toBe('wf-other');
  });

  // ── Reject ──

  it('should update workflow status in list on rejectWorkflowSuccess', () => {
    const existing: WorkflowsState = {
      ...initialWorkflowsState,
      workflows: [mockWorkflow],
      selectedWorkflow: mockWorkflow,
    };
    const state = workflowsReducer(
      existing,
      WfActions.rejectWorkflowSuccess({ workflow: rejectedWorkflow })
    );
    expect(state.workflows[0].status).toBe('REJECTED');
    expect(state.selectedWorkflow?.status).toBe('REJECTED');
  });

  // ── Clear ──

  it('should reset to initial state on clearWorkflows', () => {
    const existing: WorkflowsState = {
      workflows: [mockWorkflow],
      selectedWorkflow: mockWorkflow,
      loading: true,
      error: 'stale',
    };
    const state = workflowsReducer(
      existing,
      WfActions.clearWorkflows()
    );
    expect(state).toEqual(initialWorkflowsState);
  });
});
