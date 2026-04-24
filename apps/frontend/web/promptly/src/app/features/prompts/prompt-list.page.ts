import { Component, OnInit, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { PromptsFacade } from '../../state/prompts/prompts.facade';

@Component({
  selector: 'promptly-prompt-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe,
    MatCardModule, MatTableModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatTooltipModule, MatProgressSpinnerModule, MatSnackBarModule,
    MatPaginatorModule, MatSortModule,
  ],
  templateUrl: './prompt-list.page.html',
  styleUrl: './prompt-list.page.scss',
})
export class PromptListPage implements OnInit {
  readonly facade = inject(PromptsFacade);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  displayedColumns = ['name', 'currentVersion', 'updatedAt'];

  readonly sortState = signal<Sort>({ active: 'updatedAt', direction: 'desc' });
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);

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

  ngOnInit(): void {
    this.facade.loadPrompts();
  }

  goToPrompt(id: string): void {
    this.router.navigate(['/prompts', id]);
  }

  openCreatePage(): void {
    this.router.navigate(['/prompts/create']);
  }

  copyId(id: string, event: Event): void {
    event.stopPropagation();
    navigator.clipboard.writeText(id).then(() => {
      this.snackBar.open('Prompt ID copied!', 'OK', { duration: 2000 });
    });
  }

  onSortChange(sort: Sort): void { this.sortState.set(sort); }
  onPageChange(event: PageEvent): void { this.pageIndex.set(event.pageIndex); this.pageSize.set(event.pageSize); }
}
