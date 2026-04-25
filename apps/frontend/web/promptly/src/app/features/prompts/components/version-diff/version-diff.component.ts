import {
  Component,
  Input,
  OnChanges,
  SimpleChanges,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnDestroy,
  NgZone,
  ChangeDetectionStrategy,
} from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { VersionResponse } from '@promptly/client';

declare const monaco: any;

/**
 * Version diff component using Monaco's built-in diff editor.
 *
 * Features:
 * - Side-by-side or inline diff toggle
 * - Monaco-powered syntax-aware diffing
 * - Added/removed line stats
 * - Respects app theme (promptly-dark / promptly-light)
 */
@Component({
  selector: 'promptly-version-diff',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatCardModule, MatSelectModule, MatFormFieldModule, MatIconModule, MatButtonModule, MatTooltipModule, FormsModule],
  templateUrl: './version-diff.component.html',
  styleUrl: './version-diff.component.scss',
})
export class VersionDiffComponent implements OnChanges, AfterViewInit, OnDestroy {
  @ViewChild('diffContainer') diffContainer?: ElementRef<HTMLDivElement>;

  @Input() versions: VersionResponse[] = [];

  leftVersion: number | null = null;
  rightVersion: number | null = null;
  addedCount = 0;
  removedCount = 0;
  inline = false;

  private diffEditor: any = null;
  private _themeObserver: MutationObserver | null = null;
  private _resizeObserver: ResizeObserver | null = null;

  constructor(private ngZone: NgZone) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['versions'] && this.versions.length >= 2) {
      // Auto-select last two versions
      this.leftVersion = this.versions[this.versions.length - 2].versionNumber ?? null;
      this.rightVersion = this.versions[this.versions.length - 1].versionNumber ?? null;
      // Defer diff update so the ViewChild is available
      setTimeout(() => this.updateDiff(), 0);
    }
  }

  ngAfterViewInit(): void {
    if (this.leftVersion && this.rightVersion && this.leftVersion !== this.rightVersion) {
      this.updateDiff();
    }
  }

  ngOnDestroy(): void {
    this._themeObserver?.disconnect();
    this._resizeObserver?.disconnect();
    this.diffEditor?.dispose();
  }

  toggleLayout(): void {
    this.inline = !this.inline;
    if (this.diffEditor) {
      this.diffEditor.updateOptions({ renderSideBySide: !this.inline });
    }
  }

  updateDiff(): void {
    if (!this.leftVersion || !this.rightVersion || this.leftVersion === this.rightVersion) {
      this.destroyDiffEditor();
      this.addedCount = 0;
      this.removedCount = 0;
      return;
    }

    const left = this.versions.find(v => v.versionNumber === this.leftVersion);
    const right = this.versions.find(v => v.versionNumber === this.rightVersion);
    if (!left || !right) return;

    const leftContent = left.content || '';
    const rightContent = right.content || '';

    // Compute simple line counts for stats
    this.computeStats(leftContent, rightContent);

    // Wait for the container to be rendered
    setTimeout(() => this.createOrUpdateDiffEditor(leftContent, rightContent), 0);
  }

  private createOrUpdateDiffEditor(leftContent: string, rightContent: string): void {
    if (!this.diffContainer?.nativeElement) return;

    if (typeof monaco === 'undefined') {
      // Monaco not loaded yet — fall back gracefully
      return;
    }

    const isDark = !document.documentElement.hasAttribute('data-theme')
      || document.documentElement.getAttribute('data-theme') !== 'light';
    const theme = isDark ? 'promptly-dark' : 'promptly-light';

    if (this.diffEditor) {
      // Update existing models
      const oldModel = this.diffEditor.getModel();
      if (oldModel && oldModel.original && oldModel.modified) {
        oldModel.original.setValue(leftContent);
        oldModel.modified.setValue(rightContent);
      } else {
        const original = monaco.editor.createModel(leftContent, 'plaintext');
        const modified = monaco.editor.createModel(rightContent, 'plaintext');
        this.diffEditor.setModel({ original, modified });
      }
      return;
    }

    this.ngZone.runOutsideAngular(() => {
      const original = monaco.editor.createModel(leftContent, 'plaintext');
      const modified = monaco.editor.createModel(rightContent, 'plaintext');

      this.diffEditor = monaco.editor.createDiffEditor(this.diffContainer!.nativeElement, {
        theme,
        automaticLayout: true,
        readOnly: true,
        renderSideBySide: !this.inline,
        fontSize: 13,
        lineHeight: 20,
        fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
        scrollBeyondLastLine: false,
        minimap: { enabled: false },
        padding: { top: 8, bottom: 8 },
        renderOverviewRuler: true,
        enableSplitViewResizing: true,
        ignoreTrimWhitespace: false,
        renderSideBySideInlineBreakpoint: 300,
      });

      this.diffEditor.setModel({ original, modified });
    });

    // ── Theme syncing ──
    if (!this._themeObserver) {
      this._themeObserver = new MutationObserver(() => {
        const lightMode = document.documentElement.getAttribute('data-theme') === 'light';
        if (this.diffEditor) {
          this.diffEditor.updateOptions({
            theme: lightMode ? 'promptly-light' : 'promptly-dark',
          });
        }
      });
      this._themeObserver.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['data-theme'],
      });
    }

    // ── Responsive resize ──
    if (!this._resizeObserver) {
      this._resizeObserver = new ResizeObserver(() => {
        this.diffEditor?.layout();
      });
      this._resizeObserver.observe(this.diffContainer.nativeElement);
    }
  }

  private destroyDiffEditor(): void {
    this._resizeObserver?.disconnect();
    this._resizeObserver = null;
    this.diffEditor?.dispose();
    this.diffEditor = null;
  }

  /**
   * Compute simple line diff stats.
   */
  private computeStats(left: string, right: string): void {
    const leftLines = left.split('\n');
    const rightLines = right.split('\n');
    const lcs = this.lcs(leftLines, rightLines);
    this.removedCount = leftLines.length - lcs.length;
    this.addedCount = rightLines.length - lcs.length;
  }

  private lcs(a: string[], b: string[]): string[] {
    const m = a.length, n = b.length;
    const dp: number[][] = Array.from({ length: m + 1 }, () => Array(n + 1).fill(0));

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        dp[i][j] = a[i - 1] === b[j - 1] ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]);
      }
    }

    const result: string[] = [];
    let i = m, j = n;
    while (i > 0 && j > 0) {
      if (a[i - 1] === b[j - 1]) {
        result.unshift(a[i - 1]);
        i--; j--;
      } else if (dp[i - 1][j] > dp[i][j - 1]) {
        i--;
      } else {
        j--;
      }
    }

    return result;
  }
}
