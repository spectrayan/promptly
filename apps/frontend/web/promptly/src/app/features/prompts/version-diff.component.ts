import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { VersionResponse } from '@promptly/client';

interface DiffLine {
  type: 'added' | 'removed' | 'unchanged';
  text: string;
  lineNum?: number;
}

@Component({
  selector: 'promptly-version-diff',
  imports: [MatCardModule, MatSelectModule, MatFormFieldModule, MatIconModule, FormsModule],
  template: `
    <mat-card class="diff-card">
      <mat-card-header>
        <mat-card-title>
          <mat-icon>compare_arrows</mat-icon>
          Version Diff
        </mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="diff-selectors">
          <mat-form-field appearance="outline" subscriptSizing="dynamic" class="diff-select">
            <mat-label>From</mat-label>
            <mat-select [(ngModel)]="leftVersion" (selectionChange)="computeDiff()">
              @for (v of versions; track v.versionNumber) {
                <mat-option [value]="v.versionNumber">v{{ v.versionNumber }} — {{ v.changeMessage ?? 'Initial' }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-icon class="diff-arrow">arrow_forward</mat-icon>

          <mat-form-field appearance="outline" subscriptSizing="dynamic" class="diff-select">
            <mat-label>To</mat-label>
            <mat-select [(ngModel)]="rightVersion" (selectionChange)="computeDiff()">
              @for (v of versions; track v.versionNumber) {
                <mat-option [value]="v.versionNumber">v{{ v.versionNumber }} — {{ v.changeMessage ?? 'Initial' }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
        </div>

        @if (diffLines.length > 0) {
          <div class="diff-stats">
            <span class="diff-added">+{{ addedCount }} added</span>
            <span class="diff-removed">−{{ removedCount }} removed</span>
          </div>

          <div class="diff-viewer">
            @for (line of diffLines; track $index) {
              <div class="diff-line" [class]="'diff-' + line.type">
                <span class="diff-gutter">{{ line.type === 'added' ? '+' : line.type === 'removed' ? '−' : ' ' }}</span>
                <span class="diff-text">{{ line.text }}</span>
              </div>
            }
          </div>
        } @else if (leftVersion && rightVersion && leftVersion !== rightVersion) {
          <div class="diff-identical">
            <mat-icon>check_circle</mat-icon>
            <span>Versions are identical</span>
          </div>
        } @else {
          <div class="diff-hint">Select two versions to compare</div>
        }
      </mat-card-content>
    </mat-card>
  `,
  styles: [`
    .diff-card {
      margin-top: 16px;

      mat-card-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;

        mat-icon { color: var(--accent, #7c4dff); }
      }
    }

    .diff-selectors {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
    }

    .diff-select { flex: 1; }

    .diff-arrow {
      color: var(--text-secondary, #888);
      flex-shrink: 0;
    }

    .diff-stats {
      display: flex;
      gap: 16px;
      font-size: 13px;
      font-weight: 600;
      margin-bottom: 12px;
    }

    .diff-added { color: #4caf50; }
    .diff-removed { color: #f44336; }

    .diff-viewer {
      border: 1px solid var(--border, rgba(255,255,255,0.1));
      border-radius: 8px;
      overflow: hidden;
      font-family: 'JetBrains Mono', 'Fira Code', monospace;
      font-size: 12px;
      line-height: 1.6;
      max-height: 400px;
      overflow-y: auto;
    }

    .diff-line {
      display: flex;
      padding: 2px 12px;
      white-space: pre-wrap;
      word-break: break-word;
    }

    .diff-added-bg { background: rgba(76, 175, 80, 0.08); }
    .diff-removed-bg { background: rgba(244, 67, 54, 0.08); }

    .diff-line.diff-added {
      background: rgba(76, 175, 80, 0.1);
      border-left: 3px solid #4caf50;
    }

    .diff-line.diff-removed {
      background: rgba(244, 67, 54, 0.1);
      border-left: 3px solid #f44336;
    }

    .diff-line.diff-unchanged {
      border-left: 3px solid transparent;
      opacity: 0.6;
    }

    .diff-gutter {
      width: 20px;
      flex-shrink: 0;
      text-align: center;
      font-weight: 700;
      user-select: none;
    }

    .diff-text { flex: 1; }

    .diff-identical, .diff-hint {
      text-align: center;
      padding: 24px;
      color: var(--text-secondary, #888);
      font-size: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;

      mat-icon { color: #4caf50; }
    }
  `],
})
export class VersionDiffComponent implements OnChanges {
  @Input() versions: VersionResponse[] = [];

  leftVersion: number | null = null;
  rightVersion: number | null = null;
  diffLines: DiffLine[] = [];
  addedCount = 0;
  removedCount = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['versions'] && this.versions.length >= 2) {
      // Auto-select last two versions
      this.leftVersion = this.versions[this.versions.length - 2].versionNumber ?? null;
      this.rightVersion = this.versions[this.versions.length - 1].versionNumber ?? null;
      this.computeDiff();
    }
  }

  computeDiff(): void {
    if (!this.leftVersion || !this.rightVersion || this.leftVersion === this.rightVersion) {
      this.diffLines = [];
      this.addedCount = 0;
      this.removedCount = 0;
      return;
    }

    const left = this.versions.find(v => v.versionNumber === this.leftVersion);
    const right = this.versions.find(v => v.versionNumber === this.rightVersion);
    if (!left || !right) return;

    const leftLines = (left.content || '').split('\n');
    const rightLines = (right.content || '').split('\n');

    this.diffLines = this.computeSimpleDiff(leftLines, rightLines);
    this.addedCount = this.diffLines.filter(l => l.type === 'added').length;
    this.removedCount = this.diffLines.filter(l => l.type === 'removed').length;
  }

  /**
   * Simple line-by-line diff (LCS-based).
   */
  private computeSimpleDiff(left: string[], right: string[]): DiffLine[] {
    const lcs = this.longestCommonSubsequence(left, right);
    const result: DiffLine[] = [];
    let li = 0, ri = 0, ci = 0;

    while (li < left.length || ri < right.length) {
      if (ci < lcs.length && li < left.length && left[li] === lcs[ci] &&
          ri < right.length && right[ri] === lcs[ci]) {
        result.push({ type: 'unchanged', text: lcs[ci] });
        li++; ri++; ci++;
      } else if (li < left.length && (ci >= lcs.length || left[li] !== lcs[ci])) {
        result.push({ type: 'removed', text: left[li] });
        li++;
      } else if (ri < right.length) {
        result.push({ type: 'added', text: right[ri] });
        ri++;
      }
    }

    return result;
  }

  private longestCommonSubsequence(a: string[], b: string[]): string[] {
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
