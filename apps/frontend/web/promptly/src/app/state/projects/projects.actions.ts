/** NgRx actions for the Project & RBAC module — load, create, update, delete projects and members. */
import { createAction, props } from '@ngrx/store';
import { 
  ProjectResponse, 
  CreateProjectRequest, 
  ProjectMemberResponse, 
  AddMemberRequest, 
  UpdateMemberRequest 
} from '@promptly/client';

export const loadProjects = createAction('[Projects] Load');
export const loadProjectsSuccess = createAction('[Projects] Load Success', props<{ projects: ProjectResponse[] }>());
export const loadProjectsFailure = createAction('[Projects] Load Failure', props<{ error: string }>());

export const selectProject = createAction('[Projects] Select', props<{ projectId: string }>());

export const createProject = createAction('[Projects] Create Project', props<{ request: CreateProjectRequest }>());
export const createProjectSuccess = createAction('[Projects] Create Project Success', props<{ project: ProjectResponse }>());
export const createProjectFailure = createAction('[Projects] Create Project Failure', props<{ error: string }>());

export const loadProjectMembers = createAction('[Projects] Load Members', props<{ projectId: string }>());
export const loadProjectMembersSuccess = createAction('[Projects] Load Members Success', props<{ members: ProjectMemberResponse[] }>());
export const loadProjectMembersFailure = createAction('[Projects] Load Members Failure', props<{ error: string }>());

export const addProjectMember = createAction('[Projects] Add Member', props<{ projectId: string, request: AddMemberRequest }>());
export const addProjectMemberSuccess = createAction('[Projects] Add Member Success', props<{ member: ProjectMemberResponse }>());
export const addProjectMemberFailure = createAction('[Projects] Add Member Failure', props<{ error: string }>());

export const updateProjectMember = createAction('[Projects] Update Member', props<{ projectId: string, userId: string, request: UpdateMemberRequest }>());
export const updateProjectMemberSuccess = createAction('[Projects] Update Member Success', props<{ member: ProjectMemberResponse }>());
export const updateProjectMemberFailure = createAction('[Projects] Update Member Failure', props<{ error: string }>());

export const removeProjectMember = createAction('[Projects] Remove Member', props<{ projectId: string, userId: string }>());
export const removeProjectMemberSuccess = createAction('[Projects] Remove Member Success', props<{ userId: string }>());
export const removeProjectMemberFailure = createAction('[Projects] Remove Member Failure', props<{ error: string }>());
