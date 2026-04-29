import { InjectionToken } from '@angular/core';

export interface MockApiConfig {
  /** Whether mock API interception is enabled. */
  enabled: boolean;
  /** Simulated latency in milliseconds. */
  delay?: number;
}

export const MOCK_API_CONFIG = new InjectionToken<MockApiConfig>('MOCK_API_CONFIG', {
  providedIn: 'root',
  factory: () => ({ enabled: false }),
});
