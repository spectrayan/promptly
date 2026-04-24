import { Component, inject, OnInit, signal, computed } from '@angular/core';
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
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

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

@Component({
  selector: 'promptly-settings',
  imports: [
    FormsModule,
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

  readonly providers = PROVIDERS;
  readonly features = FEATURES;

  // State
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly selectedFeature = signal('global');
  readonly deploymentMode = signal('self-hosted');
  readonly encryptionAvailable = signal(false);
  readonly lockedFields = signal<string[]>([]);
  readonly sources = signal<Record<string, string>>({});

  // Form
  provider = 'gemini';
  model = 'gemini-2.5-flash';
  temperature = 0.3;
  maxTokens = 4096;
  baseUrl = '';
  apiKey = '';
  apiKeyConfigured = false;
  apiKeyHint = '';

  // Computed
  readonly availableModels = computed(() => {
    const p = this.providers.find(pr => pr.value === this.provider);
    return p?.models ?? [];
  });

  readonly selectedFeatureInfo = computed(() =>
    this.features.find(f => f.value === this.selectedFeature()) ?? this.features[0]
  );

  ngOnInit(): void {
    this.loadConfig();
  }

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
    // Use first project or a placeholder — in a real app, use active project from NgRx
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
        // Fallback — API not connected, show defaults
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
}
