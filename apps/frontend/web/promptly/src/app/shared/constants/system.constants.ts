/**
 * System-level constants shared across the frontend application.
 * These values must stay in sync with their backend counterparts
 * (see SystemPromptPort.SYSTEM_PROJECT_ID in the shared module).
 */

/** Well-known project name for system-level AI prompts. */
export const SYSTEM_PROJECT_NAME = '__system__';

/**
 * Returns the conventional prompt name for a given AI feature.
 * Must match the naming pattern used by SystemProjectSeeder on the backend.
 */
export function systemPromptName(feature: string): string {
  return `${feature}-system-prompt`;
}
