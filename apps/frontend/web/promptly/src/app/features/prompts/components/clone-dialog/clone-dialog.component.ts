import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface CloneDialogData {
  sourceName: string;
  sourceProject: string;
  sourceDescription: string;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-clone-dialog',
  imports: [
    FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule,
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>content_copy</mat-icon> Clone Prompt
    </h2>
    <mat-dialog-content>
      <p class="clone-hint">Creates a new DEV prompt with the current content copied over.</p>

      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Prompt Name</mat-label>
        <input matInput [(ngModel)]="name" placeholder="My cloned prompt" />
      </mat-form-field>

      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Description</mat-label>
        <textarea matInput [(ngModel)]="description" rows="2"></textarea>
      </mat-form-field>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" [disabled]="!name.trim()" (click)="confirm()">
        <mat-icon>content_copy</mat-icon> Clone
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2[mat-dialog-title] {
      display: flex;
      align-items: center;
      gap: 8px;

      mat-icon {
        color: var(--mat-sys-primary);
      }
    }

    .clone-hint {
      color: var(--mat-sys-on-surface-variant);
      font-size: 14px;
      margin-bottom: 16px;
    }

    .full-width {
      width: 100%;
    }
  `],
})
export class CloneDialogComponent {
  name: string;
  description: string;

  constructor(
    private readonly dialogRef: MatDialogRef<CloneDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CloneDialogData,
  ) {
    this.name = `${data.sourceName} (Copy)`;
    this.description = `Cloned from: ${data.sourceName}`;
  }

  confirm(): void {
    this.dialogRef.close({
      name: this.name,
      description: this.description,
      projectId: this.data.sourceProject,
    });
  }
}
