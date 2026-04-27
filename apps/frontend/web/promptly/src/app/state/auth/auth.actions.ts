/** NgRx actions for Authentication — login, register, session restore, logout. */
import { createAction, props } from '@ngrx/store';
import { AuthResponse, UserResponse } from '@promptly/client';

// Login
export const login = createAction('[Auth] Login', props<{ email: string; password: string }>());
export const loginSuccess = createAction('[Auth] Login Success', props<{ response: AuthResponse }>());
export const loginFailure = createAction('[Auth] Login Failure', props<{ error: string }>());

// Register
export const register = createAction('[Auth] Register', props<{ email: string; password: string; displayName: string }>());
export const registerSuccess = createAction('[Auth] Register Success', props<{ response: AuthResponse }>());
export const registerFailure = createAction('[Auth] Register Failure', props<{ error: string }>());

// Load current user (from stored token)
export const loadCurrentUser = createAction('[Auth] Load Current User');
export const loadCurrentUserSuccess = createAction('[Auth] Load Current User Success', props<{ user: UserResponse }>());
export const loadCurrentUserFailure = createAction('[Auth] Load Current User Failure', props<{ error: string }>());

// Restore session (from localStorage)
export const restoreSession = createAction('[Auth] Restore Session');

// Logout
export const logout = createAction('[Auth] Logout');
