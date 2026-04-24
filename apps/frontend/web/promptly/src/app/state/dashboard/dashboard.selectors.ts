import { createFeatureSelector, createSelector } from '@ngrx/store';
import { DashboardState } from './dashboard.state';

export const selectDashboardState = createFeatureSelector<DashboardState>('dashboard');

export const selectPromptCount = createSelector(selectDashboardState, s => s.promptCount);
export const selectWorkflowCount = createSelector(selectDashboardState, s => s.workflowCount);
export const selectPendingWorkflows = createSelector(selectDashboardState, s => s.pendingWorkflows);
export const selectScanCount = createSelector(selectDashboardState, s => s.scanCount);
export const selectAuditCount = createSelector(selectDashboardState, s => s.auditCount);
export const selectDashboardLoading = createSelector(selectDashboardState, s => s.loading);
export const selectDashboardError = createSelector(selectDashboardState, s => s.error);
