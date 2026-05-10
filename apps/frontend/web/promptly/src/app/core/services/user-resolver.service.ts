import { Injectable, inject } from '@angular/core';
import { ProjectsService } from '@promptly/client';
import { firstValueFrom } from 'rxjs';

/**
 * Resolves user IDs and emails to display names.
 *
 * Uses the project members API to build a cached lookup map.
 * Handles both user IDs (e.g., "usr-001") and emails (e.g., "alice@promptly.ai")
 * since seed data and runtime data may use different identifier formats.
 */
@Injectable({ providedIn: 'root' })
export class UserResolverService {
  private readonly projectsApi = inject(ProjectsService);

  /** Map of userId OR email → displayName */
  private readonly cache = new Map<string, string>();
  private loadedProjects = new Set<string>();

  /**
   * Ensure members for a given project are loaded into the cache.
   * Safe to call multiple times — won't re-fetch for the same project.
   */
  async loadProjectMembers(projectId: string): Promise<void> {
    if (this.loadedProjects.has(projectId)) return;

    try {
      const members = await firstValueFrom(
        this.projectsApi.listProjectMembers({ projectId })
      );
      for (const m of members) {
        if (m.userId) this.cache.set(m.userId, m.displayName);
        if (m.email) this.cache.set(m.email, m.displayName);
      }
      this.loadedProjects.add(projectId);
    } catch {
      // Silently fail — we'll fallback to the raw identifier
    }
  }

  /**
   * Resolve a user identifier (ID or email) to a display name.
   * Falls back to the raw identifier if not found.
   */
  resolve(identifier: string | undefined | null): string {
    if (!identifier) return '—';
    return this.cache.get(identifier) ?? this.formatFallback(identifier);
  }

  /** Well-known system identifiers → human labels */
  private static readonly SYSTEM_LABELS: Record<string, string> = {
    admin: 'Admin',
    system: 'System',
    unknown: 'Unknown',
  };

  /**
   * Best-effort fallback: extract a readable name from an email,
   * map well-known system labels, or return the raw value for IDs.
   */
  private formatFallback(identifier: string): string {
    const lower = identifier.toLowerCase();
    if (UserResolverService.SYSTEM_LABELS[lower]) {
      return UserResolverService.SYSTEM_LABELS[lower];
    }
    if (identifier.includes('@')) {
      // "alice@promptly.ai" → "alice"
      return identifier.split('@')[0];
    }
    return identifier;
  }
}
