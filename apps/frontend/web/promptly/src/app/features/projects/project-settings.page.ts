import { ChangeDetectionStrategy, Component, inject, OnInit, effect } from '@angular/core';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog } from '@angular/material/dialog';
import { ProjectsFacade } from '../../state/projects/projects.facade';
import { ProjectRole } from '@promptly/client';
import { AddMemberModalComponent } from './components/add-member-modal.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-project-settings',
  standalone: true,
  imports: [

    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatMenuModule,
    MatDividerModule
  ],
  template: `
    <div class="page-header">
      <h1>Project Settings</h1>
      <p>Manage project details and members for {{ projectsFacade.selectedProject()?.name }}</p>
    </div>

    <div class="settings-layout">
      <mat-card>
        <mat-card-header>
          <div style="display: flex; justify-content: space-between; width: 100%; align-items: center;">
            <mat-card-title>Members</mat-card-title>
            <button mat-raised-button color="primary" (click)="openAddMemberModal()">
              <mat-icon>person_add</mat-icon> Add Member
            </button>
          </div>
        </mat-card-header>
        <mat-card-content style="margin-top: 16px;">
          @if (projectsFacade.membersLoading()) {
            <p>Loading members...</p>
          } @else {
            <table mat-table [dataSource]="projectsFacade.projectMembers()" class="w-full">
              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef> Name </th>
                <td mat-cell *matCellDef="let element"> {{ element.displayName }} </td>
              </ng-container>

              <ng-container matColumnDef="email">
                <th mat-header-cell *matHeaderCellDef> Email </th>
                <td mat-cell *matCellDef="let element"> {{ element.email }} </td>
              </ng-container>

              <ng-container matColumnDef="role">
                <th mat-header-cell *matHeaderCellDef> Role </th>
                <td mat-cell *matCellDef="let element"> {{ element.role }} </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef> Actions </th>
                <td mat-cell *matCellDef="let element">
                  <button mat-icon-button [matMenuTriggerFor]="roleMenu">
                    <mat-icon>more_vert</mat-icon>
                  </button>
                  <mat-menu #roleMenu="matMenu">
                    @for (r of roles; track r) {
                      <button mat-menu-item (click)="updateRole(element.userId, r)" [disabled]="element.role === r">
                        Set to {{ r }}
                      </button>
                    }
                    <mat-divider></mat-divider>
                    <button mat-menu-item (click)="removeMember(element.userId)">
                      <mat-icon color="warn">person_remove</mat-icon>
                      <span style="color: red;">Remove Member</span>
                    </button>
                  </mat-menu>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>
          }
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 24px; padding: 24px; }
    .page-header h1 { margin: 0; font-size: 24px; font-weight: 500; }
    .page-header p { margin: 8px 0 0; color: var(--text-secondary, #666); }
    .settings-layout { max-width: 1000px; padding: 0 24px; }
    .w-full { width: 100%; }
  `]
})
export class ProjectSettingsPage implements OnInit {
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly dialog = inject(MatDialog);

  displayedColumns: string[] = ['name', 'email', 'role', 'actions'];
  roles = Object.values(ProjectRole);

  constructor() {
    effect(() => {
      // Reload members when project changes
      const id = this.projectsFacade.selectedId();
      if (id) {
        this.projectsFacade.loadProjectMembers(id);
      }
    });
  }

  ngOnInit() {
    if (this.projectsFacade.selectedId()) {
      this.projectsFacade.loadProjectMembers(this.projectsFacade.selectedId()!);
    }
  }

  openAddMemberModal() {
    const projectId = this.projectsFacade.selectedId();
    if (!projectId) return;

    const dialogRef = this.dialog.open(AddMemberModalComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.projectsFacade.addProjectMember(projectId, { userId: result.userId, role: result.role });
      }
    });
  }

  updateRole(userId: string, role: ProjectRole) {
    const projectId = this.projectsFacade.selectedId();
    if (projectId) {
      this.projectsFacade.updateProjectMember(projectId, userId, { role });
    }
  }

  removeMember(userId: string) {
    const projectId = this.projectsFacade.selectedId();
    if (projectId && confirm('Are you sure you want to remove this member?')) {
      this.projectsFacade.removeProjectMember(projectId, userId);
    }
  }
}
