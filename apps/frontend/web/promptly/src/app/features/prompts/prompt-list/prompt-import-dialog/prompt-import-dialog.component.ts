import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ExportManifest, PromptBundle } from '@promptly/client';
import { ExchangeService } from '@promptly/client/api/exchange.service';

@Component({
  selector: 'promptly-prompt-import-dialog',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './prompt-import-dialog.component.html',
  styleUrl: './prompt-import-dialog.component.scss',
})
export class PromptImportDialog {
  private readonly dialogRef = inject(MatDialogRef<PromptImportDialog>);
  private readonly data = inject<{ projectId: string }>(MAT_DIALOG_DATA);
  private readonly exchangeService = inject(ExchangeService);
  private readonly snackBar = inject(MatSnackBar);

  readonly isDragging = signal(false);
  readonly parsedManifest = signal<ExportManifest | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging.set(false);
    this.errorMessage.set(null);
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFile(files[0]);
    }
  }

  onFileSelected(event: Event) {
    this.errorMessage.set(null);
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  private handleFile(file: File) {
    if (!file.name.endsWith('.json')) {
      this.errorMessage.set('Only JSON manifest files are supported.');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const text = e.target?.result as string;
        const manifest = JSON.parse(text) as ExportManifest;
        
        // Basic validation
        if (!manifest.prompts || !Array.isArray(manifest.prompts)) {
          throw new Error('Invalid manifest structure: missing prompts array.');
        }

        // Force projectId mapping if needed, though usually backend handles it or we map it
        manifest.prompts.forEach((p: PromptBundle) => p.projectId = this.data.projectId);

        this.parsedManifest.set(manifest);
      } catch (err: any) {
        this.errorMessage.set(`Failed to parse file: ${err.message}`);
      }
    };
    reader.onerror = () => this.errorMessage.set('Error reading file.');
    reader.readAsText(file);
  }

  clearFile() {
    this.parsedManifest.set(null);
    this.errorMessage.set(null);
  }

  confirmImport() {
    const manifest = this.parsedManifest();
    if (!manifest) return;

    this.isLoading.set(true);
    this.exchangeService.importManifest({ exportManifest: manifest }).subscribe({
      next: (result: any) => {
        this.isLoading.set(false);
        this.snackBar.open(`Successfully imported ${result.imported} prompts.`, 'OK', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err: any) => {
        this.isLoading.set(false);
        const errMsg = err.error?.error || 'Import failed.';
        this.errorMessage.set(`Backend error: ${errMsg}`);
      }
    });
  }
}
