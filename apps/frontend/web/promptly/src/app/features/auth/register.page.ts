import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthFacade } from '../../state/auth/auth.facade';
import { HelpDocsService } from '../../core/services/help-docs.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-register',
  imports: [
    FormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatTooltipModule
  ],
  template: `
    <div class="register-container">
      <button mat-icon-button class="floating-help-btn" (click)="helpDocs.toggle()" matTooltip="Help Docs">
        <mat-icon>help_outline</mat-icon>
      </button>
      <div class="register-card">
        <div class="logo-section">
          <img src="logo.png" alt="Promptly Logo" class="logo-image" />
          <h1 class="logo-title">Create Account</h1>
          <p class="logo-subtitle">Join Promptly — AI Prompt Governance</p>
        </div>

        <form class="register-form" (ngSubmit)="onRegister()">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Display Name</mat-label>
            <input matInput type="text" [(ngModel)]="displayName" name="displayName"
                   placeholder="Alice Johnson" required id="register-name" />
            <mat-icon matPrefix>person</mat-icon>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Email</mat-label>
            <input matInput type="email" [(ngModel)]="email" name="email"
                   placeholder="alice&#64;company.com" required id="register-email" />
            <mat-icon matPrefix>email</mat-icon>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Password</mat-label>
            <input matInput [type]="showPassword ? 'text' : 'password'"
                   [(ngModel)]="password" name="password" required minlength="8" id="register-password" />
            <mat-icon matPrefix>lock</mat-icon>
            <mat-hint>Minimum 8 characters</mat-hint>
            <button mat-icon-button matSuffix type="button" (click)="showPassword = !showPassword">
              <mat-icon>{{ showPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
            </button>
          </mat-form-field>

          @if (auth.error()) {
            <div class="error-message">
              <mat-icon>error_outline</mat-icon>
              <span>{{ auth.error() }}</span>
            </div>
          }

          <button mat-flat-button color="primary" type="submit" class="register-btn"
                  [disabled]="auth.loading() || !email || !password || !displayName" id="register-submit">
            @if (auth.loading()) {
              <mat-spinner diameter="20"></mat-spinner>
            } @else {
              Create Account
            }
          </button>
        </form>

        <div class="login-link">
          <span>Already have an account?</span>
          <a routerLink="/login">Sign in</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #0f0c29 0%, #1a1640 30%, #302b63 60%, #24243e 100%);
      padding: var(--space-4);
      position: relative;
      overflow: hidden;
    }

    .floating-help-btn {
      position: absolute;
      top: var(--space-4);
      right: var(--space-4);
      color: rgba(255, 255, 255, 0.7);
      background: rgba(255, 255, 255, 0.05);
      backdrop-filter: blur(8px);
      z-index: 10;
      transition: all var(--duration-fast);
    }
    .floating-help-btn:hover {
      color: white;
      background: rgba(255, 255, 255, 0.15);
    }

    .register-container::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle at 30% 50%, rgba(124, 77, 255, 0.08) 0%, transparent 50%),
                  radial-gradient(circle at 70% 60%, rgba(0, 229, 255, 0.05) 0%, transparent 50%);
      animation: ambientRotate 20s linear infinite;
    }

    @keyframes ambientRotate {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    .register-card {
      width: 100%;
      max-width: 440px;
      background: rgba(255, 255, 255, 0.05);
      backdrop-filter: blur(24px);
      -webkit-backdrop-filter: blur(24px);
      border-radius: var(--radius-2xl);
      border: 1px solid rgba(255, 255, 255, 0.08);
      padding: var(--space-12) var(--space-10);
      box-shadow: 0 24px 48px rgba(0, 0, 0, 0.35),
                  0 0 80px rgba(124, 77, 255, 0.06);
      position: relative;
      z-index: 1;
      animation: fadeInUp var(--duration-slower) var(--ease-out);
    }

    .logo-section {
      text-align: center;
      margin-bottom: var(--space-10);
    }

    .logo-image {
      width: 72px;
      height: 72px;
      margin: 0 auto var(--space-4) auto;
      display: block;
      border-radius: var(--radius-lg);
      box-shadow: 0 0 24px rgba(124, 77, 255, 0.4);
      object-fit: cover;
    }

    .logo-title {
      font-size: var(--text-4xl);
      font-weight: var(--weight-bold);
      color: white;
      margin: 0;
      letter-spacing: var(--tracking-tight);
      background: linear-gradient(135deg, #ffffff 0%, #b388ff 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .logo-subtitle {
      color: rgba(255, 255, 255, 0.45);
      font-size: var(--text-md);
      margin-top: var(--space-1);
      font-weight: var(--weight-light);
      letter-spacing: var(--tracking-wide);
    }

    .register-form {
      display: flex;
      flex-direction: column;
      gap: var(--space-2);
    }

    .full-width { width: 100%; }

    :host ::ng-deep .mat-mdc-form-field {
      --mdc-outlined-text-field-container-shape: var(--radius-md);
      --mdc-outlined-text-field-outline-color: rgba(255, 255, 255, 0.15);
      --mdc-outlined-text-field-hover-outline-color: rgba(255, 255, 255, 0.35);
      --mdc-outlined-text-field-focus-outline-color: #7c4dff;
      --mdc-outlined-text-field-label-text-color: rgba(255, 255, 255, 0.45);
      --mdc-outlined-text-field-focus-label-text-color: #b388ff;
      --mdc-outlined-text-field-input-text-color: white;
      --mat-form-field-state-layer-color: transparent;
    }

    :host ::ng-deep .mat-mdc-form-field .mat-icon {
      color: rgba(255, 255, 255, 0.35);
    }

    :host ::ng-deep .mat-mdc-form-field .mat-mdc-form-field-hint {
      color: rgba(255, 255, 255, 0.3);
    }

    .error-message {
      display: flex;
      align-items: center;
      gap: var(--space-2);
      color: #ff5252;
      font-size: var(--text-base);
      padding: var(--space-3);
      background: rgba(255, 82, 82, 0.08);
      border-radius: var(--radius-md);
      border: 1px solid rgba(255, 82, 82, 0.15);
      margin-bottom: var(--space-2);
      animation: fadeIn var(--duration-base) var(--ease-out);
    }

    .register-btn {
      height: 50px;
      font-size: var(--text-lg) !important;
      font-weight: var(--weight-semibold) !important;
      border-radius: var(--radius-md) !important;
      background: var(--gradient-brand) !important;
      color: white !important;
      margin-top: var(--space-3);
      transition: all var(--duration-base) var(--ease-default) !important;
      box-shadow: 0 4px 16px rgba(124, 77, 255, 0.25);
    }

    .register-btn:hover:not(:disabled) {
      background: linear-gradient(135deg, #651fff, #536dfe) !important;
      transform: translateY(-2px);
      box-shadow: 0 8px 28px rgba(124, 77, 255, 0.4);
    }

    .register-btn:active:not(:disabled) {
      transform: translateY(0);
    }

    .register-btn:disabled {
      opacity: 0.4;
      box-shadow: none;
    }

    .login-link {
      text-align: center;
      margin-top: var(--space-6);
      color: rgba(255, 255, 255, 0.45);
      font-size: var(--text-base);
    }

    .login-link a {
      color: #b388ff;
      text-decoration: none;
      font-weight: var(--weight-semibold);
      margin-left: var(--space-1);
      transition: color var(--duration-fast);
    }

    .login-link a:hover {
      color: #7c4dff;
      text-decoration: underline;
      text-underline-offset: 3px;
    }

    @media (max-width: 480px) {
      .register-card {
        padding: var(--space-8) var(--space-5);
        border-radius: var(--radius-lg);
      }

      .logo-image {
        width: 56px;
        height: 56px;
      }

      .logo-title {
        font-size: var(--text-3xl);
      }

      .logo-section {
        margin-bottom: var(--space-6);
      }

      .register-btn {
        height: 44px;
        font-size: var(--text-base) !important;
      }
    }
  `],
})
export class RegisterPage {
  readonly auth = inject(AuthFacade);
  readonly helpDocs = inject(HelpDocsService);

  displayName = '';
  email = '';
  password = '';
  showPassword = false;

  onRegister(): void {
    if (this.email && this.password && this.displayName) {
      this.auth.register(this.email, this.password, this.displayName);
    }
  }
}
