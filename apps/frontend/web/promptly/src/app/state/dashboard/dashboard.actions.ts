import { createAction, props } from '@ngrx/store';
import { DashboardState } from './dashboard.state';

export const loadDashboard = createAction('[Dashboard] Load Dashboard', props<{ projectId?: string }>());
export const loadDashboardSuccess = createAction('[Dashboard] Load Dashboard Success', props<Omit<DashboardState, 'loading' | 'error'>>());
export const loadDashboardFailure = createAction('[Dashboard] Load Dashboard Failure', props<{ error: string }>());
