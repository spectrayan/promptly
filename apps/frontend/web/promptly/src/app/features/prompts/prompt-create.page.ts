import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
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
import { CreatePromptRequest } from '@promptly/client';
import { PromptsFacade } from '../../state/prompts/prompts.facade';
import { ProjectsFacade } from '../../state/projects/projects.facade';
import { MonacoEditorComponent } from '../../shared/components/monaco-editor/monaco-editor.component';

@Component({
  selector: 'promptly-prompt-create',
  imports: [
    FormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule,
    MatTabsModule, MatChipsModule, MonacoEditorComponent,
  ],
  templateUrl: './prompt-create.page.html',
  styleUrl: './prompt-create.page.scss',
})
export class PromptCreatePage {
  private readonly router = inject(Router);
  readonly facade = inject(PromptsFacade);
  readonly projectsFacade = inject(ProjectsFacade);

  form: Partial<CreatePromptRequest> = {
    name: '',
    description: '',
    projectId: '',
    contentFormat: 'TEXT',
    content: '',
    author: 'admin',
  };

  tagsInput = '';

  // AI Assist state
  aiPromptIdea = '';
  aiGenerating = false;
  aiRefining = false;
  aiSuggestion = '';
  showAiPanel = false;

  isValid(): boolean {
    return !!(this.form.name && this.form.projectId && this.form.content);
  }

  submit(): void {
    if (!this.isValid()) return;
    this.facade.createPrompt(this.form as CreatePromptRequest);
    this.router.navigate(['/prompts']);
  }

  cancel(): void {
    this.router.navigate(['/prompts']);
  }

  toggleAiPanel(): void {
    this.showAiPanel = !this.showAiPanel;
  }

  generateFromIdea(): void {
    if (!this.aiPromptIdea.trim()) return;
    this.aiGenerating = true;
    this.aiSuggestion = '';

    setTimeout(() => {
      this.aiSuggestion = this.mockGenerate(this.aiPromptIdea);
      this.aiGenerating = false;
    }, 1200);
  }

  refineContent(): void {
    if (!this.form.content) return;
    this.aiRefining = true;
    this.aiSuggestion = '';

    setTimeout(() => {
      this.aiSuggestion = this.mockRefine(this.form.content!);
      this.aiRefining = false;
    }, 1500);
  }

  acceptSuggestion(): void {
    this.form.content = this.aiSuggestion;
    this.aiSuggestion = '';
  }

  private mockGenerate(idea: string): string {
    return `You are an AI assistant specialized in ${idea.toLowerCase()}.

## Instructions
1. Analyze the user's input carefully
2. Provide a structured, accurate response
3. If uncertain, ask clarifying questions

## Constraints
- Stay within the scope of ${idea.toLowerCase()}
- Never fabricate information
- Always cite sources when applicable

## Output Format
Provide your response in clear, structured markdown.`;
  }

  private mockRefine(content: string): string {
    return `${content}

## Safety Guards
- Reject requests outside scope
- Never expose internal system details
- Validate all user inputs before processing
- Limit response length to 2000 tokens

## Quality Checks
- Ensure factual accuracy
- Maintain consistent tone
- Follow established templates`;
  }
}
