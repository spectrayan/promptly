import { notificationsReducer, NotificationsState } from './notifications.reducer';
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

/**
 * Unit tests for the notifications NgRx reducer.
 * Covers SSE live events, pagination, read/dismiss, and deduplication.
 */
describe('Notifications Reducer', () => {

  const initialState: NotificationsState = {
    items: [],
    hasMore: false,
    unreadCount: 0,
    loading: false,
  };

  const n1: Notification = {
    id: 'n-1', type: 'prompt.created', title: 'Prompt Created',
    message: 'Test prompt created', icon: 'edit_note',
    timestamp: '2026-01-01T00:00:00Z', read: false,
  };

  const n2: Notification = {
    id: 'n-2', type: 'workflow.approved', title: 'Approved',
    message: 'Approved by admin', icon: 'check_circle',
    timestamp: '2026-01-02T00:00:00Z', read: false,
  };

  // ── Initial state ──

  it('should return initial state for unknown action', () => {
    const state = notificationsReducer(undefined, { type: '[Unknown]' } as any);
    expect(state).toEqual(initialState);
  });

  // ── Load ──

  it('should replace items on loadNotificationsSuccess', () => {
    const state = notificationsReducer(
      { ...initialState, loading: true },
      loadNotificationsSuccess({ notifications: [n1], hasMore: true })
    );
    expect(state.items).toEqual([n1]);
    expect(state.hasMore).toBe(true);
    expect(state.loading).toBe(false);
  });

  // ── Load More (infinite scroll) ──

  it('should append items on loadMoreSuccess', () => {
    const prior: NotificationsState = { ...initialState, items: [n1] };
    const state = notificationsReducer(
      prior,
      loadMoreSuccess({ notifications: [n2], hasMore: false })
    );
    expect(state.items).toEqual([n1, n2]);
    expect(state.hasMore).toBe(false);
  });

  // ── Unread count ──

  it('should update unreadCount on loadUnreadCountSuccess', () => {
    const state = notificationsReducer(
      initialState,
      loadUnreadCountSuccess({ count: 5 })
    );
    expect(state.unreadCount).toBe(5);
  });

  // ── SSE: addNotification ──

  it('should prepend new notification and increment unread', () => {
    const state = notificationsReducer(
      initialState,
      addNotification({ notification: n1 })
    );
    expect(state.items).toEqual([n1]);
    expect(state.unreadCount).toBe(1);
  });

  it('should deduplicate — skip notification with existing id', () => {
    const prior: NotificationsState = { ...initialState, items: [n1], unreadCount: 1 };
    const state = notificationsReducer(
      prior,
      addNotification({ notification: n1 })
    );
    expect(state.items).toHaveLength(1);
    expect(state.unreadCount).toBe(1); // no increment
  });

  it('should cap items at 100', () => {
    const items: Notification[] = Array.from({ length: 100 }, (_, i) => ({
      ...n1, id: `n-${i}`,
    }));
    const prior: NotificationsState = { ...initialState, items };
    const newNotif = { ...n1, id: 'n-new' };
    const state = notificationsReducer(
      prior,
      addNotification({ notification: newNotif })
    );
    expect(state.items).toHaveLength(100);
    expect(state.items[0].id).toBe('n-new');
  });

  // ── Mark as read ──

  it('should mark single notification as read and decrement unread', () => {
    const prior: NotificationsState = {
      ...initialState,
      items: [n1, n2],
      unreadCount: 2,
    };
    const state = notificationsReducer(
      prior,
      markAsRead({ id: 'n-1' })
    );
    expect(state.items[0].read).toBe(true);
    expect(state.items[1].read).toBe(false);
    expect(state.unreadCount).toBe(1);
  });

  it('should not decrement unread if already read', () => {
    const readN1 = { ...n1, read: true };
    const prior: NotificationsState = {
      ...initialState,
      items: [readN1],
      unreadCount: 0,
    };
    const state = notificationsReducer(
      prior,
      markAsRead({ id: 'n-1' })
    );
    expect(state.unreadCount).toBe(0);
  });

  // ── Mark all as read ──

  it('should mark all as read and reset unread count', () => {
    const prior: NotificationsState = {
      ...initialState,
      items: [n1, n2],
      unreadCount: 2,
    };
    const state = notificationsReducer(
      prior,
      markAllAsRead()
    );
    expect(state.items.every(n => n.read)).toBe(true);
    expect(state.unreadCount).toBe(0);
  });

  // ── Dismiss ──

  it('should remove notification by id', () => {
    const prior: NotificationsState = {
      ...initialState,
      items: [n1, n2],
    };
    const state = notificationsReducer(
      prior,
      dismissNotification({ id: 'n-1' })
    );
    expect(state.items).toEqual([n2]);
  });

  // ── Clear All ──

  it('should reset to initial state on clearAll', () => {
    const prior: NotificationsState = {
      items: [n1, n2],
      hasMore: true,
      unreadCount: 5,
      loading: true,
    };
    const state = notificationsReducer(
      prior,
      clearAll()
    );
    expect(state).toEqual(initialState);
  });
});
