# PromptlyQueryApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deliverPrompt**](PromptlyQueryApi.md#deliverprompt) | **GET** /api/v1/deliver | Deliver prompt to AI agent |
| [**getProject**](PromptlyQueryApi.md#getproject) | **GET** /api/v1/projects/{id} | Get project by ID |
| [**getPrompt**](PromptlyQueryApi.md#getprompt) | **GET** /api/v1/prompts/{id} | Get prompt details |
| [**getSpecificVersion**](PromptlyQueryApi.md#getspecificversion) | **GET** /api/v1/prompts/{id}/versions/{versionNumber} | Get a specific version |
| [**getVersionHistory**](PromptlyQueryApi.md#getversionhistory) | **GET** /api/v1/prompts/{id}/versions | Get version history |
| [**listProjects**](PromptlyQueryApi.md#listprojects) | **GET** /api/v1/projects | List all projects |
| [**listPrompts**](PromptlyQueryApi.md#listprompts) | **GET** /api/v1/prompts | List all prompts |
| [**searchPrompts**](PromptlyQueryApi.md#searchprompts) | **GET** /api/v1/search | Semantic search for prompts |



## deliverPrompt

> DeliveryResponse deliverPrompt(appId, usecase, agent)

Deliver prompt to AI agent

Low-latency endpoint for AI agents to fetch the latest active prompt by application ID and optional use case / agent identifiers. 

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { DeliverPromptRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const api = new PromptlyQueryApi();

  const body = {
    // string | Application/project identifier
    appId: appId_example,
    // string | Use case filter (optional)
    usecase: usecase_example,
    // string | Agent identifier filter (optional)
    agent: agent_example,
  } satisfies DeliverPromptRequest;

  try {
    const data = await api.deliverPrompt(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **appId** | `string` | Application/project identifier | [Defaults to `undefined`] |
| **usecase** | `string` | Use case filter | [Optional] [Defaults to `undefined`] |
| **agent** | `string` | Agent identifier filter | [Optional] [Defaults to `undefined`] |

### Return type

[**DeliveryResponse**](DeliveryResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`, `application/problem+json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Prompt content delivered |  -  |
| **404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getProject

> ProjectResponse getProject(id)

Get project by ID

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { GetProjectRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: BearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new PromptlyQueryApi(config);

  const body = {
    // string
    id: id_example,
  } satisfies GetProjectRequest;

  try {
    const data = await api.getProject(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**ProjectResponse**](ProjectResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`, `application/problem+json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Project details |  -  |
| **404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getPrompt

> PromptResponse getPrompt(id)

Get prompt details

Returns full prompt details including latest content, tags, and timestamps.

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { GetPromptRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const api = new PromptlyQueryApi();

  const body = {
    // string | Unique prompt identifier
    id: id_example,
  } satisfies GetPromptRequest;

  try {
    const data = await api.getPrompt(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` | Unique prompt identifier | [Defaults to `undefined`] |

### Return type

[**PromptResponse**](PromptResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`, `application/problem+json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Prompt details |  -  |
| **404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getSpecificVersion

> VersionResponse getSpecificVersion(id, versionNumber)

Get a specific version

Returns the content and metadata of a specific prompt version.

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { GetSpecificVersionRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const api = new PromptlyQueryApi();

  const body = {
    // string | Unique prompt identifier
    id: id_example,
    // number
    versionNumber: 56,
  } satisfies GetSpecificVersionRequest;

  try {
    const data = await api.getSpecificVersion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` | Unique prompt identifier | [Defaults to `undefined`] |
| **versionNumber** | `number` |  | [Defaults to `undefined`] |

### Return type

[**VersionResponse**](VersionResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`, `application/problem+json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Version details |  -  |
| **404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getVersionHistory

> Array&lt;VersionResponse&gt; getVersionHistory(id)

Get version history

Returns all versions of a prompt, ordered by version number.

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { GetVersionHistoryRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const api = new PromptlyQueryApi();

  const body = {
    // string | Unique prompt identifier
    id: id_example,
  } satisfies GetVersionHistoryRequest;

  try {
    const data = await api.getVersionHistory(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` | Unique prompt identifier | [Defaults to `undefined`] |

### Return type

[**Array&lt;VersionResponse&gt;**](VersionResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`, `application/problem+json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Version history |  -  |
| **404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listProjects

> Array&lt;ProjectResponse&gt; listProjects()

List all projects

Returns all projects the current user has access to.

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { ListProjectsRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: BearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new PromptlyQueryApi(config);

  try {
    const data = await api.listProjects();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;ProjectResponse&gt;**](ProjectResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of projects |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listPrompts

> Array&lt;PromptSummaryResponse&gt; listPrompts(projectId)

List all prompts

Returns a list of prompt summaries. Optionally filter by project ID.

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { ListPromptsRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const api = new PromptlyQueryApi();

  const body = {
    // string | Filter prompts by project ID (optional)
    projectId: projectId_example,
  } satisfies ListPromptsRequest;

  try {
    const data = await api.listPrompts(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **projectId** | `string` | Filter prompts by project ID | [Optional] [Defaults to `undefined`] |

### Return type

[**Array&lt;PromptSummaryResponse&gt;**](PromptSummaryResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of prompt summaries |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## searchPrompts

> Array&lt;SearchResponse&gt; searchPrompts(q)

Semantic search for prompts

Uses embedding-based vector search to find prompts similar to the query text.

### Example

```ts
import {
  Configuration,
  PromptlyQueryApi,
} from '@promptly/query';
import type { SearchPromptsRequest } from '@promptly/query';

async function example() {
  console.log("🚀 Testing @promptly/query SDK...");
  const api = new PromptlyQueryApi();

  const body = {
    // string | Search query text
    q: q_example,
  } satisfies SearchPromptsRequest;

  try {
    const data = await api.searchPrompts(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **q** | `string` | Search query text | [Defaults to `undefined`] |

### Return type

[**Array&lt;SearchResponse&gt;**](SearchResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Search results with relevance scores |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

