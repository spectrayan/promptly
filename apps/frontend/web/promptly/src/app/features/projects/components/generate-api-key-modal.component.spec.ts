import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { GenerateApiKeyModalComponent } from './generate-api-key-modal.component';
import { ProjectsService, ApiKeyCreatedResponse } from '@promptly/client';

describe('GenerateApiKeyModalComponent', () => {
  let fixture: ComponentFixture<GenerateApiKeyModalComponent>;
  let component: GenerateApiKeyModalComponent;
  let mockProjectsService: { createProjectApiKey: ReturnType<typeof vi.fn> };
  let mockDialogRef: { close: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    mockProjectsService = {
      createProjectApiKey: vi.fn(),
    };
    mockDialogRef = {
      close: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [GenerateApiKeyModalComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ProjectsService, useValue: mockProjectsService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: { projectId: 'test-proj-1' } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GenerateApiKeyModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('initializes with default values and invalid form', () => {
    expect(component.form.valid).toBe(false);
    expect(component.form.get('name')?.value).toBe('');
    expect(component.form.get('expiresInDays')?.value).toBe(90);
    expect(component.createdKey()).toBeNull();
  });

  it('validates required name field', () => {
    const nameControl = component.form.get('name');
    nameControl?.setValue('my-agent-key');
    expect(component.form.valid).toBe(true);

    nameControl?.setValue('');
    expect(component.form.valid).toBe(false);
  });

  it('calls createProjectApiKey on valid submit and updates createdKey signal', () => {
    const mockResponse: ApiKeyCreatedResponse = {
      id: 'key-1',
      name: 'prod-key',
      prefix: 'prk_live_abcd',
      apiKey: 'prk_live_abcd1234567890abcdef',
      projectId: 'test-proj-1',
      createdAt: '2026-09-15T00:00:00Z',
      expiresAt: '2026-12-15T00:00:00Z',
      revoked: false,
    };

    mockProjectsService.createProjectApiKey.mockReturnValue(of(mockResponse));

    component.form.patchValue({ name: 'prod-key', expiresInDays: 90 });
    component.onSubmit();

    expect(mockProjectsService.createProjectApiKey).toHaveBeenCalledWith({
      projectId: 'test-proj-1',
      createApiKeyRequest: {
        name: 'prod-key',
        expiresInDays: 90,
      },
    });

    expect(component.createdKey()).toEqual(mockResponse);
    expect(component.loading()).toBe(false);
  });

  it('handles error gracefully when createProjectApiKey fails', () => {
    mockProjectsService.createProjectApiKey.mockReturnValue(
      throwError(() => new Error('Server error'))
    );

    component.form.patchValue({ name: 'fail-key' });
    component.onSubmit();

    expect(component.loading()).toBe(false);
    expect(component.createdKey()).toBeNull();
  });

  it('closes dialog with created key on onClose', () => {
    const mockResponse: ApiKeyCreatedResponse = {
      id: 'key-1',
      name: 'prod-key',
      prefix: 'prk_live_abcd',
      apiKey: 'prk_live_abcd1234567890abcdef',
      projectId: 'test-proj-1',
      createdAt: '2026-09-15T00:00:00Z',
      expiresAt: '2026-12-15T00:00:00Z',
      revoked: false,
    };
    component.createdKey.set(mockResponse);

    component.onClose();

    expect(mockDialogRef.close).toHaveBeenCalledWith(mockResponse);
  });
});
