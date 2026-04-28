
# ProblemDetails

RFC 9457 Problem Details for HTTP APIs — Standard error response format. Extension members (code, timestamp, traceId, errors) are permitted per §3.1.

## Properties

Name | Type
------------ | -------------
`type` | string
`title` | string
`status` | number
`detail` | string
`instance` | string
`code` | string
`timestamp` | Date
`traceId` | string
`errors` | [Array&lt;ValidationError&gt;](ValidationError.md)

## Example

```typescript
import type { ProblemDetails } from '@promptly/query'

// TODO: Update the object below with actual values
const example = {
  "type": https://promptly.dev/errors/conflict,
  "title": Duplicate Resource,
  "status": 409,
  "detail": A prompt named 'Welcome' already exists in this project,
  "instance": /api/v1/prompts,
  "code": PROMPT_DUPLICATE_NAME,
  "timestamp": 2026-04-25T12:00:00.000Z,
  "traceId": a1b2c3d4-e5f6-7890-abcd-ef1234567890,
  "errors": null,
} satisfies ProblemDetails

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ProblemDetails
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


