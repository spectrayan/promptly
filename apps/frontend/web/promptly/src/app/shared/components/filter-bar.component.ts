import {
  Component, Input, Output, EventEmitter,
  ChangeDetectionStrategy,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';

export interface FilterOption {
  label: string;
  value: string;
}

export interface FilterConfig {
  key: string;
  label: string;
  type: 'select' | 'text';
  options?: FilterOption[];   // for 'select' type
  placeholder?: string;      // for 'text' type
}

export interface FilterValues {
  [key: string]: string;
}

@Component({
  selector: 'promptly-filter-bar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatIconModule, MatButtonModule, MatChipsModule,
  ],
  template: `
    <div class="filter-bar">
      <!-- Search field -->
      <mat-form-field appearance="outline" class="filter-search">
        <mat-icon matPrefix>search</mat-icon>
        <input matInput
               [placeholder]="searchPlaceholder"
               [(ngModel)]="searchValue"
               (ngModelChange)="onSearchChange($event)" />
        @if (searchValue) {
          <button mat-icon-button matSuffix (click)="clearSearch()">
            <mat-icon>close</mat-icon>
          </button>
        }
      </mat-form-field>

      <!-- Filter dropdowns -->
      @for (filter of filters; track filter.key) {
        @if (filter.type === 'select' && filter.options) {
          <mat-form-field appearance="outline" class="filter-select">
            <mat-label>{{ filter.label }}</mat-label>
            <mat-select [(ngModel)]="values[filter.key]"
                        (ngModelChange)="onFilterChange()">
              <mat-option value="">All</mat-option>
              @for (opt of filter.options; track opt.value) {
                <mat-option [value]="opt.value">{{ opt.label }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
        }
      }

      <!-- Active filter count -->
      @if (activeFilterCount > 0) {
        <button mat-stroked-button class="clear-filters-btn" (click)="clearAll()">
          <mat-icon>filter_list_off</mat-icon>
          Clear {{ activeFilterCount }} filter{{ activeFilterCount > 1 ? 's' : '' }}
        </button>
      }
    </div>
  `,
  styles: [`
    .filter-bar {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      margin-bottom: var(--space-4);
      flex-wrap: wrap;
    }

    .filter-search {
      flex: 1 1 280px;
      min-width: 200px;
    }

    .filter-select {
      flex: 0 0 160px;
      min-width: 130px;
    }

    .clear-filters-btn {
      height: 40px;
      flex-shrink: 0;
      font-size: var(--text-sm);
      border-color: var(--mat-sys-outline-variant) !important;
    }

    :host ::ng-deep .mat-mdc-form-field-subscript-wrapper {
      display: none;
    }
  `],
})
export class FilterBarComponent {
  @Input() searchPlaceholder = 'Search...';
  @Input() filters: FilterConfig[] = [];
  @Output() searchChange = new EventEmitter<string>();
  @Output() filtersChange = new EventEmitter<FilterValues>();

  searchValue = '';
  values: FilterValues = {};

  get activeFilterCount(): number {
    let count = this.searchValue ? 1 : 0;
    for (const key of Object.keys(this.values)) {
      if (this.values[key]) count++;
    }
    return count;
  }

  onSearchChange(value: string): void {
    this.searchChange.emit(value);
    this.filtersChange.emit({ ...this.values, _search: value });
  }

  onFilterChange(): void {
    this.filtersChange.emit({ ...this.values, _search: this.searchValue });
  }

  clearSearch(): void {
    this.searchValue = '';
    this.searchChange.emit('');
    this.filtersChange.emit({ ...this.values, _search: '' });
  }

  clearAll(): void {
    this.searchValue = '';
    this.values = {};
    this.searchChange.emit('');
    this.filtersChange.emit({});
  }
}
