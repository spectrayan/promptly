import { ProjectResponse } from '@promptly/client';

export interface ProjectsState {
  projects: ProjectResponse[];
  selectedProjectId: string | null;
  loading: boolean;
  error: string | null;
}

export const initialProjectsState: ProjectsState = {
  projects: [],
  selectedProjectId: null,
  loading: false,
  error: null,
};
