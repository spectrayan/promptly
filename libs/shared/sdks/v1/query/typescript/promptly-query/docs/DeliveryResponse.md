
# DeliveryResponse


## Properties

Name | Type
------------ | -------------
`promptId` | string
`name` | string
`content` | string
`version` | number
`contentFormat` | string

## Example

```typescript
import type { DeliveryResponse } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "promptId": null,
  "name": null,
  "content": null,
  "version": null,
  "contentFormat": null,
} satisfies DeliveryResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as DeliveryResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


