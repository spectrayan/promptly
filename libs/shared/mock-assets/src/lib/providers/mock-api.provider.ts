import { EnvironmentProviders, makeEnvironmentProviders } from '@angular/core';
import { MOCK_API_CONFIG, MockApiConfig } from '../tokens/mock-api.token';

/**
 * Provides mock API configuration to the application.
 *
 * Usage:
 * ```ts
 * provideMockApi({ enabled: environment.useMockApi, delay: 300 })
 * ```
 */
export function provideMockApi(config: MockApiConfig): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: MOCK_API_CONFIG, useValue: config },
  ]);
}
