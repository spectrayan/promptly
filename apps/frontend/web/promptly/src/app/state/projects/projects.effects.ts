import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { ProjectsService } from '@promptly/client';
import * as ProjectsActions from './projects.actions';
import * as AuthActions from '../auth/auth.actions';

@Injectable()
export class ProjectsEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(ProjectsService);

  // Load projects on login success or session restore
  loadOnAuth$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginSuccess, AuthActions.registerSuccess, AuthActions.loadCurrentUserSuccess),
      map(() => ProjectsActions.loadProjects())
    )
  );

  loadProjects$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.loadProjects),
      switchMap(() =>
        this.api.listProjects().pipe(
          map(projects => ProjectsActions.loadProjectsSuccess({ projects })),
          catchError(err => of(ProjectsActions.loadProjectsFailure({
            error: err?.error?.detail ?? 'Failed to load projects'
          })))
        )
      )
    )
  );
}
