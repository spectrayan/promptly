import { Component, inject, OnInit, OnDestroy, Renderer2, computed, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Subscription, filter } from 'rxjs';
import { AuthFacade } from './state/auth/auth.facade';
import { ProjectsFacade } from './state/projects/projects.facade';
import { ProjectCreationModalComponent } from './features/projects/components/project-creation-modal.component';

@Component({
  selector: 'promptly-root',
  imports: [
    RouterOutlet, RouterLink, RouterLinkActive,
    MatListModule, MatIconModule, MatButtonModule,
    MatTooltipModule, MatMenuModule, MatDividerModule, MatDialogModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit, OnDestroy {
  readonly auth = inject(AuthFacade);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly renderer = inject(Renderer2);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  private routerSub?: Subscription;

  sidebarCollapsed = false;
  isDark = true;
  projectSearch = signal('');

  /** Extract projectId from current URL (e.g. /projects/proj-001/prompts → proj-001) */
  activeProjectId = signal<string | null>(null);

  /** Projects filtered by search input */
  readonly filteredProjects = computed(() => {
    const term = this.projectSearch().toLowerCase().trim();
    if (!term) return this.projectsFacade.projects();
    return this.projectsFacade.projects().filter(
      p => p.name?.toLowerCase().includes(term) || p.description?.toLowerCase().includes(term)
    );
  });

  /** Dashboard link — project-scoped when a project is active */
  readonly dashboardNavItem = computed(() => {
    const pid = this.activeProjectId();
    return { path: pid ? `/projects/${pid}/dashboard` : '/dashboard', icon: 'dashboard', label: 'Dashboard' };
  });

  /** Feature nav items that require a selected project */
  private readonly projectNavDefs = [
    { segment: 'prompts',   icon: 'edit_note', label: 'Prompts' },
    { segment: 'workflows', icon: 'approval',  label: 'Workflows' },
    { segment: 'scanner',   icon: 'security',  label: 'Scanner' },
    { segment: 'audit',     icon: 'history',    label: 'Audit' },
  ];

  /** Bottom nav — project-scoped when a project is active */
  readonly bottomNavItems = computed(() => {
    const pid = this.activeProjectId();
    return [
      { path: pid ? `/projects/${pid}/search` : '/search', icon: 'search', label: 'Search' },
      { path: pid ? `/projects/${pid}/settings` : '/settings', icon: 'settings', label: 'Settings' },
    ];
  });

  /** Computed project-scoped nav items */
  readonly projectNavItems = computed(() => {
    const pid = this.activeProjectId();
    if (!pid) return [];
    return this.projectNavDefs.map(def => ({
      path: `/projects/${pid}/${def.segment}`,
      icon: def.icon,
      label: def.label,
    }));
  });

  ngOnInit(): void {
    this.auth.restoreSession();

    // Restore theme preference
    const saved = localStorage.getItem('promptly-theme');
    if (saved === 'light') {
      this.isDark = false;
      this.applyTheme('light');
    }

    // Track active projectId from URL
    this.syncProjectIdFromUrl(this.router.url);
    this.restoreProjectIfNeeded();
    this.routerSub = this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(e => this.syncProjectIdFromUrl(e.urlAfterRedirects));
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
  }

  /** Parse /projects/:projectId/... from the URL */
  private syncProjectIdFromUrl(url: string): void {
    const match = url.match(/\/projects\/([^/]+)/);
    const pid = match?.[1] ?? null;
    if (pid !== this.activeProjectId()) {
      this.activeProjectId.set(pid);
      if (pid) {
        this.projectsFacade.selectProject(pid);
        localStorage.setItem('promptly-last-project', pid);
      }
    }
  }

  /** On initial load, if URL has no project context, redirect to last-used project */
  private restoreProjectIfNeeded(): void {
    if (this.activeProjectId()) return; // URL already has a project

    const lastPid = localStorage.getItem('promptly-last-project');
    if (lastPid) {
      // Derive target segment from current URL path (e.g. /dashboard -> dashboard)
      const segment = this.router.url.replace(/^\//, '').split('?')[0] || 'dashboard';
      this.router.navigate(['/projects', lastPid, segment], { replaceUrl: true });
    }
  }

  /** When user picks a project from the dropdown, navigate to it */
  onProjectSelect(projectId: string): void {
    // Determine the current sub-page to preserve context
    const currentSegment = this.getCurrentSubSegment();
    this.router.navigate(['/projects', projectId, currentSegment]);
  }

  /** Extract the current feature segment (prompts, workflows, etc.) or default to 'prompts' */
  private getCurrentSubSegment(): string {
    if (this.activeProjectId()) {
      const match = this.router.url.match(/\/projects\/[^/]+\/(\w+)/);
      if (match?.[1]) return match[1];
    }
    return 'prompts'; // Default landing page within a project
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  openCreateProjectModal(): void {
    this.dialog.open(ProjectCreationModalComponent, {
      width: '400px',
      disableClose: true
    });
  }

  toggleTheme(): void {
    this.isDark = !this.isDark;
    const theme = this.isDark ? 'dark' : 'light';
    this.applyTheme(theme);
    localStorage.setItem('promptly-theme', theme);
  }

  getInitials(): string {
    const name = this.auth.displayName();
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }



  private applyTheme(theme: string): void {
    if (theme === 'light') {
      this.renderer.setAttribute(document.documentElement, 'data-theme', 'light');
    } else {
      this.renderer.removeAttribute(document.documentElement, 'data-theme');
    }
  }
}
