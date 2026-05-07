import { Component, ChangeDetectionStrategy, inject, computed, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe, NgTemplateOutlet } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { ScannerFacade } from '../../state/scanner/scanner.facade';
import { PromptsFacade } from '../../state/prompts/prompts.facade';
import { ScanResponse, FindingResponse } from '@promptly/client';

@Component({
  selector: 'promptly-scan-result-detail',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DatePipe, RouterLink, NgTemplateOutlet,
    MatCardModule, MatButtonModule, MatIconModule,
    MatTooltipModule, MatDividerModule, MatChipsModule,
  ],
  templateUrl: './scan-result-detail.page.html',
  styleUrl: './scan-result-detail.page.scss',
})
export class ScanResultDetailPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly scannerFacade = inject(ScannerFacade);
  readonly promptsFacade = inject(PromptsFacade);

  private projectId = '';
  private scanId = '';

  /** Resolved scan — first try the scans list, then fall back to currentScan */
  readonly scan = computed<ScanResponse | null>(() => {
    const allScans = this.scannerFacade.scans();
    const found = allScans.find(s => s.id === this.scanId);
    if (found) return found;
    const current = this.scannerFacade.currentScan();
    return current?.id === this.scanId ? current : null;
  });

  readonly promptName = computed(() => {
    const s = this.scan();
    if (!s?.promptId) return 'Unknown';
    const prompt = this.promptsFacade.prompts().find(p => p.id === s.promptId);
    if (prompt?.name) return prompt.name;
    // Truncate long MongoDB ObjectIds
    return s.promptId.length > 12 ? s.promptId.substring(0, 8) + '…' : s.promptId;
  });

  /** Group findings by severity for visual organization */
  readonly criticalFindings = computed(() => this.filterBySeverity('CRITICAL'));
  readonly highFindings = computed(() => this.filterBySeverity('HIGH'));
  readonly mediumFindings = computed(() => this.filterBySeverity('MEDIUM'));
  readonly lowFindings = computed(() => this.filterBySeverity('LOW'));

  readonly hasFindings = computed(() => (this.scan()?.findings?.length ?? 0) > 0);

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('projectId') ?? '';
    this.scanId = this.route.snapshot.paramMap.get('scanId') ?? '';

    // Ensure scans are loaded (in case of direct navigation)
    if (!this.scannerFacade.scans().length) {
      this.scannerFacade.loadAllScans(this.projectId || undefined);
    }
    this.promptsFacade.loadPrompts(this.projectId || undefined);
  }

  goBack(): void {
    this.router.navigate(['/projects', this.projectId, 'scanner']);
  }

  /** Navigate to prompt detail in edit mode with remediation suggestions */
  fixInEditor(finding?: FindingResponse): void {
    const s = this.scan();
    if (!s?.promptId) return;

    const queryParams: Record<string, string> = { editMode: 'true' };
    if (finding?.remediation) {
      queryParams['suggestion'] = finding.remediation;
    }

    this.router.navigate(
      ['/projects', this.projectId, 'prompts', s.promptId],
      { queryParams }
    );
  }

  /** Navigate to prompt detail in edit mode with ALL remediations combined */
  fixAllInEditor(): void {
    const s = this.scan();
    if (!s?.promptId) return;

    const allRemediations = (s.findings ?? [])
      .filter(f => f.remediation)
      .map((f, i) => `${i + 1}. [${f.severity}] ${f.title}: ${f.remediation}`)
      .join('\n');

    this.router.navigate(
      ['/projects', this.projectId, 'prompts', s.promptId],
      { queryParams: { editMode: 'true', suggestion: allRemediations } }
    );
  }

  getSeverityIcon(severity?: string): string {
    switch (severity) {
      case 'CRITICAL': return 'dangerous';
      case 'HIGH': return 'warning';
      case 'MEDIUM': return 'info';
      case 'LOW': return 'check_circle';
      default: return 'help';
    }
  }

  private filterBySeverity(severity: string): FindingResponse[] {
    return (this.scan()?.findings ?? []).filter(f => f.severity === severity);
  }
}
