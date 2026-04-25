import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { WorkflowsService } from '@promptly/client';
import * as WfActions from './workflows.actions';

@Injectable()
export class WorkflowsEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(WorkflowsService);

  loadWorkflows$ = createEffect(() =>
    this.actions$.pipe(
      ofType(WfActions.loadWorkflows),
      switchMap(({ projectId }) =>
        this.api.listWorkflows({ projectId }).pipe(
          map(workflows => WfActions.loadWorkflowsSuccess({ workflows })),
          catchError(err => of(WfActions.loadWorkflowsFailure({ error: err?.error?.detail ?? 'Failed to load workflows' })))
        )
      )
    )
  );

  submitForReview$ = createEffect(() =>
    this.actions$.pipe(
      ofType(WfActions.submitForReview),
      switchMap(({ request }) =>
        this.api.submitForReview({ submitReviewRequest: request }).pipe(
          map((workflow: any) => WfActions.submitForReviewSuccess({ workflow })),
          catchError(err => of(WfActions.submitForReviewFailure({ error: err?.error?.detail ?? 'Failed to submit' })))
        )
      )
    )
  );

  approveWorkflow$ = createEffect(() =>
    this.actions$.pipe(
      ofType(WfActions.approveWorkflow),
      switchMap(({ id, request }) =>
        this.api.approveWorkflow({ id, approveRejectRequest: request }).pipe(
          map((workflow: any) => WfActions.approveWorkflowSuccess({ workflow })),
          catchError(err => of(WfActions.approveWorkflowFailure({ error: err?.error?.detail ?? 'Failed to approve' })))
        )
      )
    )
  );

  rejectWorkflow$ = createEffect(() =>
    this.actions$.pipe(
      ofType(WfActions.rejectWorkflow),
      switchMap(({ id, request }) =>
        this.api.rejectWorkflow({ id, approveRejectRequest: request }).pipe(
          map((workflow: any) => WfActions.rejectWorkflowSuccess({ workflow })),
          catchError(err => of(WfActions.rejectWorkflowFailure({ error: err?.error?.detail ?? 'Failed to reject' })))
        )
      )
    )
  );
}
