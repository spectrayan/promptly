import { createReducer, on } from '@ngrx/store';
import {
  Notification,
  addNotification,
  markAsRead,
  markAllAsRead,
  dismissNotification,
  clearAll,
} from './notifications.actions';

export interface NotificationsState {
  items: Notification[];
}

const initialState: NotificationsState = {
  items: [],
};

export const notificationsReducer = createReducer(
  initialState,
  on(addNotification, (state, { notification }) => ({
    ...state,
    items: [notification, ...state.items].slice(0, 50), // keep max 50
  })),
  on(markAsRead, (state, { id }) => ({
    ...state,
    items: state.items.map(n => n.id === id ? { ...n, read: true } : n),
  })),
  on(markAllAsRead, (state) => ({
    ...state,
    items: state.items.map(n => ({ ...n, read: true })),
  })),
  on(dismissNotification, (state, { id }) => ({
    ...state,
    items: state.items.filter(n => n.id !== id),
  })),
  on(clearAll, () => initialState),
);
