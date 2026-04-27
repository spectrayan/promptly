/** Pure reducer for the Dashboard Analytics feature slice. */
import { createReducer, on } from '@ngrx/store';
import { DashboardState, initialDashboardState } from './dashboard.state';
import * as DashboardActions from './dashboard.actions';

export const dashboardReducer = createReducer(
  initialDashboardState,

  on(DashboardActions.loadDashboard, (state): DashboardState => ({ ...state, loading: true, error: null })),
  on(DashboardActions.loadDashboardSuccess, (state, payload): DashboardState => ({
    ...state,
    ...payload,
    loading: false,
  })),
  on(DashboardActions.loadDashboardFailure, (state, { error }): DashboardState => ({ ...state, loading: false, error })),
);
