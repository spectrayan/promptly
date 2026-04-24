import { PromptResponse, PromptSummaryResponse, VersionResponse } from '@promptly/client';

export interface PromptsState {
  prompts: PromptSummaryResponse[];
  selectedPrompt: PromptResponse | null;
  versions: VersionResponse[];
  loading: boolean;
  saving: boolean;
  error: string | null;
}

export const initialPromptsState: PromptsState = {
  prompts: [],
  selectedPrompt: null,
  versions: [],
  loading: false,
  saving: false,
  error: null,
};
