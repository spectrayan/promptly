/** Pure reducer for the Notifications feature slice. Manages notification list and unread count. */
import { createReducer, on } from '@ngrx/store';
import {
  Notification,
  addNotification,
  markAsRead,
  markAllAsRead,
  dismissNotification,
  clearAll,
  loadNotificationsSuccess,
  loadMoreSuccess,
  loadUnreadCountSuccess,
} from './notifications.actions';

export interface NotificationsState {
  items: Notification[];
  hasMore: boolean;
  unreadCount: number;
  loading: boolean;
}

const initialState: NotificationsState = {
  items: [],
  hasMore: false,
  unreadCount: 0,
  loading: false,
};

export const notificationsReducer = createReducer(
  initialState,

  // Replace items with API response (initial load)
  on(loadNotificationsSuccess, (state, { notifications, hasMore }) => ({
    ...state,
    items: notifications,
    hasMore,
    loading: false,
  })),

  // Append older items (load more / infinite scroll)
  on(loadMoreSuccess, (state, { notifications, hasMore }) => ({
    ...state,
    items: [...state.items, ...notifications],
    hasMore,
    loading: false,
  })),

  // Update unread badge count
  on(loadUnreadCountSuccess, (state, { count }) => ({
    ...state,
    unreadCount: count,
  })),

  // SSE live event — prepend, deduplicate by id, cap at 100
  on(addNotification, (state, { notification }) => {
    const exists = state.items.some(n => n.id === notification.id);
    if (exists) return state;
    return {
      ...state,
      items: [notification, ...state.items].slice(0, 100),
      unreadCount: state.unreadCount + 1,
    };
  }),

  on(markAsRead, (state, { id }) => ({
    ...state,
    items: state.items.map(n => n.id === id ? { ...n, read: true } : n),
    unreadCount: Math.max(0, state.unreadCount - (state.items.find(n => n.id === id && !n.read) ? 1 : 0)),
  })),

  on(markAllAsRead, (state) => ({
    ...state,
    items: state.items.map(n => ({ ...n, read: true })),
    unreadCount: 0,
  })),

  on(dismissNotification, (state, { id }) => ({
    ...state,
    items: state.items.filter(n => n.id !== id),
  })),

  on(clearAll, () => initialState),
);
