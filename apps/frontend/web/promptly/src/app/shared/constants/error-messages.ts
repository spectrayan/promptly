import { ProblemDetails } from '@promptly/client';
import { ErrorCode, ErrorCodeType } from './error-codes';

/**
 * User-friendly error messages keyed by the backend's machine-readable error code.
 *
 * The UI owns these strings — they can be freely reworded, translated, or
 * enriched without any backend coordination.
 *
 * Workflow:
 *  1. Backend sends ProblemDetail with `code` extension property
 *  2. Frontend looks up the code in this map
 *  3. Falls back to the backend's `detail` field if the code is unknown
 */
const codeMessages: Record<string, string> = {
  // ── Prompt ───────────────────────────────────────────────────────
  [ErrorCode.PROMPT_DUPLICATE_NAME]:
    'A prompt with this name already exists in the project. Please choose a different name.',
  [ErrorCode.PROMPT_NOT_FOUND]:
    'The prompt you are looking for could not be found. It may have been deleted.',
  [ErrorCode.PROMPT_NOT_EDITABLE]:
    'This prompt cannot be edited in its current state.',
  [ErrorCode.PROMPT_NOT_SUBMITTABLE]:
    'This prompt is not eligible for review submission right now.',

  // ── Project ──────────────────────────────────────────────────────
  [ErrorCode.PROJECT_DUPLICATE_NAME]:
    'A project with this name already exists. Please choose a different name.',
  [ErrorCode.PROJECT_NOT_FOUND]:
    'The project could not be found.',
  [ErrorCode.PROJECT_MEMBER_EXISTS]:
    'This user is already a member of the project.',
  [ErrorCode.PROJECT_MEMBER_NOT_FOUND]:
    'The project member could not be found.',

  // ── Workflow ─────────────────────────────────────────────────────
  [ErrorCode.WORKFLOW_NOT_FOUND]:
    'The workflow could not be found.',
  [ErrorCode.WORKFLOW_ALREADY_COMPLETE]:
    'This workflow has already been completed and cannot be modified.',

  // ── Auth ─────────────────────────────────────────────────────────
  [ErrorCode.AUTH_INVALID_CREDENTIALS]:
    'Invalid email or password. Please try again.',
  [ErrorCode.AUTH_USER_EXISTS]:
    'An account with this email already exists.',

  // ── Generic ──────────────────────────────────────────────────────
  [ErrorCode.DUPLICATE_RESOURCE]:
    'A resource with the same identifier already exists.',
  [ErrorCode.RESOURCE_NOT_FOUND]:
    'The requested resource could not be found.',
  [ErrorCode.BAD_REQUEST]:
    'The request was invalid. Please check your input and try again.',
  [ErrorCode.BUSINESS_RULE_VIOLATION]:
    'This action is not allowed in the current state.',
  [ErrorCode.INTERNAL_ERROR]:
    'Something went wrong. Please try again later.',
};

/**
 * Resolves a user-friendly error message from a backend ProblemDetails response.
 *
 * Priority:
 *  1. Look up `code` in the code → message map
 *  2. Fall back to the backend's `detail` field
 *  3. Fall back to the provided default message
 *
 * @param error  The HTTP error response body (ProblemDetails from SDK, or any)
 * @param fallback  A default fallback message if nothing matches
 */
export function resolveErrorMessage(error: ProblemDetails | null | undefined, fallback: string): string;
export function resolveErrorMessage(error: any, fallback: string): string;
export function resolveErrorMessage(error: any, fallback: string): string {
  const code = error?.code as string | undefined;
  if (code && codeMessages[code]) {
    return codeMessages[code];
  }
  return error?.detail ?? fallback;
}

/**
 * Fallback messages used when the backend response is completely unavailable
 * (e.g. network error). These are NOT keyed by error code.
 */
export const FallbackMessages = {
  // ── Prompts ──────────────────────────────────────────────────────
  LOAD_PROMPTS: 'Failed to load prompts',
  LOAD_PROMPT: 'Prompt not found',
  CREATE_PROMPT: 'Failed to create prompt',
  UPDATE_PROMPT: 'Failed to update prompt',
  DELETE_PROMPT: 'Failed to delete prompt',
  ROLLBACK_PROMPT: 'Failed to rollback prompt',
  LOAD_VERSIONS: 'Failed to load versions',

  // ── Projects ─────────────────────────────────────────────────────
  LOAD_PROJECTS: 'Failed to load projects',
  CREATE_PROJECT: 'Failed to create project',
  LOAD_MEMBERS: 'Failed to load project members',
  ADD_MEMBER: 'Failed to add project member',
  UPDATE_MEMBER: 'Failed to update project member',
  REMOVE_MEMBER: 'Failed to remove project member',

  // ── Workflows ────────────────────────────────────────────────────
  LOAD_WORKFLOWS: 'Failed to load workflows',
  SUBMIT_REVIEW: 'Failed to submit for review',
  APPROVE_WORKFLOW: 'Failed to approve workflow',
  REJECT_WORKFLOW: 'Failed to reject workflow',

  // ── Dashboard ────────────────────────────────────────────────────
  LOAD_DASHBOARD: 'Failed to load dashboard',

  // ── Scanner ──────────────────────────────────────────────────────
  LOAD_SCANS: 'Failed to load scans',

  // ── Audit ────────────────────────────────────────────────────────
  LOAD_AUDIT: 'Failed to load audit logs',

  // ── Improver ─────────────────────────────────────────────────────
  APPLY_IMPROVEMENT: 'Failed to apply improvement',
} as const;
