import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectNotificationItems,
  selectUnreadCount,
  selectHasMore,
  selectNotificationsLoading,
} from './notifications.selectors';
import {
  connectSse,
  disconnectSse,
  loadNotifications,
  loadMore,
  loadUnreadCount,
  markAsReadApi,
  markAllAsReadApi,
  dismissApi,
  clearAll,
} from './notifications.actions';

/**
 * Facade for the Notifications NgRx feature.
 *
 * Encapsulates all Store interactions so that components never
 * import actions/selectors or the Store directly.
 */
@Injectable({ providedIn: 'root' })
export class NotificationsFacade {
  private readonly store = inject(Store);

  // ── Read (signals for OnPush) ──
  readonly items       = this.store.selectSignal(selectNotificationItems);
  readonly unreadCount = this.store.selectSignal(selectUnreadCount);
  readonly hasMore     = this.store.selectSignal(selectHasMore);
  readonly loading     = this.store.selectSignal(selectNotificationsLoading);

  // ── Commands ──
  connectSse(projectId: string): void {
    this.store.dispatch(connectSse({ projectId }));
  }

  disconnectSse(): void {
    this.store.dispatch(disconnectSse());
  }

  load(projectId: string): void {
    this.store.dispatch(loadNotifications({ projectId }));
  }

  loadMore(projectId: string, before: string): void {
    this.store.dispatch(loadMore({ projectId, before }));
  }

  loadUnreadCount(projectId: string): void {
    this.store.dispatch(loadUnreadCount({ projectId }));
  }

  markAsRead(id: string): void {
    this.store.dispatch(markAsReadApi({ id }));
  }

  markAllAsRead(projectId: string): void {
    this.store.dispatch(markAllAsReadApi({ projectId }));
  }

  dismiss(id: string): void {
    this.store.dispatch(dismissApi({ id }));
  }

  clearAll(): void {
    this.store.dispatch(clearAll());
  }
}
