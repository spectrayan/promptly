import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

const TOKEN_KEY = 'promptly_access_token';

/**
 * Route guard — redirects to /login if no access token is stored.
 */
export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem(TOKEN_KEY);

  if (token) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
