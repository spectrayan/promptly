import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { marked } from 'marked';
import DOMPurify from 'dompurify';
import mermaid from 'mermaid';

export interface DocArticle {
  id: string;
  title: string;
  description: string;
  file: string;
  category: string;
  sortOrder: number;
  content?: string | null;
}

interface DocsManifest {
  articles: DocArticle[];
}

@Injectable({ providedIn: 'root' })
export class HelpDocsStore {
  private http = inject(HttpClient);
  private basePath = 'assets/docs';

  private _manifest = signal<DocsManifest | null>(null);
  private _selectedId = signal<string | null>(null);
  private _contentCache = signal<Record<string, string>>({});
  private _loading = signal(false);
  private _error = signal<string | null>(null);

  readonly articles = computed(() => {
    const m = this._manifest();
    return m ? m.articles.sort((a, b) => a.sortOrder - b.sortOrder) : [];
  });

  readonly selectedArticle = computed(() => {
    const id = this._selectedId();
    const m = this._manifest();
    const cache = this._contentCache();
    if (!id || !m) return null;
    const entry = m.articles.find(a => a.id === id);
    if (!entry) return null;
    return { ...entry, content: cache[id] ?? null };
  });

  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();

  async loadManifest(): Promise<void> {
    if (this._manifest()) return;
    this._loading.set(true);
    this._error.set(null);
    try {
      const manifest = await firstValueFrom(
        this.http.get<DocsManifest>(`${this.basePath}/docs-registry.json`)
      );
      this._manifest.set(manifest);
    } catch {
      this._error.set('Failed to load documentation index.');
    } finally {
      this._loading.set(false);
    }
  }

  async selectArticle(id: string): Promise<void> {
    this._selectedId.set(id);
    this._error.set(null);
    if (this._contentCache()[id]) return;

    const entry = this._manifest()?.articles.find(a => a.id === id);
    if (!entry) return;

    this._loading.set(true);
    try {
      const text = await firstValueFrom(
        this.http.get(`${this.basePath}/${entry.file}`, { responseType: 'text' })
      );
      const rawHtml = await marked.parse(text);
      let html = rawHtml.replace(/<pre><code class="language-mermaid">([\s\S]*?)<\/code><\/pre>/g, '<div class="mermaid">$1</div>');
      const cleanHtml = DOMPurify.sanitize(html, { ADD_ATTR: ['class'] });

      this._contentCache.update(c => ({ ...c, [id]: cleanHtml }));
    } catch {
      this._error.set('Failed to load article.');
    } finally {
      this._loading.set(false);
    }
  }

  clearSelection(): void {
    this._selectedId.set(null);
  }
}
