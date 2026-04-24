import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectCurrentUser,
  selectIsAuthenticated,
  selectAuthLoading,
  selectAuthError,
  selectUserDisplayName,
  selectUserEmail,
  selectUserOrgRole,
} from './auth.selectors';
import * as AuthActions from './auth.actions';

@Injectable({ providedIn: 'root' })
export class AuthFacade {
  private readonly store = inject(Store);

  // ── Read (signals for OnPush) ─────────────────────────────────
  readonly user            = this.store.selectSignal(selectCurrentUser);
  readonly isAuthenticated = this.store.selectSignal(selectIsAuthenticated);
  readonly loading         = this.store.selectSignal(selectAuthLoading);
  readonly error           = this.store.selectSignal(selectAuthError);
  readonly displayName     = this.store.selectSignal(selectUserDisplayName);
  readonly email           = this.store.selectSignal(selectUserEmail);
  readonly orgRole         = this.store.selectSignal(selectUserOrgRole);

  // ── Commands ──────────────────────────────────────────────────
  login(email: string, password: string): void {
    this.store.dispatch(AuthActions.login({ email, password }));
  }

  register(email: string, password: string, displayName: string): void {
    this.store.dispatch(AuthActions.register({ email, password, displayName }));
  }

  restoreSession(): void {
    this.store.dispatch(AuthActions.restoreSession());
  }

  logout(): void {
    this.store.dispatch(AuthActions.logout());
  }
}
