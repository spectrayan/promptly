import { ChangeDetectionStrategy, Component, inject, OnInit, signal, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthFacade } from '../../state/auth/auth.facade';
import { ProjectsFacade } from '../../state/projects/projects.facade';

/**
 * Mirrors backend NotificationEventType enum.
 * key must match NotificationEventType.key in Java.
 */
interface NotificationEventDef {
  key: string;
  icon: string;
  title: string;
  category: string;
}

const NOTIFICATION_EVENT_TYPES: NotificationEventDef[] = [
  { key: 'prompt.created',    icon: 'edit_note',     title: 'Prompt Created',      category: 'Prompt Registry' },
  { key: 'prompt.updated',    icon: 'update',        title: 'Prompt Updated',      category: 'Prompt Registry' },
  { key: 'workflow.approved', icon: 'check_circle',  title: 'Workflow Approved',   category: 'Workflow' },
  { key: 'workflow.rejected', icon: 'cancel',        title: 'Workflow Rejected',   category: 'Workflow' },
  { key: 'scan.completed',    icon: 'verified',      title: 'Scan Completed',      category: 'Security Scanner' },
  { key: 'scan.critical',     icon: 'warning',       title: 'Critical Findings',   category: 'Security Scanner' },
];

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-user-profile',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatSlideToggleModule,
    MatSnackBarModule, MatProgressSpinnerModule,
    MatCheckboxModule, MatDividerModule,
  ],
  templateUrl: './user-profile.page.html',
  styleUrl: './user-profile.page.scss',
})
export class UserProfilePage implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly snackBar = inject(MatSnackBar);
  readonly auth = inject(AuthFacade);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly apiBase = environment.apiBasePath;

  readonly loading = signal(false);
  readonly savingProfile = signal(false);
  readonly savingPrefs = signal(false);

  // Profile Form
  displayName = '';
  avatarUrl = '';

  // Preferences Form
  inAppEnabled = true;
  emailEnabled = true;

  // Granular event muting
  readonly allEventTypes = NOTIFICATION_EVENT_TYPES;
  mutedEvents = new Set<string>();

  /** Group event types by category for UI display */
  readonly eventCategories = this.groupByCategory();

  constructor() {
    effect(() => {
      const pid = this.projectsFacade.selectedId();
      if (pid) {
        this.loadPreferences(pid);
      }
    });
  }

  ngOnInit(): void {
    const user = this.auth.user();
    if (user) {
      this.displayName = user.displayName ?? '';
      this.avatarUrl = user.avatarUrl ?? '';
    }
  }

  loadPreferences(projectId: string): void {
    this.loading.set(true);
    this.http.get<any>(`${this.apiBase}/api/v1/notifications/preferences`, { params: { projectId } }).subscribe({
      next: (res) => {
        this.inAppEnabled = res.inAppEnabled ?? true;
        this.emailEnabled = res.emailEnabled ?? true;
        this.mutedEvents = new Set<string>(res.mutedEvents ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  isEventMuted(key: string): boolean {
    return this.mutedEvents.has(key);
  }

  toggleEvent(key: string): void {
    if (this.mutedEvents.has(key)) {
      this.mutedEvents.delete(key);
    } else {
      this.mutedEvents.add(key);
    }
  }

  saveProfile(): void {
    this.savingProfile.set(true);
    this.http.patch(`${this.apiBase}/api/v1/auth/me`, {
      displayName: this.displayName,
      avatarUrl: this.avatarUrl || null,
    }).subscribe({
      next: () => {
        this.savingProfile.set(false);
        this.snackBar.open('Profile updated successfully!', 'OK', { duration: 3000 });
        this.auth.restoreSession();
      },
      error: () => {
        this.savingProfile.set(false);
        this.snackBar.open('Failed to update profile.', 'Dismiss', { duration: 5000 });
      }
    });
  }

  savePreferences(): void {
    const projectId = this.projectsFacade.selectedId();
    if (!projectId) {
      this.snackBar.open('No project selected. Cannot save preferences.', 'Dismiss');
      return;
    }

    this.savingPrefs.set(true);
    this.http.put(`${this.apiBase}/api/v1/notifications/preferences`, {
      projectId,
      inAppEnabled: this.inAppEnabled,
      emailEnabled: this.emailEnabled,
      mutedEvents: Array.from(this.mutedEvents)
    }).subscribe({
      next: () => {
        this.savingPrefs.set(false);
        this.snackBar.open('Preferences saved for the current project!', 'OK', { duration: 3000 });
      },
      error: () => {
        this.savingPrefs.set(false);
        this.snackBar.open('Failed to save preferences.', 'Dismiss', { duration: 5000 });
      }
    });
  }

  private groupByCategory(): { category: string; events: NotificationEventDef[] }[] {
    const map = new Map<string, NotificationEventDef[]>();
    for (const evt of NOTIFICATION_EVENT_TYPES) {
      const group = map.get(evt.category) ?? [];
      group.push(evt);
      map.set(evt.category, group);
    }
    return Array.from(map.entries()).map(([category, events]) => ({ category, events }));
  }
}
