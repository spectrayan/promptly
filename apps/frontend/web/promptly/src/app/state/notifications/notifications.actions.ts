import { createAction, props } from '@ngrx/store';

/** Represents a single notification from SSE */
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

export const addNotification = createAction(
  '[Notifications] Add',
  props<{ notification: Notification }>()
);

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

export const connectSse = createAction(
  '[Notifications] Connect SSE',
  props<{ projectId: string }>()
);

export const disconnectSse = createAction(
  '[Notifications] Disconnect SSE'
);
