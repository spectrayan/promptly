import { Injectable, inject, signal } from '@angular/core';
import { ScannerService, ScanResponse } from '@promptly/client';

/**
 * Scanner facade — signal-based (no NgRx).
 * Triggered from prompt detail page, results are prompt-scoped.
 */
@Injectable({ providedIn: 'root' })
export class ScannerFacade {
  private readonly api = inject(ScannerService);

  readonly scans = signal<ScanResponse[]>([]);
  readonly currentScan = signal<ScanResponse | null>(null);
  readonly scanning = signal(false);
  readonly error = signal<string | null>(null);

  loadAllScans(projectId?: string): void {
    this.api.getAllScans({ projectId }).subscribe({
      next: scans => this.scans.set(scans),
      error: err => this.error.set(err?.error?.detail ?? 'Failed to load scans'),
    });
  }

  triggerScan(promptId: string): void {
    this.scanning.set(true);
    this.error.set(null);

    this.api.triggerScan({ promptId }).subscribe({
      next: scan => {
        this.currentScan.set(scan);
        this.scanning.set(false);
      },
      error: err => {
        this.error.set(err?.error?.detail ?? 'Scan failed');
        this.scanning.set(false);
      },
    });
  }

  loadScanResult(promptId: string): void {
    this.api.getLatestScanResult({ promptId }).subscribe({
      next: (scan: ScanResponse) => this.currentScan.set(scan),
      error: () => this.currentScan.set(null),
    });
  }
}
