import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // ── Public routes (no auth required) ──
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.page').then(m => m.LoginPage),
    title: 'Sign In — Promptly',
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register.page').then(m => m.RegisterPage),
    title: 'Create Account — Promptly',
  },

  // ── Protected routes (auth guard) ──
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.page').then(m => m.DashboardPage),
    title: 'Dashboard — Promptly',
    canActivate: [authGuard],
  },

  // ── Project-scoped routes ──
  {
    path: 'projects/:projectId/dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.page').then(m => m.DashboardPage),
    title: 'Dashboard — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/prompts',
    loadComponent: () => import('./features/prompts/prompt-list/prompt-list.page').then(m => m.PromptListPage),
    title: 'Prompts — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/prompts/create',
    loadComponent: () => import('./features/prompts/prompt-create/prompt-create.page').then(m => m.PromptCreatePage),
    title: 'Create Prompt — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/prompts/:id',
    loadComponent: () => import('./features/prompts/prompt-detail/prompt-detail.page').then(m => m.PromptDetailPage),
    title: 'Prompt Detail — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/workflows',
    loadComponent: () => import('./features/workflows/workflow-list.page').then(m => m.WorkflowListPage),
    title: 'Workflows — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/scanner',
    loadComponent: () => import('./features/scanner/scan-results.page').then(m => m.ScanResultsPage),
    title: 'Security Scans — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/audit',
    loadComponent: () => import('./features/audit/audit-log.page').then(m => m.AuditLogPage),
    title: 'Audit Logs — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/search',
    loadComponent: () => import('./features/search/search.page').then(m => m.SearchPage),
    title: 'Search — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'projects/:projectId/settings',
    loadComponent: () => import('./features/projects/project-settings.page').then(m => m.ProjectSettingsPage),
    title: 'Project Settings — Promptly',
    canActivate: [authGuard],
  },

  // ── Global routes ──
  {
    path: 'search',
    loadComponent: () => import('./features/search/search.page').then(m => m.SearchPage),
    title: 'Search — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'settings',
    loadComponent: () => import('./features/settings/settings.page').then(m => m.SettingsPage),
    title: 'Settings — Promptly',
    canActivate: [authGuard],
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/user-profile/user-profile.page').then(m => m.UserProfilePage),
    title: 'User Profile — Promptly',
    canActivate: [authGuard],
  },

  // ── Legacy redirects (backward compat) ──
  { path: 'prompts', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'workflows', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'scanner', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'audit', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'projects/settings', redirectTo: 'dashboard', pathMatch: 'full' },

  // ── Catch-all 404 ──
  {
    path: '**',
    loadComponent: () => import('./features/not-found/not-found.page').then(m => m.NotFoundPage),
    title: 'Not Found — Promptly',
  },
];

