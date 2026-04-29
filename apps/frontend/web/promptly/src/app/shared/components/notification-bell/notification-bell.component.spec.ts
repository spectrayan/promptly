import { TestBed, ComponentFixture } from '@angular/core/testing';
import { signal } from '@angular/core';
import { NotificationBellComponent } from './notification-bell.component';
import { NotificationsFacade } from '../../../state/notifications/notifications.facade';
import { Notification } from '../../../state/notifications/notifications.actions';

/**
 * A test-double for NotificationsFacade that exposes writable signals.
 */
function createMockFacade() {
  return {
    items: signal<Notification[]>([]),
    unreadCount: signal<number>(0),
    hasMore: signal<boolean>(false),
    loading: signal<boolean>(false),
    connectSse: vi.fn(),
    disconnectSse: vi.fn(),
    load: vi.fn(),
    loadMore: vi.fn(),
    loadUnreadCount: vi.fn(),
    markAsRead: vi.fn(),
    markAllAsRead: vi.fn(),
    dismiss: vi.fn(),
    clearAll: vi.fn(),
  };
}

describe('NotificationBellComponent', () => {
  let fixture: ComponentFixture<NotificationBellComponent>;
  let component: NotificationBellComponent;
  let mockFacade: ReturnType<typeof createMockFacade>;

  beforeEach(async () => {
    mockFacade = createMockFacade();

    await TestBed.configureTestingModule({
      imports: [NotificationBellComponent],
      providers: [
        { provide: NotificationsFacade, useValue: mockFacade },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationBellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates the component', () => {
    expect(component).toBeTruthy();
  });

  // ── Signal exposure ──
  describe('signal bindings', () => {
    it('exposes items from facade', () => {
      expect(component.items()).toEqual([]);
    });

    it('exposes unreadCount from facade', () => {
      expect(component.unreadCount()).toBe(0);
    });

    it('reflects updated unreadCount', () => {
      mockFacade.unreadCount.set(5);
      expect(component.unreadCount()).toBe(5);
    });
  });

  // ── Behavior ──
  describe('user actions', () => {
    const mockNotification: Notification = {
      id: 'n-1',
      type: 'prompt.created',
      title: 'Prompt Created',
      message: 'A new prompt was created',
      icon: 'edit_note',
      timestamp: '2026-01-01T00:00:00Z',
      read: false,
      projectId: 'proj-1',
      payload: {},
    };

    it('calls markAsRead when notification is unread', () => {
      component.onRead(mockNotification);
      expect(mockFacade.markAsRead).toHaveBeenCalledWith('n-1');
    });

    it('does NOT call markAsRead when notification is already read', () => {
      component.onRead({ ...mockNotification, read: true });
      expect(mockFacade.markAsRead).not.toHaveBeenCalled();
    });

    it('calls dismiss and stops event propagation', () => {
      const event = { stopPropagation: vi.fn() } as unknown as Event;
      component.onDismiss(event, 'n-1');
      expect(event.stopPropagation).toHaveBeenCalled();
      expect(mockFacade.dismiss).toHaveBeenCalledWith('n-1');
    });

    it('calls markAllAsRead using projectId from first item', () => {
      mockFacade.items.set([mockNotification]);
      component.onMarkAllRead();
      expect(mockFacade.markAllAsRead).toHaveBeenCalledWith('proj-1');
    });

    it('does NOT call markAllAsRead when items are empty', () => {
      mockFacade.items.set([]);
      component.onMarkAllRead();
      expect(mockFacade.markAllAsRead).not.toHaveBeenCalled();
    });

    it('calls clearAll with projectId from first item', () => {
      mockFacade.items.set([mockNotification]);
      component.onClearAll();
      expect(mockFacade.clearAll).toHaveBeenCalledWith('proj-1');
    });

    it('does NOT call clearAll when items are empty', () => {
      mockFacade.items.set([]);
      component.onClearAll();
      expect(mockFacade.clearAll).not.toHaveBeenCalled();
    });
  });
});
