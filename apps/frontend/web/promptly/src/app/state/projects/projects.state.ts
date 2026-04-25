import { ProjectResponse, ProjectMemberResponse } from '@promptly/client';

export interface ProjectsState {
  projects: ProjectResponse[];
  selectedProjectId: string | null;
  projectMembers: ProjectMemberResponse[];
  membersLoading: boolean;
  membersError: string | null;
  loading: boolean;
  error: string | null;
}

export const initialProjectsState: ProjectsState = {
  projects: [],
  selectedProjectId: null,
  projectMembers: [],
  membersLoading: false,
  membersError: null,
  loading: false,
  error: null,
};
