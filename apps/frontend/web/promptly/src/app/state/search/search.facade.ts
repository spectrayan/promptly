import { Injectable, inject, signal } from '@angular/core';
import { SearchService, SearchResponse, DuplicateCheckResponse } from '@promptly/client';

/**
 * Search facade — signal-based (no NgRx).
 * Stateless query→results. No caching or cross-feature sharing.
 */
@Injectable({ providedIn: 'root' })
export class SearchFacade {
  private readonly api = inject(SearchService);

  readonly results = signal<SearchResponse[]>([]);
  readonly duplicates = signal<DuplicateCheckResponse | null>(null);
  readonly query = signal('');
  readonly searching = signal(false);

  search(q: string): void {
    this.query.set(q);
    if (!q.trim()) {
      this.results.set([]);
      return;
    }

    this.searching.set(true);
    this.api.searchPrompts({ q }).subscribe({
      next: results => {
        this.results.set(results);
        this.searching.set(false);
      },
      error: () => {
        this.results.set([]);
        this.searching.set(false);
      },
    });
  }

  findSimilar(promptId: string): void {
    this.searching.set(true);
    this.api.findSimilarPrompts({ promptId }).subscribe({
      next: results => {
        this.results.set(results);
        this.searching.set(false);
      },
      error: () => {
        this.results.set([]);
        this.searching.set(false);
      },
    });
  }

  checkDuplicates(promptId: string): void {
    this.api.checkDuplicates({ promptId }).subscribe({
      next: result => this.duplicates.set(result),
      error: () => this.duplicates.set(null),
    });
  }

  clearResults(): void {
    this.results.set([]);
    this.query.set('');
  }
}
