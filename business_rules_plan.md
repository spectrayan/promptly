# Prompt Lifecycle Business Rules — Implementation Plan

## Business Rules Matrix

| Action | DEV | STAGING | PROD | IN_REVIEW (pending workflow) |
|--------|-----|---------|------|------------------------------|
| **Edit (new version)** | ✅ | ❌ | ❌ | ❌ |
| **Clone** | ✅ | ✅ | ✅ | ✅ |
| **Delete** | ✅ | ❌ | ❌ | ❌ |
| **Submit for Review** | ✅ | ❌ | ❌ | ❌ (already submitted) |
| **Rollback** | ✅ | ❌ | ❌ | ❌ |
| **Scan** | ✅ | ✅ | ✅ | ✅ |
| **Improve (AI)** | ✅ | ❌ | ❌ | ❌ |

## Architecture

### Backend (Specification Pattern)

1. **`PromptSpecification`** — base interface in `shared.domain`
2. **Concrete specs** in `prompt.domain.model`:
   - `PromptIsEditable` — env == DEV && no pending workflow
   - `PromptIsDeletable` — env == DEV && no pending workflow
   - `PromptIsSubmittable` — env == DEV && no pending workflow
   - `PromptIsRollbackable` — env == DEV && no pending workflow
3. **`PromptLifecyclePolicy`** — domain service composing specs
4. **Application service** enforces rules before mutations

### Frontend (Signal-based)

1. Compute `canEdit`, `canDelete`, `canSubmitReview`, `canRollback`, `canClone` as signals
2. Disable/hide buttons accordingly in prompt-detail template
3. Add "Edit Prompt" and "Clone Prompt" actions

## Files to Create/Modify

### Backend (6 new, 2 modified)
- `shared/domain/Specification.java` — generic spec interface
- `prompt/domain/model/PromptSpecifications.java` — all prompt specs
- `prompt/domain/model/PromptStatus.java` — enum (DRAFT, IN_REVIEW, APPROVED, DEPLOYED)
- `prompt/application/service/PromptApplicationService.java` — enforce rules
- `prompt/infrastructure/web/PromptController.java` — return status info

### Frontend (3 modified)
- `prompt-detail.page.html` — edit/clone/disable actions
- `prompt-detail.page.ts` — canEdit/canClone signals
- `prompt-detail.page.scss` — disabled state styling
