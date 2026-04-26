import { resolveErrorMessage, FallbackMessages } from './error-messages';
import { ErrorCode } from './error-codes';

describe('resolveErrorMessage', () => {
  it('resolves a known error code to the mapped message', () => {
    const error = { code: ErrorCode.PROMPT_DUPLICATE_NAME };
    const result = resolveErrorMessage(error, 'fallback');
    expect(result).toContain('already exists');
    expect(result).not.toBe('fallback');
  });

  it('falls back to the detail field when code is unknown', () => {
    const error = { code: 'SOME_UNKNOWN_CODE', detail: 'Server detail message' };
    expect(resolveErrorMessage(error, 'fallback')).toBe('Server detail message');
  });

  it('falls back to the default message when no code or detail', () => {
    expect(resolveErrorMessage(null, 'fallback')).toBe('fallback');
    expect(resolveErrorMessage(undefined, 'fallback')).toBe('fallback');
  });

  it('falls back to default when error object has no code', () => {
    const error = { status: 500 };
    expect(resolveErrorMessage(error, 'Something went wrong')).toBe('Something went wrong');
  });

  it('uses detail over fallback when code is missing', () => {
    const error = { detail: 'Detailed server error' };
    expect(resolveErrorMessage(error, 'fallback')).toBe('Detailed server error');
  });

  it('maps all prompt-related error codes', () => {
    expect(resolveErrorMessage({ code: ErrorCode.PROMPT_NOT_FOUND }, 'x')).toContain('could not be found');
    expect(resolveErrorMessage({ code: ErrorCode.PROMPT_NOT_EDITABLE }, 'x')).toContain('cannot be edited');
    expect(resolveErrorMessage({ code: ErrorCode.PROMPT_NOT_SUBMITTABLE }, 'x')).toContain('not eligible');
  });

  it('maps workflow error codes', () => {
    expect(resolveErrorMessage({ code: ErrorCode.WORKFLOW_NOT_FOUND }, 'x')).toContain('could not be found');
    expect(resolveErrorMessage({ code: ErrorCode.WORKFLOW_ALREADY_COMPLETE }, 'x')).toContain('already been completed');
  });
});

describe('FallbackMessages', () => {
  it('contains all expected prompt fallbacks', () => {
    expect(FallbackMessages.LOAD_PROMPTS).toBe('Failed to load prompts');
    expect(FallbackMessages.CREATE_PROMPT).toBe('Failed to create prompt');
    expect(FallbackMessages.UPDATE_PROMPT).toBe('Failed to update prompt');
    expect(FallbackMessages.DELETE_PROMPT).toBe('Failed to delete prompt');
    expect(FallbackMessages.ROLLBACK_PROMPT).toBe('Failed to rollback prompt');
  });

  it('contains all expected workflow fallbacks', () => {
    expect(FallbackMessages.LOAD_WORKFLOWS).toBe('Failed to load workflows');
    expect(FallbackMessages.SUBMIT_REVIEW).toBe('Failed to submit for review');
    expect(FallbackMessages.APPROVE_WORKFLOW).toBe('Failed to approve workflow');
    expect(FallbackMessages.REJECT_WORKFLOW).toBe('Failed to reject workflow');
  });
});
