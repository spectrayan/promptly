import { ChangeDetectionStrategy, Component, inject, OnInit, effect, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog } from '@angular/material/dialog';
import { ProjectsFacade } from '../../state/projects/projects.facade';
import { ProjectRole, ProjectsService, ApiKeyResponse } from '@promptly/client';
import { AddMemberModalComponent } from './components/add-member-modal.component';
import { GenerateApiKeyModalComponent } from './components/generate-api-key-modal.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-project-settings',
  standalone: true,
  imports: [
    DatePipe,
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
      <p>Manage project details, members, and API keys for {{ projectsFacade.selectedProject()?.name }}</p>
    </div>

    <div class="settings-layout">
      <!-- Members Card -->
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

      <!-- API Keys Card -->
      <mat-card style="margin-top: 24px;">
        <mat-card-header>
          <div style="display: flex; justify-content: space-between; width: 100%; align-items: center;">
            <div>
              <mat-card-title>API Keys (Runtime Delivery)</mat-card-title>
              <mat-card-subtitle style="margin-top: 4px;">
                Authenticate external AI agents & client applications via <code>/api/v1/deliver</code>
              </mat-card-subtitle>
            </div>
            <button mat-raised-button color="primary" (click)="openGenerateApiKeyModal()">
              <mat-icon>key</mat-icon> Generate API Key
            </button>
          </div>
        </mat-card-header>
        <mat-card-content style="margin-top: 16px;">
          @if (apiKeysLoading()) {
            <p>Loading API keys...</p>
          } @else if (apiKeys().length === 0) {
            <div class="empty-keys">
              <mat-icon style="font-size: 36px; width: 36px; height: 36px; color: #bbb;">key_off</mat-icon>
              <p>No API keys generated for this project yet.</p>
            </div>
          } @else {
            <table mat-table [dataSource]="apiKeys()" class="w-full">
              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef> Name </th>
                <td mat-cell *matCellDef="let key"> <strong>{{ key.name }}</strong> </td>
              </ng-container>

              <ng-container matColumnDef="prefix">
                <th mat-header-cell *matHeaderCellDef> Key Prefix </th>
                <td mat-cell *matCellDef="let key"> <code class="prefix-code">{{ key.prefix }}</code> </td>
              </ng-container>

              <ng-container matColumnDef="createdAt">
                <th mat-header-cell *matHeaderCellDef> Created </th>
                <td mat-cell *matCellDef="let key"> {{ key.createdAt | date:'mediumDate' }} </td>
              </ng-container>

              <ng-container matColumnDef="expiresAt">
                <th mat-header-cell *matHeaderCellDef> Expires </th>
                <td mat-cell *matCellDef="let key"> {{ key.expiresAt ? (key.expiresAt | date:'mediumDate') : 'Never' }} </td>
              </ng-container>

              <ng-container matColumnDef="lastUsedAt">
                <th mat-header-cell *matHeaderCellDef> Last Used </th>
                <td mat-cell *matCellDef="let key"> {{ key.lastUsedAt ? (key.lastUsedAt | date:'short') : 'Never' }} </td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef> Status </th>
                <td mat-cell *matCellDef="let key">
                  @if (key.revoked) {
                    <span class="badge badge-revoked">Revoked</span>
                  } @else if (isKeyExpired(key)) {
                    <span class="badge badge-expired">Expired</span>
                  } @else {
                    <span class="badge badge-active">Active</span>
                  }
                </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef> Actions </th>
                <td mat-cell *matCellDef="let key">
                  @if (!key.revoked) {
                    <button mat-button color="warn" (click)="revokeApiKey(key.id)">
                      <mat-icon>block</mat-icon> Revoke
                    </button>
                  } @else {
                    <span style="color: #999; font-size: 13px;">—</span>
                  }
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="apiKeyColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: apiKeyColumns;"></tr>
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
    .settings-layout { max-width: 1000px; padding: 0 24px 48px; }
    .w-full { width: 100%; }
    .empty-keys {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 32px;
      color: #888;
    }
    .empty-keys p { margin-top: 8px; font-size: 14px; }
    .prefix-code {
      font-family: monospace;
      background: var(--surface-variant, #f0f0f0);
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 13px;
    }
    .badge {
      display: inline-block;
      padding: 3px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }
    .badge-active { background: #e8f5e9; color: #2e7d32; }
    .badge-expired { background: #fff3e0; color: #e65100; }
    .badge-revoked { background: #ffebee; color: #c62828; }
  `]
})
export class ProjectSettingsPage implements OnInit {
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly dialog = inject(MatDialog);
  private readonly projectsService = inject(ProjectsService);

  readonly displayedColumns: string[] = ['name', 'email', 'role', 'actions'];
  readonly apiKeyColumns: string[] = ['name', 'prefix', 'createdAt', 'expiresAt', 'lastUsedAt', 'status', 'actions'];
  readonly roles = Object.values(ProjectRole);

  readonly apiKeys = signal<ApiKeyResponse[]>([]);
  readonly apiKeysLoading = signal<boolean>(false);

  constructor() {
    effect(() => {
      const id = this.projectsFacade.selectedId();
      if (id) {
        this.projectsFacade.loadProjectMembers(id);
        this.loadApiKeys(id);
      }
    });
  }

  ngOnInit() {
    const id = this.projectsFacade.selectedId();
    if (id) {
      this.projectsFacade.loadProjectMembers(id);
      this.loadApiKeys(id);
    }
  }

  loadApiKeys(projectId: string) {
    this.apiKeysLoading.set(true);
    this.projectsService.listProjectApiKeys({ projectId }).subscribe({
      next: (keys) => {
        this.apiKeys.set(keys || []);
        this.apiKeysLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load API keys', err);
        this.apiKeys.set([]);
        this.apiKeysLoading.set(false);
      }
    });
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

  openGenerateApiKeyModal() {
    const projectId = this.projectsFacade.selectedId();
    if (!projectId) return;

    const dialogRef = this.dialog.open(GenerateApiKeyModalComponent, {
      width: '480px',
      data: { projectId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadApiKeys(projectId);
      }
    });
  }

  revokeApiKey(keyId: string) {
    const projectId = this.projectsFacade.selectedId();
    if (projectId && confirm('Are you sure you want to revoke this API key? External clients using it will lose access immediately.')) {
      this.projectsService.revokeProjectApiKey({ projectId, keyId }).subscribe({
        next: () => this.loadApiKeys(projectId),
        error: (err) => console.error('Failed to revoke API key', err)
      });
    }
  }

  isKeyExpired(key: ApiKeyResponse): boolean {
    if (!key.expiresAt) return false;
    return new Date(key.expiresAt).getTime() < Date.now();
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
