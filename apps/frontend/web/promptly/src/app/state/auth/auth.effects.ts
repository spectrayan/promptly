/** NgRx effects for Authentication — handles JWT API calls and token storage. */
import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { of, tap } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { AuthService } from '@promptly/client';
import * as AuthActions from './auth.actions';

const TOKEN_KEY = 'promptly_access_token';
const REFRESH_KEY = 'promptly_refresh_token';

@Injectable()
export class AuthEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(AuthService);
  private readonly router = inject(Router);

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      switchMap(({ email, password }) =>
        this.api.login({ loginRequest: { email, password } }).pipe(
          map(response => AuthActions.loginSuccess({ response })),
          catchError(err => of(AuthActions.loginFailure({
            error: err?.error?.detail ?? 'Invalid email or password'
          })))
        )
      )
    )
  );

  register$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.register),
      switchMap(({ email, password, displayName }) =>
        this.api.register({ registerRequest: { email, password, displayName } }).pipe(
          map(response => AuthActions.registerSuccess({ response })),
          catchError(err => of(AuthActions.registerFailure({
            error: err?.error?.detail ?? 'Registration failed'
          })))
        )
      )
    )
  );

  // Persist tokens to localStorage on success
  persistTokens$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginSuccess, AuthActions.registerSuccess),
      tap(({ response }) => {
        if (response.accessToken) localStorage.setItem(TOKEN_KEY, response.accessToken);
        if (response.refreshToken) localStorage.setItem(REFRESH_KEY, response.refreshToken);
      }),
      tap(() => this.router.navigate(['/dashboard'])),
    ), { dispatch: false }
  );

  // Restore session from localStorage on app init
  restoreSession$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.restoreSession),
      switchMap(() => {
        const token = localStorage.getItem(TOKEN_KEY);
        if (!token) {
          return of(AuthActions.loadCurrentUserFailure({ error: 'No token' }));
        }
        return this.api.getCurrentUser().pipe(
          map(user => AuthActions.loadCurrentUserSuccess({ user })),
          catchError(() => {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(REFRESH_KEY);
            return of(AuthActions.loadCurrentUserFailure({ error: 'Session expired' }));
          })
        );
      })
    )
  );

  // Clear tokens on logout
  logout$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.logout),
      tap(() => {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(REFRESH_KEY);
        this.router.navigate(['/login']);
      }),
    ), { dispatch: false }
  );
}
