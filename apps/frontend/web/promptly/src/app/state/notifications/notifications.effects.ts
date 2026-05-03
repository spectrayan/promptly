/** NgRx effects for real-time Notifications — handles API calls and SSE stream subscription. */
import { Injectable, inject, OnDestroy } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SseService } from '../../shared/services/sse.service';
import { NotificationsService, NotificationResponse } from '@promptly/client';
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
  'prompt.created': { icon: 'edit_note', title: 'Prompt Created' },
  'prompt.updated': { icon: 'update', title: 'Prompt Updated' },
  'workflow.approved': { icon: 'check_circle', title: 'Workflow Approved' },
  'workflow.rejected': { icon: 'cancel', title: 'Workflow Rejected' },
  'scan.completed': { icon: 'verified', title: 'Scan Completed' },
  'scan.critical': { icon: 'warning', title: 'Critical Findings' },
};

const NOTIFICATION_TEMPLATES: Record<string, string> = {
  'prompt.created': 'Prompt "{promptName}" was created',
  'prompt.updated': 'Prompt "{promptName}" updated to version {version}',
  'workflow.approved': 'Prompt "{promptName}" approved by {approvedBy}',
  'workflow.rejected': 'Prompt "{promptName}" rejected by {rejectedBy}: {reason}',
  'scan.completed': 'Prompt "{promptName}" scan finished with score {score}',
  'scan.critical': 'Prompt "{promptName}" critical findings detected! Score: {score}',
};

interface SseEvent {
  eventType?: string;
  promptId?: string;
  promptName?: string;
  version?: number;
  approvedBy?: string;
  rejectedBy?: string;
  reason?: string;
  status?: string;
  score?: number;
  [key: string]: unknown;
}

@Injectable()
export class NotificationsEffects implements OnDestroy {
  private readonly actions$ = inject(Actions);
  private readonly notificationsService = inject(NotificationsService);
  private readonly sse = inject(SseService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly disconnect$ = new Subject<void>();

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
                  horizontalPosition: 'center',
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
        this.notificationsService.listNotifications({ projectId, limit: 20 }).pipe(
          map(res => loadNotificationsSuccess({
            notifications: (res.items || []).map(item => this.mapApiNotification(item)),
            hasMore: res.hasMore || false,
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
        this.notificationsService.listNotifications({ projectId, before, limit: 20 }).pipe(
          map(res => loadMoreSuccess({
            notifications: (res.items || []).map(item => this.mapApiNotification(item)),
            hasMore: res.hasMore || false,
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
        this.notificationsService.getUnreadNotificationCount({ projectId }).pipe(
          map(res => loadUnreadCountSuccess({ count: res.unread || 0 })),
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
        this.notificationsService.markNotificationAsRead({ id }).subscribe();
        return [markAsRead({ id })];
      }),
    ),
  );

  /** Mark all as read — optimistic UI + backend sync */
  readonly markAllAsReadApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(markAllAsReadApi),
      mergeMap(({ projectId }) => {
        this.notificationsService.markAllNotificationsRead({ projectId }).subscribe();
        return [markAllAsRead()];
      }),
    ),
  );

  /** Dismiss — optimistic UI + backend sync */
  readonly dismissApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(dismissApi),
      mergeMap(({ id }) => {
        this.notificationsService.dismissNotification({ id }).subscribe();
        return [dismissNotification({ id })];
      }),
    ),
  );

  /** Clear all — delete from backend + clear local store */
  readonly clearAllApi$ = createEffect(() =>
    this.actions$.pipe(
      ofType(clearAllApi),
      mergeMap(({ projectId }) => {
        this.notificationsService.clearAllNotifications({ projectId }).subscribe();
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
  private mapApiNotification(item: NotificationResponse): Notification {
    return {
      id: item.id!,
      type: item.type!,
      title: item.title!,
      message: item.message!,
      icon: item.icon!,
      timestamp: item.createdAt!,
      read: item.read!,
      projectId: item.projectId,
      payload: item.payload,
    };
  }

  private buildMessage(eventType: string, event: SseEvent): string {
    const template = NOTIFICATION_TEMPLATES[eventType];
    if (!template) {
      return JSON.stringify(event).slice(0, 100);
    }

    const tokens: Record<string, any> = {
      ...event,
      promptName: event.promptName ?? event.promptId,
      version: event.version ?? '?',
      approvedBy: event.approvedBy ?? 'unknown',
      rejectedBy: event.rejectedBy ?? 'unknown',
      score: event.score ?? '?',
      reason: event.reason ?? '',
    };

    return template.replace(/{(\w+)}/g, (_, key) => String(tokens[key] ?? ''));
  }
}
