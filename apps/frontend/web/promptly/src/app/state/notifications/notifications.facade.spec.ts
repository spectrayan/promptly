import { TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { NotificationsFacade } from './notifications.facade';
import {
  connectSse,
  disconnectSse,
  loadNotifications,
  loadMore,
  loadUnreadCount,
  markAsReadApi,
  markAllAsReadApi,
  dismissApi,
  clearAllApi,
} from './notifications.actions';
import {
  selectNotificationItems,
  selectUnreadCount,
  selectHasMore,
  selectNotificationsLoading,
} from './notifications.selectors';

describe('NotificationsFacade', () => {
  let facade: NotificationsFacade;
  let store: MockStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        NotificationsFacade,
        provideMockStore({
          selectors: [
            { selector: selectNotificationItems, value: [] },
            { selector: selectUnreadCount, value: 0 },
            { selector: selectHasMore, value: false },
            { selector: selectNotificationsLoading, value: false },
          ],
        }),
      ],
    });

    facade = TestBed.inject(NotificationsFacade);
    store = TestBed.inject(MockStore);
    vi.spyOn(store, 'dispatch');
  });

  // ── Read ──
  describe('read signals', () => {
    it('items defaults to empty array', () => {
      expect(facade.items()).toEqual([]);
    });

    it('unreadCount defaults to 0', () => {
      expect(facade.unreadCount()).toBe(0);
    });

    it('hasMore defaults to false', () => {
      expect(facade.hasMore()).toBe(false);
    });

    it('loading defaults to false', () => {
      expect(facade.loading()).toBe(false);
    });
  });

  // ── Commands ──
  describe('commands', () => {
    it('dispatches connectSse', () => {
      facade.connectSse('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(connectSse({ projectId: 'proj-1' }));
    });

    it('dispatches disconnectSse', () => {
      facade.disconnectSse();
      expect(store.dispatch).toHaveBeenCalledWith(disconnectSse());
    });

    it('dispatches loadNotifications', () => {
      facade.load('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(loadNotifications({ projectId: 'proj-1' }));
    });

    it('dispatches loadMore', () => {
      facade.loadMore('proj-1', '2026-01-01T00:00:00Z');
      expect(store.dispatch).toHaveBeenCalledWith(
        loadMore({ projectId: 'proj-1', before: '2026-01-01T00:00:00Z' })
      );
    });

    it('dispatches loadUnreadCount', () => {
      facade.loadUnreadCount('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(loadUnreadCount({ projectId: 'proj-1' }));
    });

    it('dispatches markAsReadApi', () => {
      facade.markAsRead('n-1');
      expect(store.dispatch).toHaveBeenCalledWith(markAsReadApi({ id: 'n-1' }));
    });

    it('dispatches markAllAsReadApi', () => {
      facade.markAllAsRead('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(markAllAsReadApi({ projectId: 'proj-1' }));
    });

    it('dispatches dismissApi', () => {
      facade.dismiss('n-1');
      expect(store.dispatch).toHaveBeenCalledWith(dismissApi({ id: 'n-1' }));
    });

    it('dispatches clearAllApi', () => {
      facade.clearAll('proj-1');
      expect(store.dispatch).toHaveBeenCalledWith(clearAllApi({ projectId: 'proj-1' }));
    });
  });
});
