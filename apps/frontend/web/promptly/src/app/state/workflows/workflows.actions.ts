import { createAction, props } from '@ngrx/store';
import { SubmitReviewRequest, ApproveRejectRequest, WorkflowResponse } from '@promptly/client';

export const loadWorkflows = createAction('[Workflows] Load Workflows');
export const loadWorkflowsSuccess = createAction('[Workflows] Load Workflows Success', props<{ workflows: WorkflowResponse[] }>());
export const loadWorkflowsFailure = createAction('[Workflows] Load Workflows Failure', props<{ error: string }>());

export const submitForReview = createAction('[Workflows] Submit For Review', props<{ request: SubmitReviewRequest }>());
export const submitForReviewSuccess = createAction('[Workflows] Submit For Review Success', props<{ workflow: WorkflowResponse }>());
export const submitForReviewFailure = createAction('[Workflows] Submit For Review Failure', props<{ error: string }>());

export const approveWorkflow = createAction('[Workflows] Approve Workflow', props<{ id: string; request: ApproveRejectRequest }>());
export const approveWorkflowSuccess = createAction('[Workflows] Approve Workflow Success', props<{ workflow: WorkflowResponse }>());
export const approveWorkflowFailure = createAction('[Workflows] Approve Workflow Failure', props<{ error: string }>());

export const rejectWorkflow = createAction('[Workflows] Reject Workflow', props<{ id: string; request: ApproveRejectRequest }>());
export const rejectWorkflowSuccess = createAction('[Workflows] Reject Workflow Success', props<{ workflow: WorkflowResponse }>());
export const rejectWorkflowFailure = createAction('[Workflows] Reject Workflow Failure', props<{ error: string }>());

export const clearWorkflows = createAction('[Workflows] Clear Workflows');
