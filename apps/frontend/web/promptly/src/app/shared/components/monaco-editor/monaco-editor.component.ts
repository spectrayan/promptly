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
  ChangeDetectionStrategy,
} from '@angular/core';

declare const monaco: any;

// ═══════════════════════════════════════════════════════════════════════
// Custom Promptly theme definitions
// ═══════════════════════════════════════════════════════════════════════

const PROMPTLY_DARK_THEME: any = {
  base: 'vs-dark',
  inherit: true,
  rules: [
    { token: 'comment',    foreground: '6b7280', fontStyle: 'italic' },
    { token: 'keyword',    foreground: 'b388ff' },
    { token: 'string',     foreground: '34d399' },
    { token: 'number',     foreground: 'fbbf24' },
    { token: 'type',       foreground: '60a5fa' },
    { token: 'variable',   foreground: 'e0e0e0' },
    { token: 'delimiter',  foreground: '9ca3af' },
    { token: 'tag',        foreground: 'b388ff' },
    { token: 'attribute',  foreground: '60a5fa' },
    { token: 'key',        foreground: 'b388ff' },
    { token: 'string.key', foreground: '60a5fa' },
  ],
  colors: {
    'editor.background':                '#1c1b1f',
    'editor.foreground':                '#e0e0e0',
    'editor.lineHighlightBackground':   '#2a2930',
    'editor.selectionBackground':       '#7c4dff33',
    'editor.inactiveSelectionBackground': '#7c4dff1a',
    'editorCursor.foreground':          '#b388ff',
    'editorLineNumber.foreground':      '#4a4458',
    'editorLineNumber.activeForeground': '#b388ff',
    'editorIndentGuide.background':     '#2a2930',
    'editorIndentGuide.activeBackground': '#4a4458',
    'editorBracketMatch.background':    '#7c4dff22',
    'editorBracketMatch.border':        '#7c4dff66',
    'editor.findMatchBackground':       '#7c4dff44',
    'editor.findMatchHighlightBackground': '#7c4dff22',
    'editorWidget.background':          '#211f26',
    'editorWidget.border':              '#3d3b44',
    'editorSuggestWidget.background':   '#211f26',
    'editorSuggestWidget.border':       '#3d3b44',
    'editorSuggestWidget.selectedBackground': '#2a2930',
    'editorOverviewRuler.border':       '#2a2930',
    'scrollbarSlider.background':       '#4a445844',
    'scrollbarSlider.hoverBackground':  '#4a445888',
    'scrollbarSlider.activeBackground': '#7c4dff66',
    'minimap.background':               '#1c1b1f',
  },
};

const PROMPTLY_LIGHT_THEME: any = {
  base: 'vs',
  inherit: true,
  rules: [
    { token: 'comment',    foreground: '6b7280', fontStyle: 'italic' },
    { token: 'keyword',    foreground: '7c4dff' },
    { token: 'string',     foreground: '059669' },
    { token: 'number',     foreground: 'd97706' },
    { token: 'type',       foreground: '2563eb' },
    { token: 'variable',   foreground: '1f2937' },
    { token: 'delimiter',  foreground: '6b7280' },
    { token: 'tag',        foreground: '7c4dff' },
    { token: 'attribute',  foreground: '2563eb' },
    { token: 'key',        foreground: '7c4dff' },
    { token: 'string.key', foreground: '2563eb' },
  ],
  colors: {
    'editor.background':                '#fffbfe',
    'editor.foreground':                '#1c1b1f',
    'editor.lineHighlightBackground':   '#f3edf7',
    'editor.selectionBackground':       '#7c4dff22',
    'editor.inactiveSelectionBackground': '#7c4dff0d',
    'editorCursor.foreground':          '#7c4dff',
    'editorLineNumber.foreground':      '#c4b5d0',
    'editorLineNumber.activeForeground': '#7c4dff',
    'editorIndentGuide.background':     '#f3edf7',
    'editorIndentGuide.activeBackground': '#c4b5d0',
    'editorBracketMatch.background':    '#7c4dff18',
    'editorBracketMatch.border':        '#7c4dff44',
    'editor.findMatchBackground':       '#7c4dff33',
    'editor.findMatchHighlightBackground': '#7c4dff18',
    'editorWidget.background':          '#fffbfe',
    'editorWidget.border':              '#e0dce4',
    'editorSuggestWidget.background':   '#fffbfe',
    'editorSuggestWidget.border':       '#e0dce4',
    'editorSuggestWidget.selectedBackground': '#f3edf7',
    'editorOverviewRuler.border':       '#e0dce4',
    'scrollbarSlider.background':       '#c4b5d044',
    'scrollbarSlider.hoverBackground':  '#c4b5d088',
    'scrollbarSlider.activeBackground': '#7c4dff66',
    'minimap.background':               '#fffbfe',
  },
};

/**
 * Reusable Monaco Editor wrapper component.
 *
 * Features:
 * - Custom Promptly dark/light themes synced with the app's `data-theme`
 * - Auto-language mapping (TEXT → plaintext, JSON, YAML, Markdown)
 * - Responsive layout via ResizeObserver
 * - Bracket pair colorization, font ligatures, sticky scroll
 * - Public API: focus(), formatDocument(), undo(), redo()
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
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './monaco-editor.component.html',
  styleUrls: ['./monaco-editor.component.scss'],
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

  /** Human-readable label for the toolbar badge. */
  get languageLabel(): string {
    const labels: Record<string, string> = {
      plaintext: 'Text',
      json: 'JSON',
      yaml: 'YAML',
      markdown: 'Markdown',
    };
    return labels[this._language] ?? this._language;
  }

  /** Editor height (CSS value). */
  @Input() height = '400px';

  /** Read-only mode. */
  @Input() readOnly = false;

  /** Minimap visibility. */
  @Input() minimap = true;

  /** Word wrap mode. */
  @Input() wordWrap: 'off' | 'on' | 'wordWrapColumn' | 'bounded' = 'on';

  /** Placeholder text shown when editor is empty. */
  @Input() placeholder = '';

  private editor: any;
  private _isSettingValue = false;
  private _themeObserver: MutationObserver | null = null;
  private _resizeObserver: ResizeObserver | null = null;
  private static _themesRegistered = false;

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
    this._themeObserver?.disconnect();
    this._resizeObserver?.disconnect();
    this.editor?.dispose();
  }

  // ═══════════════════════════════════════════════════════════════════
  // Monaco Loader & Initialization
  // ═══════════════════════════════════════════════════════════════════

  private loadMonacoLoader(): void {
    // Check if Monaco is already loaded
    if (typeof monaco !== 'undefined') {
      this.registerThemesOnce();
      this.initEditor();
      return;
    }

    // Check if the loader script is already being loaded
    if ((window as any).__monacoLoaderPromise) {
      (window as any).__monacoLoaderPromise.then(() => {
        this.registerThemesOnce();
        this.initEditor();
      });
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
          this.registerThemesOnce();
          resolve();
          this.initEditor();
        });
      };
      document.head.appendChild(script);
    });
  }

  /**
   * Register custom Promptly themes once globally.
   */
  private registerThemesOnce(): void {
    if (MonacoEditorComponent._themesRegistered) return;
    monaco.editor.defineTheme('promptly-dark', PROMPTLY_DARK_THEME);
    monaco.editor.defineTheme('promptly-light', PROMPTLY_LIGHT_THEME);
    MonacoEditorComponent._themesRegistered = true;
  }

  private initEditor(): void {
    if (this.editor) return;

    const isDark = !document.documentElement.hasAttribute('data-theme')
      || document.documentElement.getAttribute('data-theme') !== 'light';

    this.ngZone.runOutsideAngular(() => {
      this.editor = monaco.editor.create(this.editorContainer.nativeElement, {
        value: this.value || '',
        language: this._language,
        theme: isDark ? 'promptly-dark' : 'promptly-light',
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
        stickyScroll: { enabled: false },
        smoothScrolling: true,
        cursorBlinking: 'smooth',
        cursorSmoothCaretAnimation: 'on',
        roundedSelection: true,
        renderWhitespace: 'selection',
        contextmenu: true,
        folding: true,
        foldingHighlight: true,
        showFoldingControls: 'mouseover',
        links: true,
        colorDecorators: true,
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

    // ── Theme syncing via data-theme attribute ──
    this._themeObserver = new MutationObserver(() => {
      const lightMode = document.documentElement.getAttribute('data-theme') === 'light';
      if (this.editor) {
        monaco.editor.setTheme(lightMode ? 'promptly-light' : 'promptly-dark');
      }
    });
    this._themeObserver.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['data-theme'],
    });

    // ── Responsive resize ──
    this._resizeObserver = new ResizeObserver(() => {
      this.editor?.layout();
    });
    this._resizeObserver.observe(this.editorContainer.nativeElement);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Language Mapping
  // ═══════════════════════════════════════════════════════════════════

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

  // ═══════════════════════════════════════════════════════════════════
  // Public API
  // ═══════════════════════════════════════════════════════════════════

  /** Focus the editor. */
  focus(): void {
    this.editor?.focus();
  }

  /** Format the document using the active language formatter. */
  formatDocument(): void {
    this.editor?.getAction('editor.action.formatDocument')?.run();
  }

  /** Undo the last edit. */
  undo(): void {
    this.editor?.trigger('component', 'undo', null);
  }

  /** Redo the last undo. */
  redo(): void {
    this.editor?.trigger('component', 'redo', null);
  }
}
