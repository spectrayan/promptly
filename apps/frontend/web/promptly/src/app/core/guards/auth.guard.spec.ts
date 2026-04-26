import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: {
            createUrlTree: vi.fn((commands: string[]) => ({
              __test_url_tree__: true,
              commands,
            })),
          },
        },
      ],
    });
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('allows access when token exists', () => {
    localStorage.setItem('promptly_access_token', 'mock-jwt-token');

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as any, {} as any)
    );

    expect(result).toBe(true);
  });

  it('redirects to /login when no token', () => {
    localStorage.removeItem('promptly_access_token');

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as any, {} as any)
    );

    expect(result).not.toBe(true);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/login']);
  });

  it('redirects when token is removed after being set', () => {
    localStorage.setItem('promptly_access_token', 'token');
    localStorage.removeItem('promptly_access_token');

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as any, {} as any)
    );

    expect(result).not.toBe(true);
  });
});
