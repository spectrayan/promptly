import { ChangeDetectionStrategy, Component, inject, OnInit, signal, computed, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { LowerCasePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PromptsFacade } from '../../state/prompts/prompts.facade';
import { ProjectsFacade } from '../../state/projects/projects.facade';
import { SYSTEM_PROJECT_NAME, systemPromptName } from '../../shared/constants/system.constants';

interface LlmConfig {
  provider: string;
  model: string;
  temperature: number;
  maxTokens: number;
  baseUrl: string;
  apiKeyConfigured: boolean;
  apiKeyHint?: string;
}

interface ResolvedConfigResponse {
  config: LlmConfig;
  lockedFields: string[];
  source: Record<string, string>;
  deploymentMode: string;
  encryptionAvailable: boolean;
}

const PROVIDERS = [
  { value: 'openai', label: 'OpenAI', icon: '🤖', models: ['gpt-4o', 'gpt-4o-mini', 'gpt-4-turbo', 'o1', 'o3-mini'] },
  { value: 'anthropic', label: 'Anthropic', icon: '🧠', models: ['claude-sonnet-4', 'claude-haiku-3.5', 'claude-opus-4'] },
  { value: 'gemini', label: 'Google Gemini', icon: '💎', models: ['gemini-2.5-flash', 'gemini-2.5-pro', 'gemini-2.0-flash'] },
  { value: 'ollama', label: 'Ollama (Local)', icon: '🏠', models: ['llama3', 'mistral', 'codellama', 'phi3'] },
];

const FEATURES = [
  { value: 'global', label: 'Global Default', icon: 'public', description: 'Default for all features unless overridden' },
  { value: 'scanner', label: 'Security Scanner', icon: 'security', description: 'LLM used for vulnerability scanning' },
  { value: 'improver', label: 'Prompt Improver', icon: 'auto_fix_high', description: 'LLM used for prompt enhancement' },
  { value: 'embedding', label: 'Embeddings', icon: 'data_array', description: 'Model used for vector embeddings' },
];

const SYSTEM_PROMPT_FEATURES = [
  { key: 'scanner', label: 'Security Scanner', icon: 'security', description: 'Analyzes prompts for vulnerabilities, PHI exposure, and injection risks' },
  { key: 'improver', label: 'Prompt Improver', icon: 'auto_fix_high', description: 'Enhances prompts for clarity, safety, and determinism' },
];

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'promptly-settings',
  imports: [
    FormsModule,
    LowerCasePipe,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatTabsModule, MatChipsModule,
    MatSlideToggleModule, MatTooltipModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatDividerModule,
  ],
  templateUrl: './settings.page.html',
  styleUrl: './settings.page.scss',
})
export class SettingsPage implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly snackBar = inject(MatSnackBar);
  private readonly apiBase = environment.apiBasePath;
  private readonly promptsFacade = inject(PromptsFacade);
  private readonly projectsFacade = inject(ProjectsFacade);

  readonly providers = PROVIDERS;
  readonly features = FEATURES;
  readonly systemPromptFeatures = SYSTEM_PROMPT_FEATURES;

  // ═══════════════════════════════════════════════════════════════
  // LLM Config State
  // ═══════════════════════════════════════════════════════════════

  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly selectedFeature = signal('global');
  readonly deploymentMode = signal('self-hosted');
  readonly encryptionAvailable = signal(false);
  readonly lockedFields = signal<string[]>([]);
  readonly sources = signal<Record<string, string>>({});

  // LLM Config Form
  provider = 'gemini';
  model = 'gemini-2.5-flash';
  temperature = 0.3;
  maxTokens = 4096;
  baseUrl = '';
  apiKey = '';
  apiKeyConfigured = false;
  apiKeyHint = '';

  readonly availableModels = computed(() => {
    const p = this.providers.find(pr => pr.value === this.provider);
    return p?.models ?? [];
  });

  readonly selectedFeatureInfo = computed(() =>
    this.features.find(f => f.value === this.selectedFeature()) ?? this.features[0]
  );

  // ═══════════════════════════════════════════════════════════════
  // System Prompt State
  //
  // Uses the PromptsFacade and ProjectsFacade — no direct SDK calls.
  // Flow: ProjectsFacade.projects → find __system__ → PromptsFacade
  // ═══════════════════════════════════════════════════════════════

  readonly selectedPromptFeature = signal('scanner');
  readonly promptSaving = signal(false);
  readonly promptIsCustomized = signal(false);
  readonly promptVersion = signal<number | null>(null);

  /** Resolved __system__ project ID. */
  private systemProjectId: string | null = null;

  /** Current prompt ID for the selected feature. */
  private currentPromptId: string | null = null;

  promptContent = '';
  promptChangeMessage = '';

  /** Delegate loading state to the facade. */
  readonly promptLoading = this.promptsFacade.loading;

  readonly selectedPromptFeatureInfo = computed(() =>
    this.systemPromptFeatures.find(f => f.key === this.selectedPromptFeature())
      ?? this.systemPromptFeatures[0]
  );

  /**
   * React to the facade's selectedPrompt signal — when a system prompt
   * is loaded via loadPrompt(), populate the editor fields.
   */
  private readonly syncSelectedPrompt = effect(() => {
    const prompt = this.promptsFacade.selected();
    if (!prompt || !this.currentPromptId || prompt.id !== this.currentPromptId) {
      return;
    }
    this.promptContent = prompt.latestContent ?? '';
    this.promptIsCustomized.set((prompt.currentVersion ?? 1) > 1);
    this.promptVersion.set(prompt.currentVersion ?? null);
    this.promptChangeMessage = '';
  });

  // ═══════════════════════════════════════════════════════════════
  // Lifecycle
  // ═══════════════════════════════════════════════════════════════

  ngOnInit(): void {
    this.loadConfig();
    this.initSystemPrompts();
  }

  // ═══════════════════════════════════════════════════════════════
  // LLM Config Methods
  // ═══════════════════════════════════════════════════════════════

  isLocked(field: string): boolean {
    return this.lockedFields().includes(field);
  }

  getSource(field: string): string {
    return this.sources()[field] ?? '';
  }

  getSourceLabel(source: string): string {
    switch (source) {
      case 'ENVIRONMENT': return 'ENV';
      case 'DATABASE': return 'DB';
      case 'YAML_DEFAULT': return 'Default';
      default: return source;
    }
  }

  getSourceClass(source: string): string {
    switch (source) {
      case 'ENVIRONMENT': return 'source-env';
      case 'DATABASE': return 'source-db';
      case 'YAML_DEFAULT': return 'source-default';
      default: return '';
    }
  }

  selectFeature(feature: string): void {
    this.selectedFeature.set(feature);
    this.loadConfig();
  }

  onProviderChange(): void {
    const models = this.availableModels();
    if (models.length > 0 && !models.includes(this.model)) {
      this.model = models[0];
    }
  }

  loadConfig(): void {
    this.loading.set(true);
    this.http.get<ResolvedConfigResponse>(
      `${this.apiBase}/api/v1/settings/llm-configs/resolved`,
      { params: { projectId: 'default', feature: this.selectedFeature() } }
    ).subscribe({
      next: (res) => {
        this.provider = res.config.provider;
        this.model = res.config.model;
        this.temperature = res.config.temperature;
        this.maxTokens = res.config.maxTokens;
        this.baseUrl = res.config.baseUrl ?? '';
        this.apiKeyConfigured = res.config.apiKeyConfigured;
        this.apiKeyHint = res.config.apiKeyHint ?? '';
        this.lockedFields.set(res.lockedFields ?? []);
        this.sources.set(res.source ?? {});
        this.deploymentMode.set(res.deploymentMode);
        this.encryptionAvailable.set(res.encryptionAvailable);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  saveConfig(): void {
    this.saving.set(true);
    this.http.put(`${this.apiBase}/api/v1/settings/llm-configs`, {
      projectId: 'default',
      feature: this.selectedFeature(),
      provider: this.provider,
      model: this.model,
      temperature: this.temperature,
      maxTokens: this.maxTokens,
      baseUrl: this.baseUrl || null,
      apiKey: this.apiKey || null,
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.apiKey = '';
        this.snackBar.open('Configuration saved!', 'OK', { duration: 3000 });
        this.loadConfig();
      },
      error: (err) => {
        this.saving.set(false);
        this.snackBar.open(err.error?.error ?? 'Failed to save', 'Dismiss', { duration: 5000 });
      }
    });
  }

  resetConfig(): void {
    this.http.delete(`${this.apiBase}/api/v1/settings/llm-configs`, {
      params: { projectId: 'default', feature: this.selectedFeature() }
    }).subscribe({
      next: () => {
        this.snackBar.open('Reset to platform defaults', 'OK', { duration: 3000 });
        this.loadConfig();
      }
    });
  }

  // ═══════════════════════════════════════════════════════════════
  // System Prompt Methods — via Facades
  //
  // Uses ProjectsFacade.projects to resolve __system__ project,
  // then PromptsFacade for all prompt CRUD.
  // ═══════════════════════════════════════════════════════════════

  /**
   * Bootstrap: load projects, then once the __system__ project is found,
   * load its prompts and select the default feature.
   */
  private initSystemPrompts(): void {
    // Ensure projects are loaded (facade handles dedup internally)
    this.projectsFacade.loadProjects();

    // Watch for projects to be populated, then resolve __system__
    const init = effect(() => {
      const projects = this.projectsFacade.projects();
      if (!projects.length) return;

      const sys = projects.find(p => p.name === SYSTEM_PROJECT_NAME);
      if (sys) {
        this.systemProjectId = sys.id;
        // Load system prompts into the NgRx store
        this.promptsFacade.loadPrompts(sys.id);
        // After prompts load, select the current feature's prompt
        this.selectSystemPrompt(this.selectedPromptFeature());
      }
      init.destroy(); // One-shot: stop watching after init
    });
  }

  selectPromptFeature(feature: string): void {
    this.selectedPromptFeature.set(feature);
    this.selectSystemPrompt(feature);
  }

  /**
   * Finds the prompt matching the feature in the loaded prompt list,
   * then dispatches loadPrompt to get full content.
   */
  private selectSystemPrompt(feature: string): void {
    const expectedName = systemPromptName(feature);

    // Watch for the prompts list to be populated, then find the matching prompt
    const selector = effect(() => {
      const prompts = this.promptsFacade.prompts();
      if (!prompts.length) return;

      const match = prompts.find(p => p.name === expectedName);
      if (match?.id) {
        this.currentPromptId = match.id;
        this.promptsFacade.loadPrompt(match.id);
      } else {
        this.currentPromptId = null;
        this.promptContent = '';
        this.promptIsCustomized.set(false);
        this.promptVersion.set(null);
      }
      selector.destroy();
    });
  }

  /**
   * Saves the system prompt using the PromptsFacade.updatePrompt().
   */
  savePrompt(): void {
    if (!this.currentPromptId) {
      this.snackBar.open('No system prompt found to update', 'Dismiss', { duration: 4000 });
      return;
    }

    this.promptsFacade.updatePrompt(this.currentPromptId, {
      content: this.promptContent,
      changeMessage: this.promptChangeMessage || undefined,
      author: 'admin',
    });
    // The facade/effect will handle success/error notifications
    this.promptChangeMessage = '';
  }

  /**
   * Resets the prompt by rolling back to version 1 (the original seeded default).
   */
  resetPrompt(): void {
    if (!this.currentPromptId) return;

    this.promptsFacade.rollbackPrompt(this.currentPromptId, 1);
    // The facade/effect will reload the prompt after rollback
  }
}
