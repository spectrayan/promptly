
# ProjectResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`name` | string
`description` | string
`tags` | Array&lt;string&gt;
`memberCount` | number
`promptCount` | number
`createdBy` | string
`createdAt` | Date
`updatedAt` | Date

## Example

```typescript
import type { ProjectResponse } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "id": proj-001,
  "name": customer-ops,
  "description": Customer operations AI prompts,
  "tags": [support, nlp],
  "memberCount": 3,
  "promptCount": 2,
  "createdBy": usr-001,
  "createdAt": 2025-06-01T08:00:00Z,
  "updatedAt": 2026-04-01T10:00:00Z,
} satisfies ProjectResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ProjectResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


