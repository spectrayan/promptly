import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-not-found',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule],
  template: `
    <div class="not-found-container">
      <mat-icon class="not-found-icon">explore_off</mat-icon>
      <h1>404</h1>
      <p>The page you're looking for doesn't exist or has been moved.</p>
      <a mat-raised-button color="primary" routerLink="/dashboard">
        <mat-icon>home</mat-icon>
        Back to Dashboard
      </a>
    </div>
  `,
  styles: [`
    .not-found-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 60vh;
      text-align: center;
      gap: 16px;
      padding: 24px;
    }

    .not-found-icon {
      font-size: 80px;
      width: 80px;
      height: 80px;
      opacity: 0.3;
    }

    h1 {
      font-size: 72px;
      font-weight: 800;
      margin: 0;
      letter-spacing: -2px;
      opacity: 0.6;
    }

    p {
      font-size: 16px;
      opacity: 0.5;
      margin: 0;
      max-width: 400px;
    }
  `],
})
export class NotFoundPage {}
