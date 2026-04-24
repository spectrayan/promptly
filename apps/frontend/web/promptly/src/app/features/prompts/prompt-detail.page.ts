import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, inject, computed, signal } from '@angular/core';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { PromptsFacade } from '../../state/prompts/prompts.facade';
import { ScannerFacade } from '../../state/scanner/scanner.facade';
import { VersionDiffComponent } from './version-diff.component';
import { CloneDialogComponent } from './clone-dialog.component';
import { MonacoEditorComponent } from '../../shared/components/monaco-editor/monaco-editor.component';

@Component({
  selector: 'promptly-prompt-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe, RouterLink, FormsModule,
    MatCardModule, MatTabsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatChipsModule, MatTooltipModule,
    MatFormFieldModule, MatInputModule,
    MatProgressSpinnerModule, MatSnackBarModule, MatDialogModule,
    VersionDiffComponent, MonacoEditorComponent,
  ],
  templateUrl: './prompt-detail.page.html',
  styleUrl: './prompt-detail.page.scss',
})
export class PromptDetailPage implements OnInit, OnDestroy {
  readonly facade = inject(PromptsFacade);
  readonly scannerFacade = inject(ScannerFacade);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  versionColumns = ['versionNumber', 'changeMessage', 'createdBy', 'createdAt', 'actions'];

  // ── Edit mode state ───────────────────────────────────────────
  readonly editing = signal(false);
  editContent = '';
  editChangeMessage = '';

  // ═══════════════════════════════════════════════════════════════════
  // Business rules computed from prompt state (mirrors backend specs)
  // ═══════════════════════════════════════════════════════════════════

  readonly isInDev = computed(() => {
    const p = this.facade.selected();
    return p?.activeEnvironment?.toUpperCase() === 'DEV';
  });

  readonly canEdit = computed(() => this.isInDev());
  readonly canDelete = computed(() => this.isInDev());
  readonly canSubmitReview = computed(() => this.isInDev());

  readonly canRollback = computed(() => {
    const p = this.facade.selected();
    return this.isInDev() && (p?.currentVersion ?? 0) > 1;
  });

  readonly canClone = computed(() => !!this.facade.selected());
  readonly canImprove = computed(() => this.isInDev());

  readonly lockReason = computed(() => {
    const p = this.facade.selected();
    if (!p) return '';
    const env = p.activeEnvironment?.toUpperCase();
    if (env === 'STAGING') return 'This prompt is in STAGING. Clone it to make changes.';
    if (env === 'PRODUCTION') return 'This prompt is in PRODUCTION. Clone it to make changes.';
    return '';
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.facade.loadPrompt(id);
    this.scannerFacade.loadScanResult(id);
  }

  ngOnDestroy(): void {
    this.facade.clearSelection();
  }

  copyId(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    navigator.clipboard.writeText(id).then(() => {
      this.snackBar.open('Prompt ID copied to clipboard!', 'OK', { duration: 2000 });
    });
  }

  // ── Edit ───────────────────────────────────────────────────────
  onEdit(): void {
    const p = this.facade.selected();
    if (!p) return;
    this.editContent = p.latestContent ?? '';
    this.editChangeMessage = '';
    this.editing.set(true);
  }

  cancelEdit(): void {
    this.editing.set(false);
    this.editContent = '';
    this.editChangeMessage = '';
  }

  saveEdit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    if (!this.editContent.trim()) return;

    this.facade.updatePrompt(id, {
      content: this.editContent,
      changeMessage: this.editChangeMessage || 'Updated via UI',
      author: 'current-user',
    });

    this.editing.set(false);
    this.snackBar.open('New version saved!', 'OK', { duration: 3000 });

    // Reload to pick up the new version
    setTimeout(() => this.facade.loadPrompt(id), 500);
  }

  // ── Clone ─────────────────────────────────────────────────────
  onClone(): void {
    const p = this.facade.selected();
    if (!p) return;

    const dialogRef = this.dialog.open(CloneDialogComponent, {
      width: '480px',
      data: {
        sourceName: p.name,
        sourceProject: p.projectId,
        sourceDescription: p.description,
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) return;
      this.facade.createPrompt({
        name: result.name,
        description: result.description,
        projectId: result.projectId ?? p.projectId!,
        contentFormat: (p.contentFormat as any) ?? 'TEXT',
        content: p.latestContent ?? '',
        author: 'current-user',
      });
      this.snackBar.open(`Cloned as "${result.name}" — redirecting...`, 'OK', { duration: 3000 });
      setTimeout(() => this.router.navigate(['/prompts']), 1500);
    });
  }

  onScan(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.scannerFacade.triggerScan(id);
    this.snackBar.open('Security scan triggered...', 'OK', { duration: 3000 });
  }

  onImprove(): void {
    this.snackBar.open('Improvement request sent to Gemini...', 'OK', { duration: 3000 });
  }

  onRollback(version: number): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.facade.rollbackPrompt(id, version);
    this.snackBar.open(`Rolled back to v${version}`, 'OK', { duration: 3000 });
    setTimeout(() => this.facade.loadPrompt(id), 500);
  }

  onSubmitReview(): void {
    this.snackBar.open('Use the Workflows page to submit for review', 'OK', { duration: 3000 });
  }

  onDelete(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.facade.deletePrompt(id);
    this.router.navigate(['/prompts']);
  }
}
