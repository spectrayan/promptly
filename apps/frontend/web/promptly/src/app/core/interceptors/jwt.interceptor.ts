import { HttpInterceptorFn } from '@angular/common/http';

const TOKEN_KEY = 'promptly_access_token';

/**
 * Attaches the JWT access token to all outgoing HTTP requests.
 * Skips auth endpoints (login, register, refresh) to avoid circular issues.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // Don't attach token to auth endpoints
  if (req.url.includes('/auth/login') || req.url.includes('/auth/register') || req.url.includes('/auth/refresh')) {
    return next(req);
  }

  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
    return next(authReq);
  }

  return next(req);
};
