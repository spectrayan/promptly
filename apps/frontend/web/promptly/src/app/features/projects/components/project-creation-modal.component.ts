import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ProjectsFacade } from '../../../state/projects/projects.facade';
import { CreateProjectRequest } from '@promptly/client';

@Component({
  selector: 'promptly-project-creation-modal',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  template: `
    <h2 mat-dialog-title>Create new project</h2>
    <mat-dialog-content>
      <form [formGroup]="form" (ngSubmit)="onSubmit()" id="project-creation-form">
        <mat-form-field appearance="outline" class="w-full mt-2">
          <mat-label>Project Name</mat-label>
          <input matInput formControlName="name" placeholder="Enter project name" required>
          @if (form.get('name')?.hasError('required')) {
            <mat-error>Project name is required</mat-error>
          }
          @if (form.get('name')?.hasError('minlength')) {
            <mat-error>Project name must be at least 3 characters</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-full mt-2">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" placeholder="Optional description" rows="3"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" [disabled]="form.invalid" (click)="onSubmit()">Create</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .w-full { width: 100%; display: block; }
    .mt-2 { margin-top: 8px; }
  `]
})
export class ProjectCreationModalComponent {
  private readonly fb = inject(FormBuilder);
  private readonly projectsFacade = inject(ProjectsFacade);
  private readonly dialogRef = inject(MatDialogRef<ProjectCreationModalComponent>);

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: ['']
  });

  onSubmit(): void {
    if (this.form.valid) {
      const request: CreateProjectRequest = {
        name: this.form.value.name!,
        description: this.form.value.description || undefined
      };
      this.projectsFacade.createProject(request);
      this.dialogRef.close(true);
    }
  }
}
