/** Memoized selectors for the Notifications feature slice. Consumed by NotificationsFacade. */
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { NotificationsState } from './notifications.reducer';

export const selectNotificationsState = createFeatureSelector<NotificationsState>('notifications');

export const selectNotificationItems = createSelector(
  selectNotificationsState,
  (state) => state?.items ?? []
);

export const selectUnreadCount = createSelector(
  selectNotificationsState,
  (state) => state?.unreadCount ?? 0
);

export const selectHasMore = createSelector(
  selectNotificationsState,
  (state) => state?.hasMore ?? false
);

export const selectNotificationsLoading = createSelector(
  selectNotificationsState,
  (state) => state?.loading ?? false
);
