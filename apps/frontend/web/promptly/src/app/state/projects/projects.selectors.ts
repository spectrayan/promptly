import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ProjectsState } from './projects.state';

export const selectProjectsState = createFeatureSelector<ProjectsState>('projects');
export const selectAllProjects = createSelector(selectProjectsState, s => s.projects);
export const selectSelectedProjectId = createSelector(selectProjectsState, s => s.selectedProjectId);
export const selectProjectsLoading = createSelector(selectProjectsState, s => s.loading);
export const selectSelectedProject = createSelector(
  selectAllProjects, selectSelectedProjectId,
  (projects, id) => projects.find(p => p.id === id) ?? null
);
