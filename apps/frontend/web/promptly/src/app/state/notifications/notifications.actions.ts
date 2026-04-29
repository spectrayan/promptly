/** NgRx actions for real-time Notifications — load, mark-read, SSE events. */
import { createAction, props } from '@ngrx/store';

/** Represents a single notification */
export interface Notification {
  id: string;
  type: string;         // e.g. 'prompt.created', 'workflow.approved'
  title: string;
  message: string;
  icon: string;
  timestamp: string;    // ISO string
  read: boolean;
  projectId?: string;
  payload?: Record<string, unknown>;
}

// ── SSE live events ──
export const addNotification = createAction(
  '[Notifications] Add',
  props<{ notification: Notification }>()
);

// ── Local UI actions ──
export const markAsRead = createAction(
  '[Notifications] Mark As Read',
  props<{ id: string }>()
);

export const markAllAsRead = createAction(
  '[Notifications] Mark All As Read'
);

export const dismissNotification = createAction(
  '[Notifications] Dismiss',
  props<{ id: string }>()
);

export const clearAll = createAction(
  '[Notifications] Clear All'
);

// ── SSE lifecycle ──
export const connectSse = createAction(
  '[Notifications] Connect SSE',
  props<{ projectId: string }>()
);

export const disconnectSse = createAction(
  '[Notifications] Disconnect SSE'
);

// ── Load from API (hydrate on project switch) ──
export const loadNotifications = createAction(
  '[Notifications] Load',
  props<{ projectId: string }>()
);

export const loadNotificationsSuccess = createAction(
  '[Notifications] Load Success',
  props<{ notifications: Notification[]; hasMore: boolean }>()
);

export const loadMore = createAction(
  '[Notifications] Load More',
  props<{ projectId: string; before: string }>()
);

export const loadMoreSuccess = createAction(
  '[Notifications] Load More Success',
  props<{ notifications: Notification[]; hasMore: boolean }>()
);

// ── Unread count (for badge) ──
export const loadUnreadCount = createAction(
  '[Notifications] Load Count',
  props<{ projectId: string }>()
);

export const loadUnreadCountSuccess = createAction(
  '[Notifications] Count Success',
  props<{ count: number }>()
);

// ── Backend-synced actions ──
export const markAsReadApi = createAction(
  '[Notifications] Mark Read API',
  props<{ id: string }>()
);

export const markAllAsReadApi = createAction(
  '[Notifications] Mark All Read API',
  props<{ projectId: string }>()
);

export const dismissApi = createAction(
  '[Notifications] Dismiss API',
  props<{ id: string }>()
);

export const clearAllApi = createAction(
  '[Notifications] Clear All API',
  props<{ projectId: string }>()
);
