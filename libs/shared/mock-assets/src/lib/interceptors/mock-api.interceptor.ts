import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { delay, of } from 'rxjs';
import { MockDataService } from '../services/mock-data.service';
import { MOCK_API_CONFIG } from '../tokens/mock-api.token';

/**
 * Functional HTTP interceptor for mock API responses.
 * When mock mode is disabled, requests pass through unmodified.
 */
export const mockApiInterceptor: HttpInterceptorFn = (req, next) => {
  const mockService = inject(MockDataService);
  const config = inject(MOCK_API_CONFIG);

  if (!config.enabled) {
    return next(req);
  }

  const mapping = mockService.resolve(req.url, req.method);
  if (!mapping) {
    return next(req);
  }

  const body = mapping.handler(req);
  const response = new HttpResponse({ status: 200, body });

  return of(response).pipe(delay(config.delay ?? 200));
};
