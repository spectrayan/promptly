
# VersionResponse


## Properties

Name | Type
------------ | -------------
`versionNumber` | number
`content` | string
`changeMessage` | string
`createdBy` | string
`createdAt` | Date

## Example

```typescript
import type { VersionResponse } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "versionNumber": null,
  "content": null,
  "changeMessage": null,
  "createdBy": null,
  "createdAt": null,
} satisfies VersionResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as VersionResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


