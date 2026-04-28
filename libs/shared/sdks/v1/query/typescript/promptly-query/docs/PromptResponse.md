
# PromptResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`name` | string
`description` | string
`projectId` | string
`status` | [PromptStatus](PromptStatus.md)
`contentFormat` | [ContentFormat](ContentFormat.md)
`currentVersion` | number
`latestContent` | string
`tags` | Array&lt;string&gt;
`createdAt` | Date
`updatedAt` | Date

## Example

```typescript
import type { PromptResponse } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "name": null,
  "description": null,
  "projectId": null,
  "status": null,
  "contentFormat": null,
  "currentVersion": null,
  "latestContent": null,
  "tags": null,
  "createdAt": null,
  "updatedAt": null,
} satisfies PromptResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PromptResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


