import { EnumLabelPipe } from './enum-label.pipe';

describe('EnumLabelPipe', () => {
  let pipe: EnumLabelPipe;

  beforeEach(() => {
    pipe = new EnumLabelPipe();
  });

  it('transforms SCREAMING_SNAKE_CASE to Title Case', () => {
    expect(pipe.transform('IN_REVIEW')).toBe('In Review');
  });

  it('transforms single word enum', () => {
    expect(pipe.transform('DRAFT')).toBe('Draft');
  });

  it('transforms multi-word enum', () => {
    expect(pipe.transform('PROMPT_CREATED')).toBe('Prompt Created');
  });

  it('returns empty string for null', () => {
    expect(pipe.transform(null)).toBe('');
  });

  it('returns empty string for undefined', () => {
    expect(pipe.transform(undefined)).toBe('');
  });

  it('returns empty string for empty string', () => {
    expect(pipe.transform('')).toBe('');
  });
});
