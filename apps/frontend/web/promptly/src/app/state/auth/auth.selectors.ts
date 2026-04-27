/** Memoized selectors for the Authentication feature slice. Consumed by AuthFacade. */
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState } from './auth.state';

export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectCurrentUser = createSelector(selectAuthState, s => s.user);
export const selectIsAuthenticated = createSelector(selectAuthState, s => s.isAuthenticated);
export const selectAuthLoading = createSelector(selectAuthState, s => s.loading);
export const selectAuthError = createSelector(selectAuthState, s => s.error);
export const selectAccessToken = createSelector(selectAuthState, s => s.accessToken);
export const selectUserDisplayName = createSelector(selectCurrentUser, u => u?.displayName ?? '');
export const selectUserEmail = createSelector(selectCurrentUser, u => u?.email ?? '');
export const selectUserOrgRole = createSelector(selectCurrentUser, u => u?.orgRole);
