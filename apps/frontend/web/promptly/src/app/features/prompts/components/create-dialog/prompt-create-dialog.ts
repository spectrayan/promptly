import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CreatePromptRequest, ImproverService, ImprovementResponse, PromptsService, GenerateFromIdeaResponse } from '@promptly/client';
import { ProjectsFacade } from '../../../../state/projects/projects.facade';
import { PromptsFacade } from '../../../../state/prompts/prompts.facade';
import { AuthFacade } from '../../../../state/auth/auth.facade';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-prompt-create-dialog',
  imports: [
    FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule,
    MatTooltipModule, MatTabsModule, MatSnackBarModule,
  ],
  templateUrl: './prompt-create-dialog.html',
  styleUrl: './prompt-create-dialog.scss',
})
export class PromptCreateDialog {
  private readonly dialogRef = inject(MatDialogRef<PromptCreateDialog>);
  private readonly improverService = inject(ImproverService);
  private readonly promptsService = inject(PromptsService);
  private readonly promptsFacade = inject(PromptsFacade);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);
  readonly projectsFacade = inject(ProjectsFacade);
  private readonly auth = inject(AuthFacade);

  form: Partial<CreatePromptRequest> = {
    name: '',
    description: '',
    projectId: '',
    contentFormat: 'TEXT',
    content: '',
    author: '',
  };

  constructor() {
    // Set author to authenticated user's ID
    this.form.author = this.auth.user()?.id ?? 'unknown';
  }

  // ── AI Assist state (signal-based) ──────────────────────────────
  showAiPanel = false;
  readonly aiGenerating = signal(false);
  readonly aiRefining = signal(false);
  readonly aiSuggestion = signal('');
  readonly aiSummary = signal('');
  readonly aiError = signal<string | null>(null);

  isValid(): boolean {
    return !!(this.form.name && this.form.projectId && this.form.content);
  }

  submit(): void {
    this.dialogRef.close(this.form as CreatePromptRequest);
  }

  toggleAiPanel(): void {
    this.showAiPanel = !this.showAiPanel;
  }

  /**
   * Generate prompt content from an idea using the dedicated generate API.
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
   * Creates a temporary prompt, calls improve, and cleans up.
   */
  refineContent(): void {
    if (!this.form.content?.trim() || !this.form.projectId) {
      this.snackBar.open('Please select a project first', 'OK', { duration: 3000 });
      return;
    }

    this.aiRefining.set(true);
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);

    const tempRequest: CreatePromptRequest = {
      name: `_temp_dialog_ref_${Date.now()}`,
      description: 'Temporary prompt for AI refinement',
      projectId: this.form.projectId!,
      contentFormat: this.form.contentFormat ?? 'TEXT',
      content: this.form.content!,
      author: this.auth.user()?.id ?? 'system',
    };

    this.promptsFacade.createPrompt(tempRequest);

    const checkInterval = setInterval(() => {
      const created = this.promptsFacade.selected();
      if (created?.id && created.name?.startsWith('_temp_dialog_ref_')) {
        clearInterval(checkInterval);

        this.improverService.improvePrompt({ promptId: created.id }).subscribe({
          next: (response: ImprovementResponse) => {
            this.aiSuggestion.set(response.improvedContent ?? '');
            this.aiSummary.set(response.summary ?? '');
            this.aiRefining.set(false);
            this.promptsFacade.deletePrompt(created.id!);
            this.promptsFacade.clearSelection();
          },
          error: (err) => {
            this.aiError.set(err?.error?.detail ?? 'AI refinement failed');
            this.aiRefining.set(false);
            this.promptsFacade.deletePrompt(created.id!);
            this.promptsFacade.clearSelection();
          },
        });
      }
    }, 300);

    setTimeout(() => {
      clearInterval(checkInterval);
      if (this.aiRefining()) {
        this.aiRefining.set(false);
        this.aiError.set('Refinement timed out. Please try again.');
      }
    }, 30000);
  }

  acceptSuggestion(): void {
    this.form.content = this.aiSuggestion();
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    // Explicit change detection for OnPush
    this.cdr.markForCheck();
  }

  dismissSuggestion(): void {
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);
  }
}
