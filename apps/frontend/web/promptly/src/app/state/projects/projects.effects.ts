import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProjectsService } from '@promptly/client';
import { resolveErrorMessage, FallbackMessages } from '../../shared/constants/error-messages';
import * as ProjectsActions from './projects.actions';
import * as AuthActions from '../auth/auth.actions';

@Injectable()
export class ProjectsEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(ProjectsService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

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
            error: resolveErrorMessage(err?.error, FallbackMessages.LOAD_PROJECTS)
          })))
        )
      )
    )
  );

  createProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.createProject),
      switchMap(({ request }) =>
        this.api.createProject({ createProjectRequest: request }).pipe(
          map(project => ProjectsActions.createProjectSuccess({ project })),
          catchError(err => of(ProjectsActions.createProjectFailure({
            error: resolveErrorMessage(err?.error, FallbackMessages.CREATE_PROJECT)
          })))
        )
      )
    )
  );

  // Navigate to the newly created project's prompts page
  navigateOnCreate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.createProjectSuccess),
      tap(({ project }) => {
        this.router.navigate(['/projects', project.id, 'prompts']);
      })
    ),
    { dispatch: false }
  );

  loadProjectMembers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.loadProjectMembers),
      switchMap(({ projectId }) =>
        this.api.listProjectMembers({ projectId }).pipe(
          map(members => ProjectsActions.loadProjectMembersSuccess({ members })),
          catchError(err => of(ProjectsActions.loadProjectMembersFailure({
            error: resolveErrorMessage(err?.error, FallbackMessages.LOAD_MEMBERS)
          })))
        )
      )
    )
  );

  addProjectMember$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.addProjectMember),
      switchMap(({ projectId, request }) =>
        this.api.addProjectMember({ projectId, addMemberRequest: request }).pipe(
          map(member => ProjectsActions.addProjectMemberSuccess({ member })),
          catchError(err => of(ProjectsActions.addProjectMemberFailure({
            error: resolveErrorMessage(err?.error, FallbackMessages.ADD_MEMBER)
          })))
        )
      )
    )
  );

  updateProjectMember$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.updateProjectMember),
      switchMap(({ projectId, userId, request }) =>
        this.api.updateProjectMember({ projectId, userId, updateMemberRequest: request }).pipe(
          map(member => ProjectsActions.updateProjectMemberSuccess({ member })),
          catchError(err => of(ProjectsActions.updateProjectMemberFailure({
            error: resolveErrorMessage(err?.error, FallbackMessages.UPDATE_MEMBER)
          })))
        )
      )
    )
  );

  removeProjectMember$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectsActions.removeProjectMember),
      switchMap(({ projectId, userId }) =>
        this.api.removeProjectMember({ projectId, userId }).pipe(
          map(() => ProjectsActions.removeProjectMemberSuccess({ userId })),
          catchError(err => of(ProjectsActions.removeProjectMemberFailure({
            error: resolveErrorMessage(err?.error, FallbackMessages.REMOVE_MEMBER)
          })))
        )
      )
    )
  );

  showErrorSnackbar$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ProjectsActions.createProjectFailure,
        ProjectsActions.addProjectMemberFailure,
        ProjectsActions.updateProjectMemberFailure,
        ProjectsActions.removeProjectMemberFailure,
      ),
      tap(({ error }) => {
        this.snackBar.open(error, 'Dismiss', { duration: 5000, panelClass: 'snackbar-error' });
      })
    ),
    { dispatch: false }
  );
}
