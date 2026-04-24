# Prompt Lifecycle Business Rules — Implementation Summary

## What Was Built

Full-stack business rule enforcement for prompt lifecycle management using the **Specification pattern** in the domain layer.

### Business Rules Matrix (Verified)

| Action | DEV | STAGING | PROD |
|--------|:---:|:-------:|:----:|
| **Edit (new version)** | ✅ | ❌ | ❌ |
| **Improve (AI)** | ✅ | ❌ | ❌ |
| **Delete** | ✅ | ❌ (disabled) | ❌ (disabled) |
| **Submit for Review** | ✅ | ❌ (disabled) | ❌ (disabled) |
| **Rollback** | ✅ (if >1 version) | ❌ | ❌ |
| **Clone** | ✅ | ✅ | ✅ |
| **Scan** | ✅ | ✅ | ✅ |

## Screenshots

````carousel
![DEV prompt (p-003) — All actions enabled: Edit, Improve, Scan, Clone, Submit for Review, Delete](/C:/Users/bhara/.gemini/antigravity/brain/96038283-45e5-42e4-9b87-3f5733f6e980/dev_prompt_p003_1776991793740.png)
<!-- slide -->
![STAGING prompt (p-001) — Lock banner shown, Edit/Improve hidden, Submit/Delete disabled, Clone enabled](/C:/Users/bhara/.gemini/antigravity/brain/96038283-45e5-42e4-9b87-3f5733f6e980/staging_prompt_p001_1776991803299.png)
<!-- slide -->
![PRODUCTION prompt (p-002) — Same locked behavior, only Clone and Scan available](/C:/Users/bhara/.gemini/antigravity/brain/96038283-45e5-42e4-9b87-3f5733f6e980/production_prompt_p002_1776991814499.png)
````

## Backend Architecture

### New Files Created
| File | Purpose |
|------|---------|
| `shared/domain/Specification.java` | Generic specification interface with `and()`, `or()`, `not()` composition |
| `prompt/domain/model/PromptSpecifications.java` | All prompt business rules as composable specifications |
| `prompt/domain/model/PromptStatus.java` | `DRAFT`, `IN_REVIEW`, `APPROVED` enum |
| `prompt/application/port/in/ClonePromptUseCase.java` | Clone use case port |

### Modified Files
| File | Change |
|------|--------|
| `Prompt.java` (aggregate) | Added `status` field, spec-based `assertDeletable()`, `assertRollbackable()`, query methods `isEditable()` etc. |
| `PromptApplicationService.java` | Implements `ClonePromptUseCase`, enforces rules via aggregate methods |
| `PromptDocument.java` | Added `status` field for persistence |
| `PromptPersistenceMapper.java` | Added `statusToString`/`stringToStatus` mappings |
| `seed-data/init.js` | Added `status` field to all prompt documents |

### Specification Pattern Usage
```java
// Business rule: editable = DEV + not in review
public static Specification<Prompt> isEditable() {
    return isInDev().and(isNotInReview());
}

// Enforced in aggregate root
public PromptVersion createNewVersion(...) {
    assertSatisfies(PromptSpecifications.isEditable(), "Cannot create new version");
    // ...
}
```

## Frontend Architecture

### Signal-based business rules in `prompt-detail.page.ts`
```typescript
readonly canEdit = computed(() => this.isInDev());
readonly canDelete = computed(() => this.isInDev());
readonly canClone = computed(() => !!this.facade.selected());
readonly lockReason = computed(() => {
    const env = p?.activeEnvironment?.toUpperCase();
    if (env === 'STAGING') return 'This prompt is in STAGING. Clone it to make changes.';
    if (env === 'PROD') return 'This prompt is in PRODUCTION. Clone it to make changes.';
    return '';
});
```

### UX Patterns
- **Lock banner**: Amber warning bar with 🔒 icon + "Clone to DEV" CTA for non-DEV prompts
- **Hidden buttons**: Edit/Improve buttons completely hidden for non-DEV
- **Disabled with tooltip**: Submit for Review / Delete shown as disabled with explanatory tooltips
- **Always available**: Clone and Scan work regardless of environment
