import { Component, inject, signal, output, OnInit, ChangeDetectionStrategy, ElementRef, effect, DestroyRef } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatListModule } from '@angular/material/list';
import { HelpDocsStore } from '../../../core/services/help-docs.store';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import mermaid from 'mermaid';

@Component({
  selector: 'promptly-help-docs-panel',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatListModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './help-docs-panel.component.html',
  styleUrl: './help-docs-panel.component.scss'
})
export class HelpDocsPanelComponent implements OnInit {
  readonly closePanel = output<void>();
  private store = inject(HelpDocsStore);
  private el = inject(ElementRef);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  articles = this.store.articles;
  selectedArticle = this.store.selectedArticle;
  loading = this.store.loading;
  error = this.store.error;
  viewMode = signal<'list' | 'article'>('list');

  constructor() {
    mermaid.initialize({ startOnLoad: false, theme: 'default' });
    
    effect(() => {
      // Re-run mermaid whenever the article view becomes active and content is loaded
      if (this.viewMode() === 'article' && this.selectedArticle()?.content) {
        setTimeout(() => {
          try {
             mermaid.run({
               nodes: this.el.nativeElement.querySelectorAll('.mermaid')
             });
          } catch (e) {
             console.error('Mermaid render error', e);
          }
        }, 100);
      }
    });

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((event: any) => {
      this.autoSelectArticle(event.urlAfterRedirects);
    });
  }

  ngOnInit(): void {
    this.store.loadManifest().then(() => {
      this.autoSelectArticle(this.router.url);
    });
  }

  autoSelectArticle(url: string): void {
    let id = 'overview';
    if (url.includes('/dashboard')) id = 'dashboard';
    else if (url.includes('/registry') || url.includes('/prompts')) id = 'registry';
    else if (url.includes('/workflows') || url.includes('/reviews')) id = 'workflows';
    else if (url.includes('/security') || url.includes('/scans')) id = 'security';

    this.onSelectArticle(id);
  }

  onSelectArticle(id: string): void {
    this.viewMode.set('article');
    this.store.selectArticle(id);
  }

  backToList(): void {
    this.viewMode.set('list');
    this.store.clearSelection();
  }
}
