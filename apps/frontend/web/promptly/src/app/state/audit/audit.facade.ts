import { Injectable, inject, signal } from '@angular/core';
import { AuditService, AuditResponse } from '@promptly/client';
import { ErrorMessages } from '../../shared/constants/error-messages';

/**
 * Audit facade — signal-based (no NgRx).
 * Read-only list with SSE append. No cross-feature sharing needed.
 */
@Injectable({ providedIn: 'root' })
export class AuditFacade {
  private readonly api = inject(AuditService);

  readonly logs = signal<AuditResponse[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  loadLogs(projectId?: string): void {
    this.loading.set(true);
    this.error.set(null);

    this.api.getAuditLogs({ projectId }).subscribe({
      next: logs => {
        this.logs.set(logs);
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err?.error?.detail ?? ErrorMessages.LOAD_AUDIT);
        this.loading.set(false);
      },
    });
  }

  /** Append a single log entry (used by SSE listener) */
  appendLog(log: AuditResponse): void {
    this.logs.update(current => [log, ...current]);
  }
}
