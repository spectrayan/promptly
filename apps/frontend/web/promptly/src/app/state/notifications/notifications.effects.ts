/** NgRx effects for real-time Notifications — handles API calls and SSE stream subscription. */
import { Injectable, inject, OnDestroy } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SseService } from '../../shared/services/sse.service';
import { environment } from '../../../environments/environment';
import {
  connectSse,
  disconnectSse,
  loadNotifications,
  loadNotificationsSuccess,
  loadMore,
  loadMoreSuccess,
  loadUnreadCount,
  loadUnreadCountSuccess,
  markAsReadApi,
  markAsRead,
  markAllAsReadApi,
  markAllAsRead,
  dismissApi,
  dismissNotification,
  clearAllApi,
  clearAll,
  Notification,
} from './notifications.actions';
import { EMPTY, Subject, switchMap, map, tap, takeUntil, catchError, mergeMap, timer, exhaustMap } from 'rxjs';

/** Maps SSE event types to human-readable notification details */
const EVENT_META: Record<string, { icon: string; title: string }> = {
  'prompt.created':     { icon: 'edit_note',     title: 'Prompt Created' },
  'prompt.updated':     { icon: 'update',        title: 'Prompt Updated' },
  'workflow.approved':  { icon: 'check_circle',  title: 'Workflow Approved' },
  'workflow.rejected':  { icon: 'cancel',        title: 'Workflow Rejected' },
  'scan.completed':     { icon: 'verified',      title: 'Scan Completed' },
  'scan.critical':      { icon: 'warning',       title: 'Critical Findings' },
};

interface SseEvent {
  eventType?: string;
  promptId?: string;
  name?: string;
  version?: number;
  approvedBy?: string;
  rejectedBy?: string;
  reason?: string;
  status?: string;
  score?: number;
  [key: string]: unknown;
}

interface NotificationListResponse {
  items: Array<{
    id: string;
    type: string;
    title: string;
    message: string;
    icon: string;
    read: boolean;
    createdAt: string;
    projectId: string;
    payload: Record<string, unknown>;
  }>;
  hasMore: boolean;
  oldestTimestamp?: string;
}

@Injectable()
export class NotificationsEffects implements OnDestroy {
  private readonly actions$ = inject(Actions);
  private readonly http = inject(HttpClient);
  private readonly sse = inject(SseService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly disconnect$ = new Subject<void>();
  private readonly apiBase = environment.apiBasePath;

  /**
   * Timestamp after which SSE events should trigger snackbars.
   * Set to ~3s after each SSE connect to suppress REPLAY'd events.
   * Replayed events still update the notification list via API reload.
   */
  private sseReadyAt = 0;

  /**
   * Connect to SSE — when an event arrives, refresh from the API.
   * The backend already persists the notification during fan-out,
   * so we just reload to get properly formatted data with real IDs.
   *
   * Uses mergeMap on inner events so rapid SSE emissions (fan-out
   * emits per-member) don't cancel each other's API reload.
   *
   * Snackbar notifications are suppressed for the first 3 seconds
   * after connect to avoid showing toasts for REPLAY'd (old) events.
   */
  readonly connectSse$ = createEffect(() =>
    this.actions$.pipe(
      ofType(connectSse),
      switchMap(({ projectId }) => {
        this.disconnect$.next();
        this.currentProjectId = projectId;

        // Suppress snackbar for replayed events (replay is instant, real events come later)
        this.sseReadyAt = Date.now() + 3_000;

        const topic = `project-${projectId}`;
        const events = Object.keys(EVENT_META);

        return this.sse.connect<SseEvent>(topic, events).pipe(
          takeUntil(this.disconnect$),
          mergeMap(event => {
            const eventType = (event as any)?.eventType ?? 'notification';
            const meta = EVENT_META[eventType] ?? { icon: 'notifications', title: 'Notification' };

            // Use backend-provided title/message when available (avoids raw JSON)
            const title = (event as any)?._title ?? meta.title;
            const message = (event as any)?._message ?? this.buildMessage(eventType, event);

            // Only show snackbar for genuinely new events (not REPLAY'd ones)
            if (Date.now() >= this.sseReadyAt) {
              this.snackBar.open(
                `${title}: ${message}`,
                'Dismiss',
                {
                  duration: 5000,
                  horizontalPosition: 'end',
                  verticalPosition: 'bottom',
                  panelClass: [`snack-${eventType.split('.')[0]}`],
                },
              );
            }

            // Reload persisted notifications from the API
            return [
              loadNotifications({ projectId }),
              loadUnreadCount({ projectId }),
            ];
          }),
        );
      }),
    ),
  );

  /**
   * Polling fallback — refreshes notifications every 10s while connected.
   * Ensures the UI stays current even if the SSE transport silently drops.
   */
  readonly pollNotifications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(connectSse),
      switchMap(({ projectId }) =>
        timer(10_000, 10_000).pipe(
          takeUntil(this.disconnect$),
          exhaustMap(() => [
            loadNotifications({ projectId }),
            loadUnreadCount({ projectId }),
          ]),
        ),
      ),
    ),
  );

  private currentProjectId = '';

  /** Load persisted notifications on project switch */
  readonly loadOnConnect$ = createEffect(() =>
    this.actions$.pipe(
      ofType(connectSse),
      switchMap(({ projectId }) => [
        loadNotifications({ projectId }),
        loadUnreadCount({ projectId }),
      ]),
    ),
  );

  /** Fetch notifications from API */
  readonly loadNotifications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadNotifications),
      switchMap(({ projectId }) =>
        this.http.get<NotificationListResponse>(
          `${this.apiBase}/api/v1/notifications?projectId=${projectId}&limit=20`
        ).pipe(
          map(res => loadNotificationsSuccess({
            notifications: res.items.map(item => this.mapApiNotification(item)),
            hasMore: res.hasMore,
          })),
          catchError(() => EMPTY),
        ),
      ),
    ),
  );

  /** Load more (infinite scroll) */
  readonly loadMore$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadMore),
      switchMap(({ projectId, before }) =>
        this.http.get<NotificationListResponse>(
          `${this.apiBase}/api/v1/notifications?projectId=${projectId}&before=${before}&limit=20`
        ).pipe(
          map(res => loadMoreSuccess({
            notifications: res.items.map(item => this.mapApiNotification(item)),
            hasMore: res.hasMore,
          })),
          catchError(() => EMPTY),
        ),
      ),
    ),
  );

  /** Load unread count for badge */
  readonly loadUnreadCount$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadUnreadCount),
      switchMap(({ projectId }) =>
        this.http.get<{ unread: number }>(
          `${this.apiBase}/api/v1/notifications/count?projectId=${projectId}`
        ).pipe(
          map(res => loadUnreadCountSuccess({ count: res.unread })),
          catchError(() => EMPTY),
        ),
      ),
    ),
  );

  /** Mark as read — optimistic UI + backend sync */
  readonly markAsReadApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(markAsReadApi),
      mergeMap(({ id }) => {
        this.http.patch(`${this.apiBase}/api/v1/notifications/${id}/read`, {}).subscribe();
        return [markAsRead({ id })];
      }),
    ),
  );

  /** Mark all as read — optimistic UI + backend sync */
  readonly markAllAsReadApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(markAllAsReadApi),
      mergeMap(({ projectId }) => {
        this.http.post(`${this.apiBase}/api/v1/notifications/mark-all-read?projectId=${projectId}`, {}).subscribe();
        return [markAllAsRead()];
      }),
    ),
  );

  /** Dismiss — optimistic UI + backend sync */
  readonly dismissApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(dismissApi),
      mergeMap(({ id }) => {
        this.http.delete(`${this.apiBase}/api/v1/notifications/${id}/read`).subscribe();
        return [dismissNotification({ id })];
      }),
    ),
  );

  /** Clear all — delete from backend + clear local store */
  readonly clearAllApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(clearAllApi),
      mergeMap(({ projectId }) => {
        this.http.delete(`${this.apiBase}/api/v1/notifications?projectId=${projectId}`).subscribe();
        return [clearAll()];
      }),
    ),
  );

  /** Disconnect SSE */
  readonly disconnectSse$ = createEffect(() =>
    this.actions$.pipe(
      ofType(disconnectSse),
      tap(() => this.disconnect$.next()),
    ),
    { dispatch: false },
  );

  ngOnDestroy(): void {
    this.disconnect$.next();
    this.disconnect$.complete();
  }

  /** Map API response to frontend Notification */
  private mapApiNotification(item: NotificationListResponse['items'][0]): Notification {
    return {
      id: item.id,
      type: item.type,
      title: item.title,
      message: item.message,
      icon: item.icon,
      timestamp: item.createdAt,
      read: item.read,
      projectId: item.projectId,
      payload: item.payload,
    };
  }

  private buildMessage(eventType: string, event: SseEvent): string {
    switch (eventType) {
      case 'prompt.created':
        return `Prompt "${event.name ?? event.promptId}" was created`;
      case 'prompt.updated':
        return `Prompt updated to version ${event.version ?? '?'}`;
      case 'workflow.approved':
        return `Approved by ${event.approvedBy ?? 'unknown'}`;
      case 'workflow.rejected':
        return `Rejected by ${event.rejectedBy ?? 'unknown'}: ${event.reason ?? ''}`;
      case 'scan.completed':
        return `Scan finished with score ${event.score ?? '?'}`;
      case 'scan.critical':
        return `Critical findings detected! Score: ${event.score ?? '?'}`;
      default:
        return JSON.stringify(event).slice(0, 100);
    }
  }
}
