import { createFeatureSelector, createSelector } from '@ngrx/store';
import { WorkflowsState } from './workflows.state';

export const selectWorkflowsState = createFeatureSelector<WorkflowsState>('workflows');

export const selectAllWorkflows = createSelector(selectWorkflowsState, s => s.workflows);
export const selectWorkflowsLoading = createSelector(selectWorkflowsState, s => s.loading);
export const selectWorkflowsError = createSelector(selectWorkflowsState, s => s.error);
export const selectSelectedWorkflow = createSelector(selectWorkflowsState, s => s.selectedWorkflow);

export const selectWorkflowCount = createSelector(selectAllWorkflows, w => w.length);

export const selectPendingWorkflows = createSelector(
  selectAllWorkflows,
  workflows => workflows.filter(w => w.status === 'PENDING' || w.status === 'IN_REVIEW')
);

export const selectApprovedWorkflows = createSelector(
  selectAllWorkflows,
  workflows => workflows.filter(w => w.status === 'APPROVED')
);
