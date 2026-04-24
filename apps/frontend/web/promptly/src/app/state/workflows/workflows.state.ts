import { WorkflowResponse } from '@promptly/client';

export interface WorkflowsState {
  workflows: WorkflowResponse[];
  selectedWorkflow: WorkflowResponse | null;
  loading: boolean;
  error: string | null;
}

export const initialWorkflowsState: WorkflowsState = {
  workflows: [],
  selectedWorkflow: null,
  loading: false,
  error: null,
};
