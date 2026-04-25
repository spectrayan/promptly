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
import { PromptsFacade } from '../../../state/prompts/prompts.facade';
import { ScannerFacade } from '../../../state/scanner/scanner.facade';
import { ImproverFacade } from '../../../state/improver/improver.facade';
import { VersionDiffComponent } from '../components/version-diff/version-diff.component';
import { CloneDialogComponent } from '../components/clone-dialog/clone-dialog.component';
import { MonacoEditorComponent } from '../../../shared/components/monaco-editor/monaco-editor.component';
import { AuthFacade } from '../../../state/auth/auth.facade';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';

@Component({
  selector: 'promptly-prompt-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe, RouterLink, FormsModule,
    MatCardModule, MatTabsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatChipsModule, MatTooltipModule,
    MatFormFieldModule, MatInputModule,
    MatProgressSpinnerModule, MatSnackBarModule, MatDialogModule,
    VersionDiffComponent, MonacoEditorComponent, EnumLabelPipe,
  ],
  templateUrl: './prompt-detail.page.html',
  styleUrl: './prompt-detail.page.scss',
})
export class PromptDetailPage implements OnInit, OnDestroy {
  readonly facade = inject(PromptsFacade);
  readonly scannerFacade = inject(ScannerFacade);
  readonly improverFacade = inject(ImproverFacade);
  readonly auth = inject(AuthFacade);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  private projectId: string | null = null;
  versionColumns = ['versionNumber', 'changeMessage', 'createdBy', 'createdAt', 'actions'];

  // ── Edit mode state ───────────────────────────────────────────
  readonly editing = signal(false);
  editContent = '';
  editChangeMessage = '';
  showDiff = false;

  // ═══════════════════════════════════════════════════════════════════
  // Business rules computed from prompt state (mirrors backend specs)
  // ═══════════════════════════════════════════════════════════════════

  readonly canEdit = computed(() => true);

  /** APPROVED prompts can only be deleted by admins */
  readonly canDelete = computed(() => {
    const p = this.facade.selected();
    if (!p) return false;
    if (p.status === 'APPROVED') {
      return this.auth.orgRole() === 'ORG_ADMIN';
    }
    return true;
  });

  readonly canSubmitReview = computed(() => {
    const p = this.facade.selected();
    return p?.status === 'DRAFT';
  });

  readonly canRollback = computed(() => {
    const p = this.facade.selected();
    return (p?.currentVersion ?? 0) > 1;
  });

  readonly canClone = computed(() => !!this.facade.selected());
  readonly canImprove = computed(() => true);

  readonly lockReason = computed(() => '');

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('projectId');
    const id = this.route.snapshot.paramMap.get('id')!;
    this.facade.loadPrompt(id);
    this.scannerFacade.loadScanResult(id);
  }

  ngOnDestroy(): void {
    this.facade.clearSelection();
    this.improverFacade.clearSuggestion();
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
      setTimeout(() => {
        if (this.projectId) {
          this.router.navigate(['/projects', this.projectId, 'prompts']);
        }
      }, 1500);
    });
  }

  onScan(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.scannerFacade.triggerScan(id);
    this.snackBar.open('Security scan triggered...', 'OK', { duration: 3000 });
  }

  // ── AI Improve ────────────────────────────────────────────────
  onImprove(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.improverFacade.improvePrompt(id);
    this.snackBar.open('Requesting AI improvement...', 'OK', { duration: 2000 });
  }

  onAcceptImprovement(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const suggestion = this.improverFacade.suggestion();
    if (!id || !suggestion?.improvedContent) return;

    this.improverFacade.applyImprovement(id, suggestion.improvedContent, 'current-user');
    this.snackBar.open('Improvement applied as new version!', 'OK', { duration: 3000 });

    // Reload prompt to reflect the new version
    setTimeout(() => this.facade.loadPrompt(id), 500);
  }

  onDismissImprovement(): void {
    this.improverFacade.clearSuggestion();
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
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId, 'prompts']);
    }
  }
}
