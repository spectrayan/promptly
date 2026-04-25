import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectAllProjects,
  selectSelectedProject,
  selectSelectedProjectId,
  selectProjectsLoading,
  selectProjectMembers,
  selectProjectMembersLoading,
} from './projects.selectors';
import * as ProjectsActions from './projects.actions';
import { AddMemberRequest, UpdateMemberRequest, CreateProjectRequest } from '@promptly/client';

@Injectable({ providedIn: 'root' })
export class ProjectsFacade {
  private readonly store = inject(Store);

  readonly projects       = this.store.selectSignal(selectAllProjects);
  readonly selectedProject = this.store.selectSignal(selectSelectedProject);
  readonly selectedId     = this.store.selectSignal(selectSelectedProjectId);
  readonly loading        = this.store.selectSignal(selectProjectsLoading);
  
  readonly projectMembers = this.store.selectSignal(selectProjectMembers);
  readonly membersLoading = this.store.selectSignal(selectProjectMembersLoading);

  loadProjects(): void { this.store.dispatch(ProjectsActions.loadProjects()); }
  selectProject(projectId: string): void {
    this.store.dispatch(ProjectsActions.selectProject({ projectId }));
  }

  createProject(request: CreateProjectRequest): void {
    this.store.dispatch(ProjectsActions.createProject({ request }));
  }

  loadProjectMembers(projectId: string): void {
    this.store.dispatch(ProjectsActions.loadProjectMembers({ projectId }));
  }

  addProjectMember(projectId: string, request: AddMemberRequest): void {
    this.store.dispatch(ProjectsActions.addProjectMember({ projectId, request }));
  }

  updateProjectMember(projectId: string, userId: string, request: UpdateMemberRequest): void {
    this.store.dispatch(ProjectsActions.updateProjectMember({ projectId, userId, request }));
  }

  removeProjectMember(projectId: string, userId: string): void {
    this.store.dispatch(ProjectsActions.removeProjectMember({ projectId, userId }));
  }
}
