import {
  selectNotificationItems,
  selectUnreadCount,
  selectHasMore,
  selectNotificationsLoading,
} from './notifications.selectors';
import { NotificationsState } from './notifications.reducer';

describe('Notification Selectors', () => {
  const state: NotificationsState = {
    items: [
      { id: 'n-1', type: 'prompt.created', title: 'Created', message: 'msg', icon: 'edit_note', timestamp: 'ts', read: false, projectId: 'p1', payload: {} },
      { id: 'n-2', type: 'prompt.updated', title: 'Updated', message: 'msg', icon: 'update', timestamp: 'ts', read: true, projectId: 'p1', payload: {} },
    ],
    unreadCount: 1,
    hasMore: true,
    loading: false,
  };

  it('selectNotificationItems returns items', () => {
    const result = selectNotificationItems.projector(state);
    expect(result).toHaveLength(2);
    expect(result[0].id).toBe('n-1');
  });

  it('selectUnreadCount returns unread count', () => {
    expect(selectUnreadCount.projector(state)).toBe(1);
  });

  it('selectHasMore returns hasMore flag', () => {
    expect(selectHasMore.projector(state)).toBe(true);
  });

  it('selectNotificationsLoading returns loading flag', () => {
    expect(selectNotificationsLoading.projector(state)).toBe(false);
  });

  // ── Null-safety (selectors use ?. and ?? for defensive access) ──
  describe('null-safety', () => {
    it('selectNotificationItems returns empty array for null state', () => {
      expect(selectNotificationItems.projector(null as any)).toEqual([]);
    });

    it('selectUnreadCount returns 0 for undefined state', () => {
      expect(selectUnreadCount.projector(undefined as any)).toBe(0);
    });

    it('selectHasMore returns false for null state', () => {
      expect(selectHasMore.projector(null as any)).toBe(false);
    });

    it('selectNotificationsLoading returns false for null state', () => {
      expect(selectNotificationsLoading.projector(null as any)).toBe(false);
    });
  });
});
