
# PromptSummaryResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`name` | string
`description` | string
`projectId` | string
`status` | [PromptStatus](PromptStatus.md)
`currentVersion` | number
`updatedAt` | Date

## Example

```typescript
import type { PromptSummaryResponse } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "name": null,
  "description": null,
  "projectId": null,
  "status": null,
  "currentVersion": null,
  "updatedAt": null,
} satisfies PromptSummaryResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PromptSummaryResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


