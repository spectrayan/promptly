import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SseService } from '../../shared/services/sse.service';
import {
  addNotification,
  connectSse,
  disconnectSse,
  Notification,
} from './notifications.actions';
import { EMPTY, Subject, switchMap, map, tap, takeUntil } from 'rxjs';

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

@Injectable()
export class NotificationsEffects {
  private readonly actions$ = inject(Actions);
  private readonly sse = inject(SseService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly disconnect$ = new Subject<void>();

  /** Connect to SSE on project selection */
  readonly connectSse$ = createEffect(() =>
    this.actions$.pipe(
      ofType(connectSse),
      switchMap(({ projectId }) => {
        this.disconnect$.next(); // Close any previous connection

        const topic = `project-${projectId}`;
        const events = Object.keys(EVENT_META);

        return this.sse.connect<SseEvent>(topic, events).pipe(
          takeUntil(this.disconnect$),
          map(event => {
            const eventType = (event as any)?.eventType ?? 'notification';
            const meta = EVENT_META[eventType] ?? { icon: 'notifications', title: 'Notification' };

            const notification: Notification = {
              id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
              type: eventType,
              title: meta.title,
              message: this.buildMessage(eventType, event),
              icon: meta.icon,
              timestamp: new Date().toISOString(),
              read: false,
              projectId,
              payload: event as Record<string, unknown>,
            };

            return addNotification({ notification });
          }),
        );
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

  /** Show snackbar for new notifications */
  readonly toast$ = createEffect(() =>
    this.actions$.pipe(
      ofType(addNotification),
      tap(({ notification }) => {
        this.snackBar.open(
          `${notification.title}: ${notification.message}`,
          'Dismiss',
          {
            duration: 5000,
            horizontalPosition: 'end',
            verticalPosition: 'bottom',
            panelClass: [`snack-${notification.type.split('.')[0]}`],
          },
        );
      }),
    ),
    { dispatch: false },
  );

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
