import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
  NgZone,
} from '@angular/core';

declare const monaco: any;

/**
 * Reusable Monaco Editor wrapper component.
 *
 * Replaces plain textareas with a full-featured code editor supporting
 * syntax highlighting, auto-completion, and language-specific formatting
 * for prompt authoring (Text, JSON, YAML, Markdown).
 *
 * Usage:
 * ```html
 * <promptly-monaco-editor
 *   [(value)]="form.content"
 *   [language]="form.contentFormat"
 *   [readOnly]="!canEdit()"
 *   [height]="'400px'"
 * />
 * ```
 */
@Component({
  selector: 'promptly-monaco-editor',
  standalone: true,
  template: `<div #editorContainer class="monaco-host" [style.height]="height"></div>`,
  styles: [`
    :host {
      display: block;
      border: 1px solid var(--mat-sys-outline-variant, #ccc);
      border-radius: var(--radius-md, 8px);
      overflow: hidden;
    }
    .monaco-host {
      width: 100%;
    }
  `],
})
export class MonacoEditorComponent implements OnInit, AfterViewInit, OnDestroy, OnChanges {
  @ViewChild('editorContainer', { static: true }) editorContainer!: ElementRef<HTMLDivElement>;

  /** The text content of the editor. */
  @Input() value = '';
  @Output() valueChange = new EventEmitter<string>();

  /** Language for syntax highlighting. Maps content format to Monaco language ID. */
  @Input() set language(format: string) {
    this._language = this.mapLanguage(format);
  }
  get language(): string { return this._language; }
  private _language = 'plaintext';

  /** Editor height (CSS value). */
  @Input() height = '400px';

  /** Read-only mode. */
  @Input() readOnly = false;

  /** Minimap visibility. */
  @Input() minimap = true;

  /** Word wrap mode. */
  @Input() wordWrap: 'off' | 'on' | 'wordWrapColumn' | 'bounded' = 'on';

  private editor: any;
  private _isSettingValue = false;

  constructor(private ngZone: NgZone) {}

  ngOnInit(): void {
    this.loadMonacoLoader();
  }

  ngAfterViewInit(): void {
    // Editor creation is deferred to loadMonacoLoader callback
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.editor) return;

    if (changes['value'] && !this._isSettingValue) {
      const current = this.editor.getValue();
      if (this.value !== current) {
        this._isSettingValue = true;
        this.editor.setValue(this.value || '');
        this._isSettingValue = false;
      }
    }

    if (changes['readOnly']) {
      this.editor.updateOptions({ readOnly: this.readOnly });
    }

    if (changes['language'] || changes['_language']) {
      const model = this.editor.getModel();
      if (model) {
        monaco.editor.setModelLanguage(model, this._language);
      }
    }
  }

  ngOnDestroy(): void {
    this.editor?.dispose();
  }

  private loadMonacoLoader(): void {
    // Check if Monaco is already loaded
    if (typeof monaco !== 'undefined') {
      this.initEditor();
      return;
    }

    // Check if the loader script is already being loaded
    if ((window as any).__monacoLoaderPromise) {
      (window as any).__monacoLoaderPromise.then(() => this.initEditor());
      return;
    }

    // Load the AMD loader
    (window as any).__monacoLoaderPromise = new Promise<void>((resolve) => {
      const script = document.createElement('script');
      script.src = 'assets/monaco-editor/min/vs/loader.js';
      script.onload = () => {
        const require = (window as any).require;
        require.config({
          paths: { vs: 'assets/monaco-editor/min/vs' },
        });
        require(['vs/editor/editor.main'], () => {
          resolve();
          this.initEditor();
        });
      };
      document.head.appendChild(script);
    });
  }

  private initEditor(): void {
    if (this.editor) return;

    // Detect theme from CSS custom properties or system preference
    const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      || document.body.classList.contains('dark-theme');

    this.ngZone.runOutsideAngular(() => {
      this.editor = monaco.editor.create(this.editorContainer.nativeElement, {
        value: this.value || '',
        language: this._language,
        theme: isDark ? 'vs-dark' : 'vs',
        readOnly: this.readOnly,
        minimap: { enabled: this.minimap },
        wordWrap: this.wordWrap,
        automaticLayout: true,
        fontSize: 14,
        lineHeight: 22,
        fontFamily: "'JetBrains Mono', 'Fira Code', 'Cascadia Code', monospace",
        fontLigatures: true,
        padding: { top: 12, bottom: 12 },
        scrollBeyondLastLine: false,
        renderLineHighlight: 'gutter',
        suggestOnTriggerCharacters: true,
        tabSize: 2,
        bracketPairColorization: { enabled: true },
        guides: {
          bracketPairs: true,
          indentation: true,
        },
      });

      // Listen for content changes
      this.editor.onDidChangeModelContent(() => {
        if (this._isSettingValue) return;
        this._isSettingValue = true;
        const newValue = this.editor.getValue();
        this.ngZone.run(() => {
          this.value = newValue;
          this.valueChange.emit(newValue);
        });
        this._isSettingValue = false;
      });
    });

    // Listen for theme changes
    const observer = new MutationObserver(() => {
      const dark = document.body.classList.contains('dark-theme');
      if (this.editor) {
        monaco.editor.setTheme(dark ? 'vs-dark' : 'vs');
      }
    });
    observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });
  }

  /**
   * Maps Promptly content format to Monaco language ID.
   */
  private mapLanguage(format: string): string {
    switch (format?.toUpperCase()) {
      case 'JSON':       return 'json';
      case 'YAML':       return 'yaml';
      case 'MARKDOWN':   return 'markdown';
      case 'TEXT':       return 'plaintext';
      default:           return 'plaintext';
    }
  }

  /** Public API: focus the editor. */
  focus(): void {
    this.editor?.focus();
  }

  /** Public API: format the document. */
  formatDocument(): void {
    this.editor?.getAction('editor.action.formatDocument')?.run();
  }
}
