import { createAction, props } from '@ngrx/store';
import { ProjectResponse } from '@promptly/client';

export const loadProjects = createAction('[Projects] Load');
export const loadProjectsSuccess = createAction('[Projects] Load Success', props<{ projects: ProjectResponse[] }>());
export const loadProjectsFailure = createAction('[Projects] Load Failure', props<{ error: string }>());
export const selectProject = createAction('[Projects] Select', props<{ projectId: string }>());
