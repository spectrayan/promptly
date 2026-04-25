import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
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
export class DashboardPage implements OnInit, OnDestroy {
  readonly facade = inject(DashboardFacade);
  readonly auth = inject(AuthFacade);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly route = inject(ActivatedRoute);
  private paramSub?: Subscription;

  /** Prefix for project-scoped routes, e.g. '/projects/abc123' */
  projectPrefix = '';

  ngOnInit(): void {
    this.paramSub = this.route.paramMap.subscribe(params => {
      const pid = params.get('projectId');
      this.projectPrefix = pid ? `/projects/${pid}` : '';
      this.facade.loadDashboard(pid ?? undefined);
    });
  }

  ngOnDestroy(): void {
    this.paramSub?.unsubscribe();
  }
}
