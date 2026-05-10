import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ExchangeService } from '@promptly/client/api/exchange.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PromptsFacade } from '../../../state/prompts/prompts.facade';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';
import { PromptImportDialog } from './prompt-import-dialog/prompt-import-dialog.component';

@Component({
  selector: 'promptly-prompt-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe,
    MatCardModule, MatTableModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatTooltipModule, MatProgressSpinnerModule, MatSnackBarModule,
    MatPaginatorModule, MatSortModule, MatMenuModule, MatDialogModule, EnumLabelPipe,
  ],
  templateUrl: './prompt-list.page.html',
  styleUrl: './prompt-list.page.scss',
})
export class PromptListPage {
  readonly facade = inject(PromptsFacade);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);
  private readonly exchangeService = inject(ExchangeService);

  projectId: string | null = null;

  displayedColumns = ['name', 'status', 'currentVersion', 'updatedAt'];

  readonly sortState = signal<Sort>({ active: 'updatedAt', direction: 'desc' });
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);
  readonly viewMode = signal<'list' | 'card'>((localStorage.getItem('promptViewMode') as 'list' | 'card') || 'list');

  /** Client-side sorting */
  readonly sortedPrompts = computed(() => {
    let data = [...this.facade.prompts()];
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

  /** Paginated slice */
  readonly paginatedPrompts = computed(() => {
    const all = this.sortedPrompts();
    const start = this.pageIndex() * this.pageSize();
    return all.slice(start, start + this.pageSize());
  });

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe(params => {
      this.projectId = params.get('projectId');
      this.facade.loadPrompts(this.projectId ?? undefined);
    });
  }

  goToPrompt(id: string): void {
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId, 'prompts', id]);
    }
  }

  openCreatePage(): void {
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId, 'prompts', 'create']);
    }
  }

  copyId(id: string, event: Event): void {
    event.stopPropagation();
    navigator.clipboard.writeText(id).then(() => {
      this.snackBar.open('Prompt ID copied!', 'OK', { duration: 2000 });
    });
  }

  onSortChange(sort: Sort): void { this.sortState.set(sort); }
  onPageChange(event: PageEvent): void { this.pageIndex.set(event.pageIndex); this.pageSize.set(event.pageSize); }

  toggleViewMode(): void {
    const newMode = this.viewMode() === 'list' ? 'card' : 'list';
    this.viewMode.set(newMode);
    localStorage.setItem('promptViewMode', newMode);
  }

  openImportDialog(): void {
    if (!this.projectId) return;
    this.dialog.open(PromptImportDialog, {
      width: '600px',
      panelClass: 'glass-dialog',
      data: { projectId: this.projectId }
    }).afterClosed().subscribe(result => {
      if (result && this.projectId) {
        this.facade.loadPrompts(this.projectId);
      }
    });
  }

  exportAll(): void {
    if (!this.projectId) return;
    this.exchangeService.exportByProject({ projectId: this.projectId }).subscribe({
      next: (manifest: any) => {
        const blob = new Blob([JSON.stringify(manifest, null, 2)], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `export-${this.projectId}-${new Date().toISOString().split('T')[0]}.json`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.snackBar.open('Export successful!', 'OK', { duration: 3000 });
      },
      error: (err: any) => this.snackBar.open('Export failed', 'Close', { duration: 3000 })
    });
  }
}
