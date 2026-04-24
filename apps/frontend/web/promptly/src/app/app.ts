import { Component, inject, OnInit, Renderer2 } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthFacade } from './state/auth/auth.facade';
import { ProjectsFacade } from './state/projects/projects.facade';

@Component({
  selector: 'promptly-root',
  imports: [
    RouterOutlet, RouterLink, RouterLinkActive,
    MatListModule, MatIconModule, MatButtonModule,
    MatTooltipModule, MatMenuModule, MatDividerModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  readonly auth = inject(AuthFacade);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly renderer = inject(Renderer2);

  sidebarCollapsed = false;
  isDark = true;

  navItems = [
    { path: '/dashboard', icon: 'dashboard',       label: 'Dashboard' },
    { path: '/prompts',   icon: 'edit_note',        label: 'Prompts' },
    { path: '/workflows', icon: 'approval',         label: 'Workflows' },
    { path: '/scanner',   icon: 'security',         label: 'Scanner' },
    { path: '/search',    icon: 'search',           label: 'Search' },
    { path: '/audit',     icon: 'history',          label: 'Audit' },
    { path: '/settings',  icon: 'settings',         label: 'Settings' },
  ];

  ngOnInit(): void {
    this.auth.restoreSession();

    // Restore theme preference
    const saved = localStorage.getItem('promptly-theme');
    if (saved === 'light') {
      this.isDark = false;
      this.applyTheme('light');
    }
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
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
