import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CreatePromptRequest, ImproverService, ImprovementResponse, PromptsService, GenerateFromIdeaResponse } from '@promptly/client';
import { PromptsFacade } from '../../../state/prompts/prompts.facade';
import { ProjectsFacade } from '../../../state/projects/projects.facade';
import { MonacoEditorComponent } from '../../../shared/components/monaco-editor/monaco-editor.component';
import { MarkdownPreviewComponent } from '../../../shared/components/markdown-preview/markdown-preview.component';
import { AuthFacade } from '../../../state/auth/auth.facade';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-prompt-create',
  imports: [
    FormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule,
    MatTabsModule, MatChipsModule, MatTooltipModule, MatSnackBarModule, MonacoEditorComponent, MarkdownPreviewComponent,
  ],
  templateUrl: './prompt-create.page.html',
  styleUrl: './prompt-create.page.scss',
})
export class PromptCreatePage {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly improverService = inject(ImproverService);
  private readonly promptsService = inject(PromptsService);
  readonly facade = inject(PromptsFacade);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly auth = inject(AuthFacade);

  private projectId: string | null = null;

  form: Partial<CreatePromptRequest> = {
    name: '',
    description: '',
    projectId: '',
    contentFormat: 'TEXT',
    content: '',
    author: '',
  };

  tagsInput = '';

  constructor() {
    // Pre-fill projectId from the route
    this.projectId = this.route.snapshot.paramMap.get('projectId');
    if (this.projectId) {
      this.form.projectId = this.projectId;
      this.form.author = this.auth.user()?.id ?? 'unknown';
    }
  }

  // ── AI Assist state ─────────────────────────────────────────────
  showAiPanel = false;

  // ── View mode for markdown preview ─────────────────────────────
  readonly viewMode = signal<'edit' | 'split' | 'preview'>('edit');

  get isMarkdownFormat(): boolean {
    return this.form.contentFormat?.toUpperCase() === 'MARKDOWN';
  }
  readonly aiGenerating = signal(false);
  readonly aiRefining = signal(false);
  readonly aiSuggestion = signal('');
  readonly aiSummary = signal('');
  readonly aiError = signal<string | null>(null);

  isValid(): boolean {
    return !!(this.form.name && this.form.projectId && this.form.content);
  }

  submit(): void {
    if (!this.isValid()) return;
    this.facade.createPrompt(this.form as CreatePromptRequest);
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId, 'prompts']);
    }
  }

  cancel(): void {
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId, 'prompts']);
    }
  }

  toggleAiPanel(): void {
    this.showAiPanel = !this.showAiPanel;
  }

  /**
   * Generate prompt content from an idea.
   *
   * Calls the dedicated generateFromIdea API endpoint which uses a
   * separate LLM system prompt designed for creating prompts from scratch.
   * No temporary prompt creation needed.
   */
  generateFromIdea(idea: string): void {
    if (!idea.trim()) {
      this.snackBar.open('Please enter an idea', 'OK', { duration: 3000 });
      return;
    }

    this.aiGenerating.set(true);
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);

    this.promptsService.generateFromIdea({
      generateFromIdeaRequest: {
        idea,
        projectId: this.form.projectId ?? undefined,
      },
    }).subscribe({
      next: (response: GenerateFromIdeaResponse) => {
        this.aiSuggestion.set(response.generatedContent ?? '');
        this.aiSummary.set(response.summary ?? '');
        this.aiGenerating.set(false);

        // Auto-fill suggested title if the form name is still empty
        if (!this.form.name && response.title) {
          this.form.name = response.title;
        }

        // AI-generated prompts are always markdown
        this.form.contentFormat = 'MARKDOWN';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.aiError.set(err?.error?.detail ?? 'AI generation failed');
        this.aiGenerating.set(false);
        this.snackBar.open('AI generation failed. Please try again.', 'OK', { duration: 3000 });
      },
    });
  }

  /**
   * Refine current content using the Improver API.
   *
   * Same create-improve-delete flow as generateFromIdea, but seeds
   * the temporary prompt with the user's current draft content.
   */
  refineContent(): void {
    if (!this.form.content?.trim()) return;
    if (!this.form.projectId) {
      this.snackBar.open('Please select a project first', 'OK', { duration: 3000 });
      return;
    }

    this.aiRefining.set(true);
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);

    // Create a temporary prompt with current content
    const tempRequest: CreatePromptRequest = {
      name: `_temp_refine_${Date.now()}`,
      description: 'Temporary prompt for AI refinement',
      projectId: this.form.projectId!,
      contentFormat: this.form.contentFormat ?? 'TEXT',
      content: this.form.content!,
      author: this.auth.user()?.id ?? 'system',
    };

    this.facade.createPrompt(tempRequest);

    const checkInterval = setInterval(() => {
      const created = this.facade.selected();
      if (created?.id && created.name?.startsWith('_temp_refine_')) {
        clearInterval(checkInterval);

        this.improverService.improvePrompt({ promptId: created.id }).subscribe({
          next: (response: ImprovementResponse) => {
            this.aiSuggestion.set(response.improvedContent ?? '');
            this.aiSummary.set(response.summary ?? '');
            this.aiRefining.set(false);

            // Clean up
            this.facade.deletePrompt(created.id!);
            this.facade.clearSelection();
          },
          error: (err) => {
            this.aiError.set(err?.error?.detail ?? 'AI refinement failed');
            this.aiRefining.set(false);
            this.facade.deletePrompt(created.id!);
            this.facade.clearSelection();
            this.snackBar.open('AI refinement failed. Please try again.', 'OK', { duration: 3000 });
          },
        });
      }
    }, 300);

    setTimeout(() => {
      clearInterval(checkInterval);
      if (this.aiRefining()) {
        this.aiRefining.set(false);
        this.aiError.set('Refinement timed out. Please try again.');
        this.snackBar.open('AI refinement timed out', 'OK', { duration: 3000 });
      }
    }, 30000);
  }

  acceptSuggestion(): void {
    this.form.content = this.aiSuggestion();
    this.form.contentFormat = 'MARKDOWN'; // AI always generates markdown
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    // Explicit change detection needed so Monaco editor picks up new content
    // via its @Input() value binding under OnPush strategy
    this.cdr.markForCheck();
  }

  dismissSuggestion(): void {
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);
  }
}
