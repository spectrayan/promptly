import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { ProjectsService, ApiKeyCreatedResponse } from '@promptly/client';

export interface GenerateApiKeyModalData {
  projectId: string;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-generate-api-key-modal',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>{{ createdKey() ? 'API Key Generated' : 'Generate Project API Key' }}</h2>
    <mat-dialog-content>
      @if (!createdKey()) {
        <p class="subtitle">
          API keys provide machine-to-machine Runtime Delivery access to prompts for this project.
        </p>
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <mat-form-field appearance="outline" class="w-full mt-2">
            <mat-label>Key Name</mat-label>
            <input matInput formControlName="name" placeholder="e.g. production-agent, ci-evaluator" required>
            @if (form.get('name')?.hasError('required')) {
              <mat-error>Name is required</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="w-full mt-2">
            <mat-label>Expiration</mat-label>
            <mat-select formControlName="expiresInDays">
              <mat-option [value]="30">30 days</mat-option>
              <mat-option [value]="60">60 days</mat-option>
              <mat-option [value]="90">90 days</mat-option>
              <mat-option [value]="365">1 year</mat-option>
              <mat-option [value]="null">Never</mat-option>
            </mat-select>
          </mat-form-field>
        </form>
      } @else {
        <div class="success-banner">
          <mat-icon color="warn">warning</mat-icon>
          <div>
            <strong>Save this secret key!</strong>
            <p>You won't be able to see it again once you close this dialog.</p>
          </div>
        </div>

        <div class="key-display-container">
          <input class="key-input" type="text" readonly [value]="createdKey()?.apiKey" />
          <button mat-flat-button color="primary" (click)="copyKey()">
            <mat-icon>{{ copied() ? 'check' : 'content_copy' }}</mat-icon>
            {{ copied() ? 'Copied' : 'Copy' }}
          </button>
        </div>
      }
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      @if (!createdKey()) {
        <button mat-button mat-dialog-close>Cancel</button>
        <button mat-raised-button color="primary" [disabled]="form.invalid || loading()" (click)="onSubmit()">
          {{ loading() ? 'Generating...' : 'Generate Key' }}
        </button>
      } @else {
        <button mat-raised-button color="primary" (click)="onClose()">Done</button>
      }
    </mat-dialog-actions>
  `,
  styles: [`
    .w-full { width: 100%; display: block; }
    .mt-2 { margin-top: 8px; }
    .subtitle { color: var(--text-secondary, #666); margin-bottom: 16px; font-size: 14px; }
    .success-banner {
      display: flex;
      gap: 12px;
      align-items: center;
      background: #fff8e1;
      border: 1px solid #ffe082;
      border-radius: 8px;
      padding: 12px;
      margin-bottom: 16px;
      color: #795548;
    }
    .success-banner p { margin: 4px 0 0; font-size: 13px; }
    .key-display-container {
      display: flex;
      gap: 8px;
      align-items: center;
      background: var(--surface-variant, #f5f5f5);
      border-radius: 8px;
      padding: 8px;
      border: 1px solid var(--border-color, #e0e0e0);
    }
    .key-input {
      flex: 1;
      font-family: monospace;
      font-size: 14px;
      border: none;
      background: transparent;
      outline: none;
      padding: 4px 8px;
    }
  `]
})
export class GenerateApiKeyModalComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<GenerateApiKeyModalComponent>);
  private readonly projectsService = inject(ProjectsService);
  readonly data: GenerateApiKeyModalData = inject(MAT_DIALOG_DATA);

  readonly loading = signal(false);
  readonly copied = signal(false);
  readonly createdKey = signal<ApiKeyCreatedResponse | null>(null);

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    expiresInDays: [90]
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading.set(true);
    const val = this.form.value;

    this.projectsService.createProjectApiKey({
      projectId: this.data.projectId,
      createApiKeyRequest: {
        name: val.name!,
        expiresInDays: val.expiresInDays ?? undefined
      }
    }).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.createdKey.set(res);
      },
      error: (err) => {
        this.loading.set(false);
        console.error('Failed to create API key', err);
      }
    });
  }

  copyKey(): void {
    const key = this.createdKey()?.apiKey;
    if (key) {
      navigator.clipboard.writeText(key);
      this.copied.set(true);
      setTimeout(() => this.copied.set(false), 2000);
    }
  }

  onClose(): void {
    this.dialogRef.close(this.createdKey());
  }
}
