/**
 * Centralized error messages for API fallback responses.
 * The primary message always comes from the backend's RFC 7807 `detail` field;
 * these are only used as fallbacks when the server response is unavailable.
 */
export const ErrorMessages = {
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
