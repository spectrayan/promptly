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

  loadDashboard(): void {
    this.store.dispatch(loadDashboard());
  }
}
