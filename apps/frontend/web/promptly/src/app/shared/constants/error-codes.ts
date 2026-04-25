/**
 * Machine-readable error codes received from the backend's RFC 9457
 * ProblemDetail {@code code} extension property.
 *
 * Must stay in sync with {@code ErrorCode.java} on the backend.
 */
export const ErrorCode = {
  // ── Prompt ───────────────────────────────────────────────────────
  PROMPT_DUPLICATE_NAME:    'PROMPT_DUPLICATE_NAME',
  PROMPT_NOT_FOUND:         'PROMPT_NOT_FOUND',
  PROMPT_NOT_EDITABLE:      'PROMPT_NOT_EDITABLE',
  PROMPT_NOT_SUBMITTABLE:   'PROMPT_NOT_SUBMITTABLE',

  // ── Project ──────────────────────────────────────────────────────
  PROJECT_DUPLICATE_NAME:   'PROJECT_DUPLICATE_NAME',
  PROJECT_NOT_FOUND:        'PROJECT_NOT_FOUND',
  PROJECT_MEMBER_EXISTS:    'PROJECT_MEMBER_EXISTS',
  PROJECT_MEMBER_NOT_FOUND: 'PROJECT_MEMBER_NOT_FOUND',

  // ── Workflow ─────────────────────────────────────────────────────
  WORKFLOW_NOT_FOUND:       'WORKFLOW_NOT_FOUND',
  WORKFLOW_ALREADY_COMPLETE:'WORKFLOW_ALREADY_COMPLETE',

  // ── Auth ─────────────────────────────────────────────────────────
  AUTH_INVALID_CREDENTIALS: 'AUTH_INVALID_CREDENTIALS',
  AUTH_USER_EXISTS:          'AUTH_USER_EXISTS',

  // ── Generic ──────────────────────────────────────────────────────
  DUPLICATE_RESOURCE:       'DUPLICATE_RESOURCE',
  RESOURCE_NOT_FOUND:       'RESOURCE_NOT_FOUND',
  BAD_REQUEST:              'BAD_REQUEST',
  BUSINESS_RULE_VIOLATION:  'BUSINESS_RULE_VIOLATION',
  INTERNAL_ERROR:           'INTERNAL_ERROR',
} as const;

export type ErrorCodeType = (typeof ErrorCode)[keyof typeof ErrorCode];
