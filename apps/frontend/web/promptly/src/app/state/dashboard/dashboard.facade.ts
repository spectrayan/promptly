import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectPromptCount,
  selectWorkflowCount,
  selectPendingWorkflows,
  selectScanCount,
  selectAuditCount,
  selectDashboardLoading,
  selectDashboardError,
} from './dashboard.selectors';
import { loadDashboard } from './dashboard.actions';

/**
 * Facade for the Dashboard feature — aggregates cross-module metrics
 * into a single project-scoped overview.
 *
 * Uses NgRx store internally. Loads prompt counts, workflow counts,
 * pending approvals, scan counts, and audit entry counts in a single
 * backend call via the dedicated dashboard endpoint.
 */
@Injectable({ providedIn: 'root' })
export class DashboardFacade {
  private readonly store = inject(Store);

  readonly promptCount     = this.store.selectSignal(selectPromptCount);
  readonly workflowCount   = this.store.selectSignal(selectWorkflowCount);
  readonly pendingWorkflows = this.store.selectSignal(selectPendingWorkflows);
  readonly scanCount       = this.store.selectSignal(selectScanCount);
  readonly auditCount      = this.store.selectSignal(selectAuditCount);
  readonly loading         = this.store.selectSignal(selectDashboardLoading);
  readonly error           = this.store.selectSignal(selectDashboardError);

  loadDashboard(projectId?: string): void {
    this.store.dispatch(loadDashboard({ projectId }));
  }
}
