import { Injectable, inject } from '@angular/core';
import { MOCK_API_CONFIG, MockApiConfig } from '../tokens/mock-api.token';

export interface ApiMapping {
  /** URL pattern (string or RegExp) to match requests against. */
  pattern: string | RegExp;
  /** HTTP method to match. */
  method: string;
  /** Handler that returns mock response data. */
  handler: (req: any) => any;
}

/**
 * Service for registering and resolving mock API mappings.
 * When mock mode is disabled, all lookups return null (passthrough to real API).
 */
@Injectable({ providedIn: 'root' })
export class MockDataService {
  private readonly config = inject(MOCK_API_CONFIG);
  private readonly mappings: ApiMapping[] = [];

  get enabled(): boolean {
    return this.config.enabled;
  }

  register(mapping: ApiMapping): void {
    this.mappings.push(mapping);
  }

  resolve(url: string, method: string): ApiMapping | null {
    if (!this.config.enabled) return null;
    return this.mappings.find(m => {
      const patternMatch = m.pattern instanceof RegExp
        ? m.pattern.test(url)
        : url.includes(m.pattern);
      return patternMatch && m.method.toUpperCase() === method.toUpperCase();
    }) ?? null;
  }
}
