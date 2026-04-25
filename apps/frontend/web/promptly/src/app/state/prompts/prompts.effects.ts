import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PromptsService } from '@promptly/client';
import * as PromptsActions from './prompts.actions';

@Injectable()
export class PromptsEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(PromptsService);
  private readonly snackBar = inject(MatSnackBar);

  loadPrompts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.loadPrompts),
      switchMap(({ projectId }) =>
        this.api.listPrompts({ projectId }).pipe(
          map(prompts => PromptsActions.loadPromptsSuccess({ prompts })),
          catchError(err => of(PromptsActions.loadPromptsFailure({ error: err?.error?.detail ?? 'Failed to load prompts' })))
        )
      )
    )
  );

  loadPrompt$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.loadPrompt),
      switchMap(({ id }) =>
        this.api.getPrompt({ id }).pipe(
          map(prompt => PromptsActions.loadPromptSuccess({ prompt })),
          catchError(err => of(PromptsActions.loadPromptFailure({ error: err?.error?.detail ?? 'Prompt not found' })))
        )
      )
    )
  );

  createPrompt$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.createPrompt),
      switchMap(({ request }) =>
        this.api.createPrompt({ createPromptRequest: request }).pipe(
          map(prompt => PromptsActions.createPromptSuccess({ prompt })),
          catchError(err => of(PromptsActions.createPromptFailure({ error: err?.error?.detail ?? 'Failed to create prompt' })))
        )
      )
    )
  );

  updatePrompt$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.updatePrompt),
      switchMap(({ id, request }) =>
        this.api.updatePrompt({ id, updatePromptRequest: request }).pipe(
          map(prompt => PromptsActions.updatePromptSuccess({ prompt })),
          catchError(err => of(PromptsActions.updatePromptFailure({ error: err?.error?.detail ?? 'Failed to update prompt' })))
        )
      )
    )
  );

  deletePrompt$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.deletePrompt),
      switchMap(({ id }) =>
        this.api.deletePrompt({ id }).pipe(
          map(() => PromptsActions.deletePromptSuccess({ id })),
          catchError(err => of(PromptsActions.deletePromptFailure({ error: err?.error?.detail ?? 'Failed to delete prompt' })))
        )
      )
    )
  );

  rollbackPrompt$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.rollbackPrompt),
      switchMap(({ id, targetVersion }) =>
        this.api.rollbackPrompt({ id, targetVersion }).pipe(
          map(prompt => PromptsActions.rollbackPromptSuccess({ prompt })),
          catchError(err => of(PromptsActions.rollbackPromptFailure({ error: err?.error?.detail ?? 'Failed to rollback' })))
        )
      )
    )
  );

  loadVersions$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PromptsActions.loadPromptSuccess),
      switchMap(({ prompt }) =>
        this.api.getVersionHistory({ id: prompt.id! }).pipe(
          map(versions => PromptsActions.loadVersionsSuccess({ versions })),
          catchError(() => of(PromptsActions.loadVersionsFailure({ error: 'Failed to load versions' })))
        )
      )
    )
  );

  showErrorSnackbar$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        PromptsActions.createPromptFailure,
        PromptsActions.updatePromptFailure,
        PromptsActions.deletePromptFailure,
        PromptsActions.rollbackPromptFailure,
      ),
      tap(({ error }) => {
        this.snackBar.open(error, 'Dismiss', { duration: 5000, panelClass: 'snackbar-error' });
      })
    ),
    { dispatch: false }
  );
}
