/** State shape and initial values for the Dashboard Analytics NgRx feature slice. */
export interface DashboardState {
  promptCount: number;
  workflowCount: number;
  pendingWorkflows: number;
  scanCount: number;
  auditCount: number;
  loading: boolean;
  error: string | null;
}

export const initialDashboardState: DashboardState = {
  promptCount: 0,
  workflowCount: 0,
  pendingWorkflows: 0,
  scanCount: 0,
  auditCount: 0,
  loading: false,
  error: null,
};
