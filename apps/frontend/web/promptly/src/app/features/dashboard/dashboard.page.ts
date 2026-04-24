import { Component, OnInit, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { DashboardFacade } from '../../state/dashboard/dashboard.facade';
import { AuthFacade } from '../../state/auth/auth.facade';
import { ProjectsFacade } from '../../state/projects/projects.facade';

@Component({
  selector: 'promptly-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatCardModule, MatIconModule, MatProgressSpinnerModule, RouterLink],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.scss',
})
export class DashboardPage implements OnInit {
  readonly facade = inject(DashboardFacade);
  readonly auth = inject(AuthFacade);
  readonly projectsFacade = inject(ProjectsFacade);

  ngOnInit(): void {
    this.facade.loadDashboard();
  }
}
