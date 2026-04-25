import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AsyncPipe } from '@angular/common';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { debounceTime, switchMap, map, startWith } from 'rxjs/operators';
import { AuthService, UserResponse, ProjectRole } from '@promptly/client';

@Component({
  selector: 'promptly-add-member-modal',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    AsyncPipe,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatAutocompleteModule
  ],
  template: `
    <h2 mat-dialog-title>Add Project Member</h2>
    <mat-dialog-content>
      <form [formGroup]="form" (ngSubmit)="onSubmit()" id="add-member-form">
        <mat-form-field appearance="outline" class="w-full mt-2">
          <mat-label>Search User</mat-label>
          <input type="text" matInput formControlName="userSearch" [matAutocomplete]="auto">
          <mat-autocomplete #auto="matAutocomplete" [displayWith]="displayUser">
            @for (user of filteredUsers$ | async; track user.id) {
              <mat-option [value]="user">
                {{ user.displayName }} ({{ user.email }})
              </mat-option>
            }
          </mat-autocomplete>
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-full mt-2">
          <mat-label>Role</mat-label>
          <mat-select formControlName="role" required>
            @for (role of roles; track role) {
              <mat-option [value]="role">{{ role }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" [disabled]="form.invalid || !selectedUser" (click)="onSubmit()">Add Member</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .w-full { width: 100%; display: block; }
    .mt-2 { margin-top: 8px; }
  `]
})
export class AddMemberModalComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly dialogRef = inject(MatDialogRef<AddMemberModalComponent>);

  readonly roles = Object.values(ProjectRole);
  filteredUsers$!: Observable<UserResponse[]>;

  readonly form = this.fb.group({
    userSearch: ['', Validators.required],
    role: [ProjectRole.Viewer, Validators.required]
  });

  get selectedUser(): UserResponse | null {
    const val = this.form.get('userSearch')?.value;
    return typeof val === 'object' ? val : null;
  }

  ngOnInit() {
    this.filteredUsers$ = this.form.get('userSearch')!.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => {
        const query = typeof value === 'string' ? value : (value as any)?.displayName;
        return this.authService.listUsers({ q: query || '' }).pipe(
          map(users => users || [])
        );
      })
    );
  }

  displayUser(user: UserResponse): string {
    return user ? `${user.displayName} (${user.email})` : '';
  }

  onSubmit(): void {
    if (this.form.valid && this.selectedUser) {
      this.dialogRef.close({
        userId: this.selectedUser.id,
        role: this.form.value.role
      });
    }
  }
}
