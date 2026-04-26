import { ChangeDetectionStrategy, Component, inject, OnInit, Renderer2, computed, signal, effect, HostListener, DestroyRef } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { AuthFacade } from './state/auth/auth.facade';
import { ProjectsFacade } from './state/projects/projects.facade';
import { NotificationsFacade } from './state/notifications/notifications.facade';
import { ProjectCreationModalComponent } from './features/projects/components/project-creation-modal.component';
import { NotificationBellComponent } from './shared/components/notification-bell/notification-bell.component';
import { HelpDocsService } from './core/services/help-docs.service';
import { HelpDocsPanelComponent } from './shared/components/help-docs-panel/help-docs-panel.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-root',
  imports: [
    RouterOutlet, RouterLink, RouterLinkActive,
    MatListModule, MatIconModule, MatButtonModule,
    MatTooltipModule, MatMenuModule, MatDividerModule, MatDialogModule,
    NotificationBellComponent, HelpDocsPanelComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  readonly auth = inject(AuthFacade);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly renderer = inject(Renderer2);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly notificationsFacade = inject(NotificationsFacade);
  private readonly destroyRef = inject(DestroyRef);
  readonly helpDocs = inject(HelpDocsService);

  private projectAutoSelected = false; // Guard to prevent repeated auto-selection

  sidebarCollapsed = window.innerWidth <= 768;
  isDark = true;
  projectSearch = signal('');
  helpDocsWidth = signal<number>(480);
  private isResizing = false;

  /** Extract projectId from current URL (e.g. /projects/proj-001/prompts → proj-001) */
  activeProjectId = signal<string | null>(null);

  /** Auto-select project when projects load async and no project is active */
  private readonly _autoSelectEffect = effect(() => {
    const projects = this.projectsFacade.projects();
    const active = this.activeProjectId();
    const authed = this.auth.isAuthenticated();

    if (authed && !active && projects.length > 0 && !this.projectAutoSelected) {
      this.projectAutoSelected = true;
      queueMicrotask(() => this.restoreProjectIfNeeded());
    }
    if (active) {
      this.projectAutoSelected = true;
    }
  });

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
    { segment: 'settings',  icon: 'tune',      label: 'Project Settings' },
  ];

  /** Bottom nav — always-visible global items */
  readonly bottomNavItems = computed(() => {
    const pid = this.activeProjectId();
    return [
      { path: pid ? `/projects/${pid}/search` : '/search', icon: 'search', label: 'Search' },
      { path: '/settings', icon: 'admin_panel_settings', label: 'Platform Settings' },
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
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(e => {
      this.syncProjectIdFromUrl(e.urlAfterRedirects);
      // Re-check after every navigation (e.g. post-login redirect to /dashboard)
      this.restoreProjectIfNeeded();
      // Auto-close sidebar on mobile after navigation
      if (window.innerWidth <= 768) {
        this.sidebarCollapsed = true;
      }
    });
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
        // Connect SSE for this project
        this.notificationsFacade.connectSse(pid);
      } else {
        this.notificationsFacade.disconnectSse();
      }
    }
  }

  /**
   * On initial load or after login, if URL has no project context,
   * redirect to the last-used project (from localStorage) or auto-select
   * the first available project from the loaded list.
   */
  private restoreProjectIfNeeded(): void {
    if (this.activeProjectId()) return; // URL already has a project

    // Only restore when user is authenticated and on a non-login page
    // Skip for global routes that don't require project context
    const globalRoutes = ['/login', '/register', '/profile', '/settings'];
    if (!this.auth.isAuthenticated() || globalRoutes.some(r => this.router.url.startsWith(r))) return;

    const lastPid = localStorage.getItem('promptly-last-project');
    const projects = this.projectsFacade.projects();

    let targetPid: string | null = null;

    if (lastPid && projects.some(p => p.id === lastPid)) {
      // Saved project still exists in the user's project list
      targetPid = lastPid;
    } else if (lastPid && projects.length === 0) {
      // Projects haven't loaded yet — optimistically navigate with the saved ID
      targetPid = lastPid;
    } else if (projects.length > 0) {
      // No saved project (or it no longer exists) — auto-select first
      targetPid = projects[0].id;
    }

    if (targetPid) {
      const segment = this.router.url.replace(/^\//, '').split('?')[0] || 'dashboard';
      // Avoid re-navigating to login or empty segments
      const validSegment = ['login', ''].includes(segment) ? 'dashboard' : segment;
      this.router.navigate(['/projects', targetPid, validSegment], { replaceUrl: true });
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

  @HostListener('window:resize')
  onResize(): void {
    // Auto-collapse sidebar when resizing to mobile
    if (window.innerWidth <= 768 && !this.sidebarCollapsed) {
      this.sidebarCollapsed = true;
    }
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

  openHelpDocs(): void {
    this.helpDocs.toggle();
  }

  onResizeStart(event: MouseEvent): void {
    this.isResizing = true;
    event.preventDefault(); // Prevent text selection
    this.renderer.setStyle(document.body, 'cursor', 'ew-resize');
    this.renderer.addClass(document.body, 'is-resizing');
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent): void {
    if (!this.isResizing) return;
    const newWidth = window.innerWidth - event.clientX;
    if (newWidth > 300 && newWidth < 800) {
      this.helpDocsWidth.set(newWidth);
    }
  }

  @HostListener('document:mouseup')
  onMouseUp(): void {
    if (this.isResizing) {
      this.isResizing = false;
      this.renderer.removeStyle(document.body, 'cursor');
      this.renderer.removeClass(document.body, 'is-resizing');
    }
  }



  private applyTheme(theme: string): void {
    if (theme === 'light') {
      this.renderer.setAttribute(document.documentElement, 'data-theme', 'light');
    } else {
      this.renderer.removeAttribute(document.documentElement, 'data-theme');
    }
  }
}
