import { enumToLabel, capitalizeFirstLetter, truncateString } from './string-utils';

describe('string-utils', () => {
  // ── enumToLabel ──────────────────────────────────────────────
  describe('enumToLabel', () => {
    it('converts SCREAMING_SNAKE_CASE to Title Case', () => {
      expect(enumToLabel('IN_REVIEW')).toBe('In Review');
    });

    it('handles single word', () => {
      expect(enumToLabel('DRAFT')).toBe('Draft');
    });

    it('handles three words', () => {
      expect(enumToLabel('PROMPT_CREATED_SUCCESSFULLY')).toBe('Prompt Created Successfully');
    });

    it('returns empty string for null', () => {
      expect(enumToLabel(null)).toBe('');
    });

    it('returns empty string for undefined', () => {
      expect(enumToLabel(undefined)).toBe('');
    });

    it('returns empty string for empty string', () => {
      expect(enumToLabel('')).toBe('');
    });
  });

  // ── capitalizeFirstLetter ──────────────────────────────────
  describe('capitalizeFirstLetter', () => {
    it('capitalizes the first letter', () => {
      expect(capitalizeFirstLetter('hello')).toBe('Hello');
    });

    it('handles already capitalized', () => {
      expect(capitalizeFirstLetter('Hello')).toBe('Hello');
    });

    it('handles single character', () => {
      expect(capitalizeFirstLetter('a')).toBe('A');
    });

    it('returns empty string for empty string', () => {
      expect(capitalizeFirstLetter('')).toBe('');
    });
  });

  // ── truncateString ─────────────────────────────────────────
  describe('truncateString', () => {
    it('truncates long strings and adds ellipsis', () => {
      expect(truncateString('Hello World', 5)).toBe('Hello...');
    });

    it('does not truncate short strings', () => {
      expect(truncateString('Hi', 10)).toBe('Hi');
    });

    it('handles exact length', () => {
      expect(truncateString('Hello', 5)).toBe('Hello');
    });

    it('handles empty string', () => {
      expect(truncateString('', 5)).toBe('');
    });
  });
});
