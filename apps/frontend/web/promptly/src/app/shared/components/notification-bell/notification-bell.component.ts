import { Component, inject, computed } from '@angular/core';
import { Store } from '@ngrx/store';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DatePipe } from '@angular/common';
import {
  Notification,
  markAsRead,
  markAllAsRead,
  dismissNotification,
  clearAll,
} from '../../state/notifications/notifications.actions';
import { NotificationsState } from '../../state/notifications/notifications.reducer';

@Component({
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
            matTooltip="Notifications"
            aria-label="Notifications">
      <mat-icon>notifications</mat-icon>
    </button>

    <mat-menu #notifMenu="matMenu" class="notification-menu" xPosition="before">
      <div class="notif-header" (click)="$event.stopPropagation()">
        <span class="notif-title">Notifications</span>
        @if (unreadCount() > 0) {
          <button mat-button color="primary" (click)="onMarkAllRead()">
            Mark all read
          </button>
        }
        @if (items().length > 0) {
          <button mat-icon-button matTooltip="Clear all" (click)="onClearAll()">
            <mat-icon>delete_sweep</mat-icon>
          </button>
        }
      </div>
      <mat-divider />

      @if (items().length === 0) {
        <div class="notif-empty" (click)="$event.stopPropagation()">
          <mat-icon>notifications_none</mat-icon>
          <span>No notifications</span>
        </div>
      }

      @for (n of items(); track n.id) {
        <button mat-menu-item
                class="notif-item"
                [class.unread]="!n.read"
                (click)="onRead(n)">
          <mat-icon [class]="'notif-icon notif-icon-' + n.type.split('.')[0]">
            {{ n.icon }}
          </mat-icon>
          <div class="notif-content">
            <span class="notif-item-title">{{ n.title }}</span>
            <span class="notif-msg">{{ n.message }}</span>
            <span class="notif-time">{{ n.timestamp | date:'short' }}</span>
          </div>
          <button mat-icon-button
                  class="notif-dismiss"
                  (click)="onDismiss($event, n.id)"
                  matTooltip="Dismiss">
            <mat-icon>close</mat-icon>
          </button>
        </button>
      }
    </mat-menu>
  `,
  styles: [`
    :host { display: inline-flex; }

    .notification-menu {
      max-width: 400px !important;
      min-width: 340px !important;
    }

    .notif-header {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 16px;
    }

    .notif-title {
      flex: 1;
      font-weight: 600;
      font-size: 14px;
    }

    .notif-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      padding: 24px 16px;
      opacity: 0.5;
      font-size: 14px;
    }

    .notif-item {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 10px 16px;
      height: auto !important;
      min-height: 56px;
      white-space: normal;
      line-height: 1.4;
    }

    .notif-item.unread {
      background: rgba(var(--mat-sys-primary-rgb, 103, 80, 164), 0.08);
    }

    .notif-icon {
      flex-shrink: 0;
      margin-top: 2px;
    }

    .notif-icon-prompt { color: var(--mat-sys-primary, #6750A4); }
    .notif-icon-workflow { color: var(--mat-sys-tertiary, #7D5260); }
    .notif-icon-scan { color: var(--mat-sys-error, #B3261E); }

    .notif-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 2px;
      min-width: 0;
    }

    .notif-item-title {
      font-weight: 600;
      font-size: 13px;
    }

    .notif-msg {
      font-size: 12px;
      opacity: 0.8;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .notif-time {
      font-size: 11px;
      opacity: 0.5;
    }

    .notif-dismiss {
      flex-shrink: 0;
      width: 28px !important;
      height: 28px !important;
      font-size: 16px;
      opacity: 0;
      transition: opacity 0.2s;
    }

    .notif-item:hover .notif-dismiss {
      opacity: 0.6;
    }

    .notif-dismiss:hover {
      opacity: 1 !important;
    }
  `],
})
export class NotificationBellComponent {
  private readonly store = inject(Store<{ notifications: NotificationsState }>);

  readonly items = computed(() =>
    (this.store.selectSignal((s: any) => s.notifications?.items ?? []))()
  );

  readonly unreadCount = computed(() =>
    this.items().filter((n: Notification) => !n.read).length
  );

  onRead(n: Notification): void {
    if (!n.read) {
      this.store.dispatch(markAsRead({ id: n.id }));
    }
  }

  onDismiss(event: Event, id: string): void {
    event.stopPropagation();
    this.store.dispatch(dismissNotification({ id }));
  }

  onMarkAllRead(): void {
    this.store.dispatch(markAllAsRead());
  }

  onClearAll(): void {
    this.store.dispatch(clearAll());
  }
}
