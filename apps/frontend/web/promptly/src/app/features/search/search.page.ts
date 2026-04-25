import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SearchFacade } from '../../state/search/search.facade';

@Component({
  selector: 'promptly-search',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    FormsModule, RouterLink, DecimalPipe,
    MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule,
    MatButtonModule, MatListModule, MatProgressSpinnerModule,
  ],
  templateUrl: './search.page.html',
  styleUrl: './search.page.scss',
})
export class SearchPage {
  readonly facade = inject(SearchFacade);
  private readonly route = inject(ActivatedRoute);
  searchQuery = '';
  projectPrefix = '';

  constructor() {
    const pid = this.route.snapshot.paramMap.get('projectId');
    this.projectPrefix = pid ? `/projects/${pid}` : '';
  }

  onSearch(): void {
    this.facade.search(this.searchQuery);
  }
}
