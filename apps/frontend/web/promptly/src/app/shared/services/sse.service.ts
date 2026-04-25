import { Injectable, inject, OnDestroy } from '@angular/core';
import { SseClient, StreamOptions } from '@spectrayan-sse/ng-sse-client';
import { SseClientHooks } from '@spectrayan-sse/ng-sse-client';
import { Observable, Subject, EMPTY, merge } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

/**
 * Promptly SSE service — thin wrapper over the Spectrayan SSE client library.
 *
 * Manages the lifecycle of SSE topic subscriptions, injects the JWT token
 * via query parameter (EventSource cannot send custom Authorization headers),
 * and provides a clean Observable API for consumers.
 */
@Injectable({ providedIn: 'root' })
export class SseService implements OnDestroy {
  private readonly sseClient = inject(SseClient);
  private readonly destroy$ = new Subject<void>();

  /**
   * Connect to the given SSE topic and receive a typed stream of events.
   * The JWT token is appended as a query parameter for authentication.
   *
   * @param topic The SSE topic to subscribe to (e.g. 'project-abc123')
   * @param events Optional named SSE event types to listen to
   */
  connect<T = unknown>(topic: string, events: string[] = []): Observable<T> {
    if (!topic) return EMPTY;

    const token = localStorage.getItem('promptly_access_token');
    const baseUrl = `${environment.apiBasePath}/api/v1/sse/${topic}`;
    const url = token ? `${baseUrl}?token=${encodeURIComponent(token)}` : baseUrl;

    const hooks: SseClientHooks = {
      onConnect: (u: string) => console.log('[SSE] Connecting:', u),
      onOpen: (info: { url: string; attempt: number }) =>
        console.log('[SSE] Connected (attempt', info.attempt, ')'),
      onError: (info: { event: Event; attempt: number; willRetry: boolean; nextDelayMs?: number }) => {
        if (info.willRetry) {
          console.warn(`[SSE] Error, retrying in ${info.nextDelayMs}ms (attempt ${info.attempt})`);
        } else {
          console.error('[SSE] Connection failed permanently');
        }
      },
      onClose: (info: { reason: string }) => console.log('[SSE] Closed:', info.reason),
    };

    return this.sseClient.stream<T>(url, {
      events,
      reconnection: {
        enabled: true,
        maxRetries: -1,
        initialDelayMs: 1000,
        maxDelayMs: 30000,
        backoffMultiplier: 2,
        jitterRatio: 0.2,
      },
      hooks,
    }).pipe(
      takeUntil(this.destroy$),
    );
  }

  /**
   * Subscribe to multiple topics by merging their streams.
   */
  connectMultiple<T = unknown>(topics: string[], events: string[] = []): Observable<T> {
    if (topics.length === 0) return EMPTY;

    const streams = topics.map(topic => this.connect<T>(topic, events));
    return merge(...streams);
  }

  /** Disconnect all active SSE streams. */
  disconnect(): void {
    this.destroy$.next();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
