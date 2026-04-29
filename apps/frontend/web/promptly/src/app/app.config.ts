import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { provideApi } from '@promptly/client';
import { provideMockApi, mockApiInterceptor } from '@promptly/mock-assets';
import { provideSseClient } from '@spectrayan/ng-sse-client';

import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { authReducer } from './state/auth/auth.reducer';
import { promptsReducer } from './state/prompts/prompts.reducer';
import { projectsReducer } from './state/projects/projects.reducer';
import { workflowsReducer } from './state/workflows/workflows.reducer';
import { dashboardReducer } from './state/dashboard/dashboard.reducer';
import { notificationsReducer } from './state/notifications/notifications.reducer';
import { AuthEffects } from './state/auth/auth.effects';
import { ProjectsEffects } from './state/projects/projects.effects';
import { PromptsEffects } from './state/prompts/prompts.effects';
import { WorkflowsEffects } from './state/workflows/workflows.effects';
import { DashboardEffects } from './state/dashboard/dashboard.effects';
import { NotificationsEffects } from './state/notifications/notifications.effects';

import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([mockApiInterceptor, jwtInterceptor])),
    provideAnimationsAsync(),

    // Mock API — enabled via environment flag
    provideMockApi({ enabled: environment.useMockApi, delay: 300 }),

    // NgRx Store
    provideStore({
      auth: authReducer,
      projects: projectsReducer,
      prompts: promptsReducer,
      workflows: workflowsReducer,
      dashboard: dashboardReducer,
      notifications: notificationsReducer,
    }),
    provideEffects(AuthEffects, ProjectsEffects, PromptsEffects, WorkflowsEffects, DashboardEffects, NotificationsEffects),
    provideStoreDevtools({ maxAge: 25, logOnly: false }),

    // Generated API SDK — base path to the backend
    provideApi({ basePath: environment.apiBasePath }),

    // Spectrayan SSE Client
    provideSseClient(),

    // Snackbar — bottom-center positioning
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { horizontalPosition: 'center', verticalPosition: 'bottom', duration: 4000 } },
  ],
};
