import { createReducer, on } from '@ngrx/store';
import { initialProjectsState } from './projects.state';
import * as ProjectsActions from './projects.actions';

export const projectsReducer = createReducer(
  initialProjectsState,
  on(ProjectsActions.loadProjects, (state) => ({ ...state, loading: true, error: null })),
  on(ProjectsActions.loadProjectsSuccess, (state, { projects }) => ({
    ...state,
    loading: false,
    projects,
    // Auto-select first if none selected
    selectedProjectId: state.selectedProjectId ?? (projects.length > 0 ? projects[0].id! : null),
  })),
  on(ProjectsActions.loadProjectsFailure, (state, { error }) => ({ ...state, loading: false, error })),
  on(ProjectsActions.selectProject, (state, { projectId }) => ({ ...state, selectedProjectId: projectId })),
);
