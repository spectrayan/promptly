import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ScannerFacade } from '../../state/scanner/scanner.facade';
import { PromptsFacade } from '../../state/prompts/prompts.facade';

@Component({
  selector: 'promptly-scan-results',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe, FormsModule,
    MatCardModule, MatTableModule, MatIconModule, MatChipsModule,
    MatButtonModule, MatTooltipModule, MatProgressSpinnerModule, MatSnackBarModule,
    MatPaginatorModule, MatSortModule, MatFormFieldModule, MatSelectModule,
  ],
  templateUrl: './scan-results.page.html',
  styleUrl: './scan-results.page.scss',
})
export class ScanResultsPage {
  readonly facade = inject(ScannerFacade);
  readonly promptsFacade = inject(PromptsFacade);
  private readonly snackBar = inject(MatSnackBar);
  private readonly route = inject(ActivatedRoute);

  displayedColumns = ['prompt', 'status', 'findings', 'scannedAt'];

  // ── Filters / sort / pagination ───────────────────────────────
  readonly statusFilter = signal<string[]>([]);
  readonly sortState = signal<Sort>({ active: 'scannedAt', direction: 'desc' });
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);

  readonly filteredScans = computed(() => {
    let data = [...this.facade.scans()];
    const statuses = this.statusFilter();
    if (statuses.length) {
      data = data.filter(s => statuses.includes(s.status ?? ''));
    }
    // Sort
    const sort = this.sortState();
    if (sort.active && sort.direction) {
      data.sort((a: any, b: any) => {
        const dir = sort.direction === 'asc' ? 1 : -1;
        const aVal = a[sort.active] ?? '';
        const bVal = b[sort.active] ?? '';
        return aVal < bVal ? -dir : aVal > bVal ? dir : 0;
      });
    }
    return data;
  });

  readonly paginatedScans = computed(() => {
    const all = this.filteredScans();
    const start = this.pageIndex() * this.pageSize();
    return all.slice(start, start + this.pageSize());
  });

  readonly hasActiveFilters = computed(() => this.statusFilter().length > 0);

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe(params => {
      const projectId = params.get('projectId') ?? undefined;
      this.facade.loadAllScans(projectId);
      this.promptsFacade.loadPrompts(projectId);
    });
  }

  getPromptName(promptId: string): string {
    const prompt = this.promptsFacade.prompts().find(p => p.id === promptId);
    return prompt?.name ?? promptId;
  }

  copyId(id: string): void {
    navigator.clipboard.writeText(id).then(() => {
      this.snackBar.open('Prompt ID copied!', 'OK', { duration: 2000 });
    });
  }

  onStatusFilterChange(values: string[]): void { this.statusFilter.set(values); this.pageIndex.set(0); }
  onSortChange(sort: Sort): void { this.sortState.set(sort); }
  clearFilters(): void { this.statusFilter.set([]); this.pageIndex.set(0); }
  onPageChange(e: PageEvent): void { this.pageIndex.set(e.pageIndex); this.pageSize.set(e.pageSize); }
}
