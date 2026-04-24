import { Component, OnInit, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { WorkflowsFacade } from '../../state/workflows/workflows.facade';
import { PromptsFacade } from '../../state/prompts/prompts.facade';

@Component({
  selector: 'promptly-workflow-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe, FormsModule, MatCardModule, MatTableModule, MatButtonModule,
    MatIconModule, MatChipsModule, MatTooltipModule,
    MatProgressSpinnerModule, MatSnackBarModule,
    MatPaginatorModule, MatSortModule, MatFormFieldModule, MatSelectModule,
  ],
  templateUrl: './workflow-list.page.html',
  styleUrl: './workflow-list.page.scss',
})
export class WorkflowListPage implements OnInit {
  readonly facade = inject(WorkflowsFacade);
  readonly promptsFacade = inject(PromptsFacade);
  private readonly snackBar = inject(MatSnackBar);

  displayedColumns = ['prompt', 'status', 'targetEnvironment', 'requestedBy', 'createdAt', 'actions'];

  // ── Filters / sort / pagination ───────────────────────────────
  readonly statusFilter = signal<string[]>([]);
  readonly targetEnvFilter = signal<string[]>([]);
  readonly sortState = signal<Sort>({ active: 'createdAt', direction: 'desc' });
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);

  readonly filteredWorkflows = computed(() => {
    let data = [...this.facade.workflows()];
    const statuses = this.statusFilter();
    const envs = this.targetEnvFilter();

    if (statuses.length) {
      data = data.filter(w => statuses.includes(w.status ?? ''));
    }
    if (envs.length) {
      data = data.filter(w => envs.includes(w.targetEnvironment ?? ''));
    }

    // Sort
    const sort = this.sortState();
    if (sort.active && sort.direction) {
      data.sort((a: any, b: any) => {
        const dir = sort.direction === 'asc' ? 1 : -1;
        const key = sort.active === 'prompt' ? 'promptId' : sort.active;
        const aVal = a[key] ?? '';
        const bVal = b[key] ?? '';
        return aVal < bVal ? -dir : aVal > bVal ? dir : 0;
      });
    }
    return data;
  });

  readonly paginatedWorkflows = computed(() => {
    const all = this.filteredWorkflows();
    const start = this.pageIndex() * this.pageSize();
    return all.slice(start, start + this.pageSize());
  });

  readonly hasActiveFilters = computed(() => this.statusFilter().length > 0 || this.targetEnvFilter().length > 0);

  ngOnInit(): void {
    this.facade.loadWorkflows();
    this.promptsFacade.loadPrompts();
  }

  getPromptName(promptId: string): string {
    const prompt = this.promptsFacade.prompts().find(p => p.id === promptId);
    return prompt?.name ?? promptId;
  }

  copyId(id: string, event: Event): void {
    event.stopPropagation();
    navigator.clipboard.writeText(id).then(() => {
      this.snackBar.open('Prompt ID copied!', 'OK', { duration: 2000 });
    });
  }

  statusClass(status: string | undefined): string {
    switch (status) {
      case 'APPROVED': return 'badge-success';
      case 'REJECTED': return 'badge-danger';
      case 'PENDING': return 'badge-warning';
      case 'IN_REVIEW': return 'badge-info';
      default: return 'badge-neutral';
    }
  }

  onApprove(id: string): void {
    this.facade.approveWorkflow(id, { actor: 'admin', comment: 'Approved via UI' });
    this.snackBar.open('Workflow approved', 'OK', { duration: 3000 });
  }

  onReject(id: string): void {
    this.facade.rejectWorkflow(id, { actor: 'admin', comment: 'Rejected via UI' });
    this.snackBar.open('Workflow rejected', 'OK', { duration: 3000 });
  }

  onStatusFilterChange(values: string[]): void { this.statusFilter.set(values); this.pageIndex.set(0); }
  onEnvFilterChange(values: string[]): void { this.targetEnvFilter.set(values); this.pageIndex.set(0); }
  onSortChange(sort: Sort): void { this.sortState.set(sort); }
  clearFilters(): void { this.statusFilter.set([]); this.targetEnvFilter.set([]); this.pageIndex.set(0); }
  onPageChange(e: PageEvent): void { this.pageIndex.set(e.pageIndex); this.pageSize.set(e.pageSize); }
}
