import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DatePipe } from '@angular/common';
import { Notification } from '../../../state/notifications/notifications.actions';
import { NotificationsFacade } from '../../../state/notifications/notifications.facade';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-notification-bell',
  standalone: true,
  imports: [
    MatIconModule, MatButtonModule, MatBadgeModule,
    MatMenuModule, MatDividerModule, MatTooltipModule, DatePipe,
  ],
  template: `
    <button mat-icon-button
            [matMenuTriggerFor]="notifMenu"
            [matBadge]="unreadCount()"
            [matBadgeHidden]="unreadCount() === 0"
            matBadgeColor="warn"
            matBadgeSize="small"
            [matTooltip]="unreadCount() > 0 ? unreadCount() + ' unread notifications' : 'Notifications'"
            aria-label="Notifications"
            [class.has-unread]="unreadCount() > 0">
      <mat-icon>{{ unreadCount() > 0 ? 'notifications_active' : 'notifications' }}</mat-icon>
    </button>

    <mat-menu #notifMenu="matMenu" class="notification-menu" xPosition="before">
      <div class="notif-header" (click)="$event.stopPropagation()">
        <span class="notif-title">Notifications</span>
        <div class="notif-header-actions">
          @if (unreadCount() > 0) {
            <button mat-button class="notif-mark-all" (click)="onMarkAllRead()">
              Mark all read
            </button>
          }
          @if (items().length > 0) {
            <button mat-icon-button matTooltip="Clear all" (click)="onClearAll()" class="notif-clear-btn">
              <mat-icon>delete_sweep</mat-icon>
            </button>
          }
        </div>
      </div>
      <mat-divider />

      <div class="notif-scroll" (click)="$event.stopPropagation()">
        @if (items().length === 0) {
          <div class="notif-empty">
            <mat-icon class="notif-empty-icon">notifications_none</mat-icon>
            <span>No notifications yet</span>
          </div>
        }

        @for (n of items(); track n.id) {
          <div class="notif-item" [class.unread]="!n.read" (click)="onRead(n)">
            <div class="notif-icon-wrap" [class]="'notif-icon-' + n.type.split('.')[0]">
              <mat-icon>{{ n.icon }}</mat-icon>
            </div>
            <div class="notif-content">
              <span class="notif-item-title">{{ n.title }}</span>
              <span class="notif-msg">{{ n.message }}</span>
              <span class="notif-time">{{ n.timestamp | date:'medium' }}</span>
            </div>
            <button mat-icon-button
                    class="notif-dismiss"
                    (click)="onDismiss($event, n.id)"
                    matTooltip="Dismiss">
              <mat-icon>close</mat-icon>
            </button>
          </div>
        }
      </div>
    </mat-menu>
  `,
  styles: [`
    :host { display: inline-flex; }

    /* Pulse animation on the bell when there are unread notifications */
    .has-unread mat-icon {
      animation: bell-ring 2s ease-in-out infinite;
    }

    @keyframes bell-ring {
      0%, 80%, 100% { transform: rotate(0); }
      85% { transform: rotate(12deg); }
      90% { transform: rotate(-12deg); }
      95% { transform: rotate(6deg); }
    }

    /* Ensure badge is visible on dark backgrounds */
    ::ng-deep .has-unread .mat-badge-content {
      background: #ef5350 !important;
      color: #fff !important;
      font-weight: 700;
      box-shadow: 0 0 6px rgba(239, 83, 80, 0.5);
    }
    ::ng-deep .mat-mdc-menu-panel.notification-menu {
      max-width: 460px !important;
      min-width: 400px !important;
    }

    .notif-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 16px;
    }

    .notif-title {
      font-weight: 700;
      font-size: 15px;
      letter-spacing: -0.01em;
    }

    .notif-header-actions {
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .notif-mark-all {
      font-size: 12px !important;
      line-height: 1 !important;
      padding: 4px 12px !important;
      min-height: 28px !important;
      height: 28px !important;
      border-radius: 14px !important;
      color: var(--mat-sys-primary, #b39ddb) !important;
    }

    .notif-clear-btn {
      width: 32px !important;
      height: 32px !important;
      padding: 0 !important;
    }

    .notif-clear-btn mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }

    .notif-scroll {
      max-height: 420px;
      overflow-y: auto;
    }

    .notif-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      padding: 40px 16px;
      opacity: 0.4;
      font-size: 14px;
    }

    .notif-empty-icon {
      font-size: 40px;
      width: 40px;
      height: 40px;
      opacity: 0.5;
    }

    .notif-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 12px 16px;
      cursor: pointer;
      transition: background-color 0.15s ease;
      border-bottom: 1px solid rgba(255,255,255,0.04);
    }

    .notif-item:hover {
      background: rgba(255,255,255,0.04);
    }

    .notif-item.unread {
      background: rgba(var(--mat-sys-primary-rgb, 103, 80, 164), 0.08);
    }

    .notif-item.unread:hover {
      background: rgba(var(--mat-sys-primary-rgb, 103, 80, 164), 0.12);
    }

    .notif-icon-wrap {
      flex-shrink: 0;
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255,255,255,0.06);
      margin-top: 2px;
    }

    .notif-icon-wrap mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }

    .notif-icon-prompt { color: var(--mat-sys-primary, #b39ddb); }
    .notif-icon-workflow { color: #66bb6a; }
    .notif-icon-scan { color: #ef5350; }

    .notif-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 3px;
      min-width: 0;
      overflow: hidden;
    }

    .notif-item-title {
      font-weight: 600;
      font-size: 13px;
      line-height: 1.3;
    }

    .notif-msg {
      font-size: 12px;
      line-height: 1.45;
      opacity: 0.75;
      white-space: normal;
      word-wrap: break-word;
      overflow-wrap: break-word;
    }

    .notif-time {
      font-size: 11px;
      opacity: 0.4;
      margin-top: 2px;
    }

    .notif-dismiss {
      flex-shrink: 0;
      width: 28px !important;
      height: 28px !important;
      padding: 0 !important;
      opacity: 0;
      transition: opacity 0.2s;
      margin-top: 4px;
    }

    .notif-dismiss mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
    }

    .notif-item:hover .notif-dismiss {
      opacity: 0.5;
    }

    .notif-dismiss:hover {
      opacity: 1 !important;
    }
  `],
})
export class NotificationBellComponent {
  private readonly notifications = inject(NotificationsFacade);

  readonly items = this.notifications.items;
  readonly unreadCount = this.notifications.unreadCount;

  onRead(n: Notification): void {
    if (!n.read) {
      this.notifications.markAsRead(n.id);
    }
  }

  onDismiss(event: Event, id: string): void {
    event.stopPropagation();
    this.notifications.dismiss(id);
  }

  onMarkAllRead(): void {
    const firstItem = this.items()[0];
    if (firstItem?.projectId) {
      this.notifications.markAllAsRead(firstItem.projectId);
    }
  }

  onClearAll(): void {
    const firstItem = this.items()[0];
    if (firstItem?.projectId) {
      this.notifications.clearAll(firstItem.projectId);
    }
  }
}
