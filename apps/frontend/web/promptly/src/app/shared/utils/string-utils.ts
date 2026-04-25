/**
 * String utility functions for the Promptly platform.
 */

/**
 * Converts a SCREAMING_SNAKE_CASE enum constant to a human-readable label.
 * e.g., "IN_REVIEW" → "In Review", "PROMPT_CREATED" → "Prompt Created"
 *
 * @param value The enum constant string (e.g., "IN_REVIEW")
 * @returns A human-readable label (e.g., "In Review"), or empty string if input is falsy
 */
export function enumToLabel(value: string | null | undefined): string {
  if (!value) return '';
  return value
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
}

/**
 * Capitalizes the first letter of a string.
 * @param str The string to capitalize
 * @returns The string with the first letter capitalized
 */
export function capitalizeFirstLetter(str: string): string {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Truncates a string to a specified length and adds an ellipsis if truncated.
 * @param str The string to truncate
 * @param maxLength The maximum length of the string
 * @returns The truncated string
 */
export function truncateString(str: string, maxLength: number): string {
  if (!str || str.length <= maxLength) return str;
  return str.slice(0, maxLength) + '...';
}
