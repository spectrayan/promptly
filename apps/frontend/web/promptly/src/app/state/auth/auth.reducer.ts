/** Pure reducer for the Authentication feature slice. Manages user, token, and error state. */
import { createReducer, on } from '@ngrx/store';
import { initialAuthState } from './auth.state';
import * as AuthActions from './auth.actions';

export const authReducer = createReducer(
  initialAuthState,

  // Login
  on(AuthActions.login, (state) => ({ ...state, loading: true, error: null })),
  on(AuthActions.loginSuccess, (state, { response }) => ({
    ...state,
    loading: false,
    isAuthenticated: true,
    user: response.user ?? null,
    accessToken: response.accessToken ?? null,
    refreshToken: response.refreshToken ?? null,
    error: null,
  })),
  on(AuthActions.loginFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Register
  on(AuthActions.register, (state) => ({ ...state, loading: true, error: null })),
  on(AuthActions.registerSuccess, (state, { response }) => ({
    ...state,
    loading: false,
    isAuthenticated: true,
    user: response.user ?? null,
    accessToken: response.accessToken ?? null,
    refreshToken: response.refreshToken ?? null,
    error: null,
  })),
  on(AuthActions.registerFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),

  // Load current user
  on(AuthActions.loadCurrentUserSuccess, (state, { user }) => ({
    ...state,
    user,
    isAuthenticated: true,
  })),
  on(AuthActions.loadCurrentUserFailure, (state) => ({
    ...state,
    user: null,
    accessToken: null,
    refreshToken: null,
    isAuthenticated: false,
  })),

  // Logout
  on(AuthActions.logout, () => initialAuthState),
);
