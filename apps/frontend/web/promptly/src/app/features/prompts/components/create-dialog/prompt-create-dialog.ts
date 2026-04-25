import { Component, inject, signal } from '@angular/core';
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
import { CreatePromptRequest, ImproverService, ImprovementResponse } from '@promptly/client';
import { ProjectsFacade } from '../../../../state/projects/projects.facade';
import { PromptsFacade } from '../../../../state/prompts/prompts.facade';

@Component({
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
  private readonly promptsFacade = inject(PromptsFacade);
  private readonly snackBar = inject(MatSnackBar);
  readonly projectsFacade = inject(ProjectsFacade);

  form: Partial<CreatePromptRequest> = {
    name: '',
    description: '',
    projectId: '',
    contentFormat: 'TEXT',
    content: '',
    author: 'admin',
  };

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
   * Generate prompt content from an idea using the Improver API.
   * Creates a temporary prompt, calls improve, and cleans up.
   */
  generateFromIdea(idea: string): void {
    if (!idea.trim() || !this.form.projectId) {
      this.snackBar.open('Please select a project first', 'OK', { duration: 3000 });
      return;
    }

    this.aiGenerating.set(true);
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);

    const tempRequest: CreatePromptRequest = {
      name: `_temp_dialog_gen_${Date.now()}`,
      description: 'Temporary prompt for AI generation',
      projectId: this.form.projectId!,
      contentFormat: this.form.contentFormat ?? 'TEXT',
      content: `Generate a production-quality prompt for the following use case:\n\n${idea}`,
      author: 'system',
    };

    this.promptsFacade.createPrompt(tempRequest);

    const checkInterval = setInterval(() => {
      const created = this.promptsFacade.selected();
      if (created?.id && created.name?.startsWith('_temp_dialog_gen_')) {
        clearInterval(checkInterval);

        this.improverService.improvePrompt({ promptId: created.id }).subscribe({
          next: (response: ImprovementResponse) => {
            this.aiSuggestion.set(response.improvedContent ?? '');
            this.aiSummary.set(response.summary ?? '');
            this.aiGenerating.set(false);
            this.promptsFacade.deletePrompt(created.id!);
            this.promptsFacade.clearSelection();
          },
          error: (err) => {
            this.aiError.set(err?.error?.detail ?? 'AI generation failed');
            this.aiGenerating.set(false);
            this.promptsFacade.deletePrompt(created.id!);
            this.promptsFacade.clearSelection();
          },
        });
      }
    }, 300);

    setTimeout(() => {
      clearInterval(checkInterval);
      if (this.aiGenerating()) {
        this.aiGenerating.set(false);
        this.aiError.set('Generation timed out. Please try again.');
      }
    }, 30000);
  }

  /**
   * Refine current content using the Improver API.
   * Same create-improve-delete cycle.
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
      author: 'system',
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
  }

  dismissSuggestion(): void {
    this.aiSuggestion.set('');
    this.aiSummary.set('');
    this.aiError.set(null);
  }
}
