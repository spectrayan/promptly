import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { forkJoin, of } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { PromptsService, WorkflowsService, ScannerService, AuditService } from '@promptly/client';
import * as DashboardActions from './dashboard.actions';

/**
 * Dashboard effects — aggregates counts from multiple SDK services.
 * Each API call is individually resilient: if one fails, it defaults
 * to 0 instead of killing the entire dashboard.
 */
@Injectable()
export class DashboardEffects {
  private readonly actions$ = inject(Actions);
  private readonly promptsApi = inject(PromptsService);
  private readonly workflowsApi = inject(WorkflowsService);
  private readonly scannerApi = inject(ScannerService);
  private readonly auditApi = inject(AuditService);

  loadDashboard$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadDashboard),
      switchMap(() =>
        forkJoin({
          prompts: this.promptsApi.listPrompts().pipe(catchError(() => of([]))),
          workflows: this.workflowsApi.listWorkflows().pipe(catchError(() => of([]))),
          scans: this.scannerApi.getAllScans().pipe(catchError(() => of([]))),
          audit: this.auditApi.getAuditLogs().pipe(catchError(() => of([]))),
        }).pipe(
          map(({ prompts, workflows, scans, audit }) =>
            DashboardActions.loadDashboardSuccess({
              promptCount: prompts.length,
              workflowCount: workflows.length,
              pendingWorkflows: workflows.filter((w: any) => w.status === 'PENDING' || w.status === 'IN_REVIEW').length,
              scanCount: scans.length,
              auditCount: audit.length,
            })
          ),
          catchError(err =>
            of(DashboardActions.loadDashboardFailure({ error: err?.message ?? 'Failed to load dashboard' }))
          )
        )
      )
    )
  );
}
