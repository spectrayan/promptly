/** Pure reducer for the Project & RBAC feature slice. Handles project and member state transitions. */
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

  // Create Project
  on(ProjectsActions.createProject, (state) => ({ ...state, loading: true, error: null })),
  on(ProjectsActions.createProjectSuccess, (state, { project }) => ({
    ...state,
    loading: false,
    projects: [...state.projects, project],
    selectedProjectId: project.id!
  })),
  on(ProjectsActions.createProjectFailure, (state, { error }) => ({ ...state, loading: false, error })),

  // Project Members
  on(ProjectsActions.loadProjectMembers, (state) => ({ ...state, membersLoading: true, membersError: null })),
  on(ProjectsActions.loadProjectMembersSuccess, (state, { members }) => ({
    ...state,
    membersLoading: false,
    projectMembers: members
  })),
  on(ProjectsActions.loadProjectMembersFailure, (state, { error }) => ({ ...state, membersLoading: false, membersError: error })),

  on(ProjectsActions.addProjectMember, (state) => ({ ...state, membersLoading: true, membersError: null })),
  on(ProjectsActions.addProjectMemberSuccess, (state, { member }) => ({
    ...state,
    membersLoading: false,
    projectMembers: [...state.projectMembers, member]
  })),
  on(ProjectsActions.addProjectMemberFailure, (state, { error }) => ({ ...state, membersLoading: false, membersError: error })),

  on(ProjectsActions.updateProjectMember, (state) => ({ ...state, membersLoading: true, membersError: null })),
  on(ProjectsActions.updateProjectMemberSuccess, (state, { member }) => ({
    ...state,
    membersLoading: false,
    projectMembers: state.projectMembers.map(m => m.userId === member.userId ? member : m)
  })),
  on(ProjectsActions.updateProjectMemberFailure, (state, { error }) => ({ ...state, membersLoading: false, membersError: error })),

  on(ProjectsActions.removeProjectMember, (state) => ({ ...state, membersLoading: true, membersError: null })),
  on(ProjectsActions.removeProjectMemberSuccess, (state, { userId }) => ({
    ...state,
    membersLoading: false,
    projectMembers: state.projectMembers.filter(m => m.userId !== userId)
  })),
  on(ProjectsActions.removeProjectMemberFailure, (state, { error }) => ({ ...state, membersLoading: false, membersError: error }))
);
