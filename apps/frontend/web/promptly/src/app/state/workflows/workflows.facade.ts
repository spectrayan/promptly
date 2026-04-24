import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { SubmitReviewRequest, ApproveRejectRequest } from '@promptly/client';
import {
  selectAllWorkflows,
  selectWorkflowsLoading,
  selectWorkflowsError,
  selectPendingWorkflows,
  selectWorkflowCount,
} from './workflows.selectors';
import * as WfActions from './workflows.actions';

@Injectable({ providedIn: 'root' })
export class WorkflowsFacade {
  private readonly store = inject(Store);

  readonly workflows = this.store.selectSignal(selectAllWorkflows);
  readonly pending   = this.store.selectSignal(selectPendingWorkflows);
  readonly loading   = this.store.selectSignal(selectWorkflowsLoading);
  readonly error     = this.store.selectSignal(selectWorkflowsError);
  readonly count     = this.store.selectSignal(selectWorkflowCount);

  loadWorkflows(): void { this.store.dispatch(WfActions.loadWorkflows()); }

  submitForReview(request: SubmitReviewRequest): void {
    this.store.dispatch(WfActions.submitForReview({ request }));
  }

  approveWorkflow(id: string, request: ApproveRejectRequest): void {
    this.store.dispatch(WfActions.approveWorkflow({ id, request }));
  }

  rejectWorkflow(id: string, request: ApproveRejectRequest): void {
    this.store.dispatch(WfActions.rejectWorkflow({ id, request }));
  }
}
