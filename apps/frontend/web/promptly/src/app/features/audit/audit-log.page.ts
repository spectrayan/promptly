import { Component, OnInit, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe, SlicePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AuditFacade } from '../../state/audit/audit.facade';

@Component({
  selector: 'promptly-audit-log',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe, SlicePipe, FormsModule,
    MatCardModule, MatTableModule, MatIconModule, MatChipsModule,
    MatProgressSpinnerModule, MatPaginatorModule, MatSortModule,
    MatFormFieldModule, MatSelectModule, MatInputModule, MatButtonModule,
    MatDatepickerModule, MatNativeDateModule,
  ],
  templateUrl: './audit-log.page.html',
  styleUrl: './audit-log.page.scss',
})
export class AuditLogPage implements OnInit {
  readonly facade = inject(AuditFacade);
  private readonly route = inject(ActivatedRoute);
  displayedColumns = ['action', 'resourceType', 'resourceId', 'actorUserId', 'timestamp'];

  // ── Filter signals ────────────────────────────────────────────
  readonly actionFilter = signal<string[]>([]);
  readonly resourceFilter = signal<string[]>([]);
  readonly actorFilter = signal<string[]>([]);
  readonly dateFromValue = signal<Date | null>(null);
  readonly dateToValue = signal<Date | null>(null);

  readonly sortState = signal<Sort>({ active: 'timestamp', direction: 'desc' });
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);

  // ── Dynamic filter options (derived from data) ────────────────
  readonly availableActions = computed(() => {
    const set = new Set(this.facade.logs().map(l => l.action).filter(Boolean));
    return Array.from(set).sort() as string[];
  });

  readonly availableResources = computed(() => {
    const set = new Set(this.facade.logs().map(l => l.resourceType).filter(Boolean));
    return Array.from(set).sort() as string[];
  });

  readonly availableActors = computed(() => {
    const set = new Set(this.facade.logs().map(l => l.actorUserId).filter(Boolean));
    return Array.from(set).sort() as string[];
  });

  // ── Filtered + sorted data ───────────────────────────────────
  readonly filteredLogs = computed(() => {
    let data = [...this.facade.logs()];
    const actions = this.actionFilter();
    const resources = this.resourceFilter();
    const actors = this.actorFilter();
    const from = this.dateFromValue();
    const to = this.dateToValue();

    if (actions.length) {
      data = data.filter(l => actions.some(a => l.action?.includes(a)));
    }
    if (resources.length) {
      data = data.filter(l => resources.includes(l.resourceType ?? ''));
    }
    if (actors.length) {
      data = data.filter(l => actors.includes(l.actorUserId ?? ''));
    }
    if (from) {
      data = data.filter(l => l.timestamp && new Date(l.timestamp) >= from);
    }
    if (to) {
      const end = new Date(to);
      end.setHours(23, 59, 59, 999);
      data = data.filter(l => l.timestamp && new Date(l.timestamp) <= end);
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

  readonly paginatedLogs = computed(() => {
    const all = this.filteredLogs();
    const start = this.pageIndex() * this.pageSize();
    return all.slice(start, start + this.pageSize());
  });

  readonly hasActiveFilters = computed(() =>
    this.actionFilter().length > 0 ||
    this.resourceFilter().length > 0 ||
    this.actorFilter().length > 0 ||
    !!this.dateFromValue() ||
    !!this.dateToValue()
  );

  ngOnInit(): void {
    const projectId = this.route.parent?.snapshot.paramMap.get('projectId')
      ?? this.route.snapshot.paramMap.get('projectId')
      ?? undefined;
    this.facade.loadLogs(projectId);
  }

  actionClass(action: string | undefined): string {
    if (!action) return 'badge-neutral';
    if (action.includes('created')) return 'badge-success';
    if (action.includes('updated')) return 'badge-info';
    if (action.includes('deleted')) return 'badge-danger';
    if (action.includes('approved')) return 'badge-success';
    if (action.includes('rejected')) return 'badge-danger';
    return 'badge-neutral';
  }

  onActionFilterChange(values: string[]): void { this.actionFilter.set(values); this.pageIndex.set(0); }
  onResourceFilterChange(values: string[]): void { this.resourceFilter.set(values); this.pageIndex.set(0); }
  onActorFilterChange(values: string[]): void { this.actorFilter.set(values); this.pageIndex.set(0); }
  onDateFromChange(value: Date | null): void { this.dateFromValue.set(value); this.pageIndex.set(0); }
  onDateToChange(value: Date | null): void { this.dateToValue.set(value); this.pageIndex.set(0); }
  onSortChange(sort: Sort): void { this.sortState.set(sort); }
  onPageChange(e: PageEvent): void { this.pageIndex.set(e.pageIndex); this.pageSize.set(e.pageSize); }

  clearFilters(): void {
    this.actionFilter.set([]);
    this.resourceFilter.set([]);
    this.actorFilter.set([]);
    this.dateFromValue.set(null);
    this.dateToValue.set(null);
    this.pageIndex.set(0);
  }
}
