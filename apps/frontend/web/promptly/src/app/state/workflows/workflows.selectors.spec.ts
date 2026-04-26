import {
  selectAllWorkflows,
  selectWorkflowsLoading,
  selectWorkflowsError,
  selectSelectedWorkflow,
  selectWorkflowCount,
  selectPendingWorkflows,
  selectApprovedWorkflows,
} from './workflows.selectors';
import { WorkflowsState } from './workflows.state';
import { WorkflowResponse } from '@promptly/client';

const makeWorkflow = (id: string, status: string): WorkflowResponse =>
  ({ id, status } as WorkflowResponse);

describe('Workflow Selectors', () => {
  const state: WorkflowsState = {
    workflows: [
      makeWorkflow('wf-1', 'PENDING'),
      makeWorkflow('wf-2', 'IN_REVIEW'),
      makeWorkflow('wf-3', 'APPROVED'),
      makeWorkflow('wf-4', 'REJECTED'),
    ],
    selectedWorkflow: makeWorkflow('wf-1', 'PENDING'),
    loading: false,
    error: null,
  };

  it('selectAllWorkflows returns all workflows', () => {
    expect(selectAllWorkflows.projector(state)).toHaveLength(4);
  });

  it('selectWorkflowsLoading returns loading', () => {
    expect(selectWorkflowsLoading.projector(state)).toBe(false);
  });

  it('selectWorkflowsError returns error', () => {
    expect(selectWorkflowsError.projector(state)).toBeNull();
  });

  it('selectSelectedWorkflow returns selected workflow', () => {
    expect(selectSelectedWorkflow.projector(state)?.id).toBe('wf-1');
  });

  it('selectWorkflowCount counts all workflows', () => {
    expect(selectWorkflowCount.projector(state.workflows)).toBe(4);
  });

  it('selectPendingWorkflows filters PENDING and IN_REVIEW', () => {
    const pending = selectPendingWorkflows.projector(state.workflows);
    expect(pending).toHaveLength(2);
    expect(pending.map(w => w.id)).toEqual(['wf-1', 'wf-2']);
  });

  it('selectApprovedWorkflows filters only APPROVED', () => {
    const approved = selectApprovedWorkflows.projector(state.workflows);
    expect(approved).toHaveLength(1);
    expect(approved[0].id).toBe('wf-3');
  });

  it('selectPendingWorkflows returns empty for no matches', () => {
    const allApproved = [makeWorkflow('wf-5', 'APPROVED')];
    expect(selectPendingWorkflows.projector(allApproved)).toEqual([]);
  });
});
