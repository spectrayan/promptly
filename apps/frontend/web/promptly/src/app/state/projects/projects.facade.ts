import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectAllProjects,
  selectSelectedProject,
  selectSelectedProjectId,
  selectProjectsLoading,
} from './projects.selectors';
import * as ProjectsActions from './projects.actions';

@Injectable({ providedIn: 'root' })
export class ProjectsFacade {
  private readonly store = inject(Store);

  readonly projects       = this.store.selectSignal(selectAllProjects);
  readonly selectedProject = this.store.selectSignal(selectSelectedProject);
  readonly selectedId     = this.store.selectSignal(selectSelectedProjectId);
  readonly loading        = this.store.selectSignal(selectProjectsLoading);

  loadProjects(): void { this.store.dispatch(ProjectsActions.loadProjects()); }
  selectProject(projectId: string): void {
    this.store.dispatch(ProjectsActions.selectProject({ projectId }));
  }
}
