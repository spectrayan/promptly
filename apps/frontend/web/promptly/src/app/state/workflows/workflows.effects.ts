/** NgRx effects for the Approval Workflow engine — handles API calls via `@promptly/client` SDK. */
import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { WorkflowsService } from '@promptly/client';
import { resolveErrorMessage, FallbackMessages } from '../../shared/constants/error-messages';
import * as WfActions from './workflows.actions';

@Injectable()
export class WorkflowsEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(WorkflowsService);
  private readonly snackBar = inject(MatSnackBar);

  loadWorkflows$ = createEffect(() =>
    this.actions$.pipe(
      ofType(WfActions.loadWorkflows),
      switchMap(({ projectId }) =>
        this.api.listWorkflows({ projectId }).pipe(
          map(workflows => WfActions.loadWorkflowsSuccess({ workflows })),
          catchError(err => of(WfActions.loadWorkflowsFailure({ error: resolveErrorMessage(err?.error, FallbackMessages.LOAD_WORKFLOWS) })))
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
          catchError(err => of(WfActions.submitForReviewFailure({ error: resolveErrorMessage(err?.error, FallbackMessages.SUBMIT_REVIEW) })))
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
          catchError(err => of(WfActions.approveWorkflowFailure({ error: resolveErrorMessage(err?.error, FallbackMessages.APPROVE_WORKFLOW) })))
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
          catchError(err => of(WfActions.rejectWorkflowFailure({ error: resolveErrorMessage(err?.error, FallbackMessages.REJECT_WORKFLOW) })))
        )
      )
    )
  );

  showErrorSnackbar$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        WfActions.submitForReviewFailure,
        WfActions.approveWorkflowFailure,
        WfActions.rejectWorkflowFailure,
      ),
      tap(({ error }) => {
        this.snackBar.open(error, 'Dismiss', { duration: 5000, panelClass: 'snackbar-error' });
      })
    ),
    { dispatch: false }
  );
}
