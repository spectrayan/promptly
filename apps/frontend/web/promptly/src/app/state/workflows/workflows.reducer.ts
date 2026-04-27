/** Pure reducer for the Approval Workflow feature slice. Handles workflow lifecycle state transitions. */
import { createReducer, on } from '@ngrx/store';
import { WorkflowsState, initialWorkflowsState } from './workflows.state';
import * as WfActions from './workflows.actions';

export const workflowsReducer = createReducer(
  initialWorkflowsState,

  on(WfActions.loadWorkflows, (state): WorkflowsState => ({ ...state, loading: true, error: null })),
  on(WfActions.loadWorkflowsSuccess, (state, { workflows }): WorkflowsState => ({ ...state, workflows, loading: false })),
  on(WfActions.loadWorkflowsFailure, (state, { error }): WorkflowsState => ({ ...state, loading: false, error })),

  on(WfActions.submitForReviewSuccess, (state, { workflow }): WorkflowsState => ({
    ...state,
    workflows: [workflow, ...state.workflows],
  })),

  on(WfActions.approveWorkflowSuccess, (state, { workflow }): WorkflowsState => ({
    ...state,
    workflows: state.workflows.map(w => w.id === workflow.id ? workflow : w),
    selectedWorkflow: state.selectedWorkflow?.id === workflow.id ? workflow : state.selectedWorkflow,
  })),

  on(WfActions.rejectWorkflowSuccess, (state, { workflow }): WorkflowsState => ({
    ...state,
    workflows: state.workflows.map(w => w.id === workflow.id ? workflow : w),
    selectedWorkflow: state.selectedWorkflow?.id === workflow.id ? workflow : state.selectedWorkflow,
  })),

  on(WfActions.clearWorkflows, (): WorkflowsState => ({ ...initialWorkflowsState })),
);
