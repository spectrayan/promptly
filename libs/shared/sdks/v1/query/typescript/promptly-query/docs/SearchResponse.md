
# SearchResponse


## Properties

Name | Type
------------ | -------------
`promptId` | string
`name` | string
`description` | string
`score` | number

## Example

```typescript
import type { SearchResponse } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "promptId": null,
  "name": null,
  "description": null,
  "score": null,
} satisfies SearchResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SearchResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


