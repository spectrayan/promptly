# promptly_query.PromptlyQueryApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**deliver_prompt**](PromptlyQueryApi.md#deliver_prompt) | **GET** /api/v1/deliver | Deliver prompt to AI agent
[**get_project**](PromptlyQueryApi.md#get_project) | **GET** /api/v1/projects/{id} | Get project by ID
[**get_prompt**](PromptlyQueryApi.md#get_prompt) | **GET** /api/v1/prompts/{id} | Get prompt details
[**get_specific_version**](PromptlyQueryApi.md#get_specific_version) | **GET** /api/v1/prompts/{id}/versions/{versionNumber} | Get a specific version
[**get_version_history**](PromptlyQueryApi.md#get_version_history) | **GET** /api/v1/prompts/{id}/versions | Get version history
[**list_projects**](PromptlyQueryApi.md#list_projects) | **GET** /api/v1/projects | List all projects
[**list_prompts**](PromptlyQueryApi.md#list_prompts) | **GET** /api/v1/prompts | List all prompts
[**search_prompts**](PromptlyQueryApi.md#search_prompts) | **GET** /api/v1/search | Semantic search for prompts


# **deliver_prompt**
> DeliveryResponse deliver_prompt(app_id, usecase=usecase, agent=agent)

Deliver prompt to AI agent

Low-latency endpoint for AI agents to fetch the latest active prompt
by application ID and optional use case / agent identifiers.


### Example


```python
import promptly_query
from promptly_query.models.delivery_response import DeliveryResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    app_id = 'app_id_example' # str | Application/project identifier
    usecase = 'usecase_example' # str | Use case filter (optional)
    agent = 'agent_example' # str | Agent identifier filter (optional)

    try:
        # Deliver prompt to AI agent
        api_response = api_instance.deliver_prompt(app_id, usecase=usecase, agent=agent)
        print("The response of PromptlyQueryApi->deliver_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->deliver_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app_id** | **str**| Application/project identifier | 
 **usecase** | **str**| Use case filter | [optional] 
 **agent** | **str**| Agent identifier filter | [optional] 

### Return type

[**DeliveryResponse**](DeliveryResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Prompt content delivered |  -  |
**404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_project**
> ProjectResponse get_project(id)

Get project by ID

### Example

* Bearer (JWT) Authentication (BearerAuth):

```python
import promptly_query
from promptly_query.models.project_response import ProjectResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): BearerAuth
configuration = promptly_query.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    id = 'id_example' # str | 

    try:
        # Get project by ID
        api_response = api_instance.get_project(id)
        print("The response of PromptlyQueryApi->get_project:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->get_project: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**ProjectResponse**](ProjectResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Project details |  -  |
**404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_prompt**
> PromptResponse get_prompt(id)

Get prompt details

Returns full prompt details including latest content, tags, and timestamps.

### Example


```python
import promptly_query
from promptly_query.models.prompt_response import PromptResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    id = 'id_example' # str | Unique prompt identifier

    try:
        # Get prompt details
        api_response = api_instance.get_prompt(id)
        print("The response of PromptlyQueryApi->get_prompt:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->get_prompt: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Unique prompt identifier | 

### Return type

[**PromptResponse**](PromptResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Prompt details |  -  |
**404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_specific_version**
> VersionResponse get_specific_version(id, version_number)

Get a specific version

Returns the content and metadata of a specific prompt version.

### Example


```python
import promptly_query
from promptly_query.models.version_response import VersionResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    id = 'id_example' # str | Unique prompt identifier
    version_number = 56 # int | 

    try:
        # Get a specific version
        api_response = api_instance.get_specific_version(id, version_number)
        print("The response of PromptlyQueryApi->get_specific_version:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->get_specific_version: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Unique prompt identifier | 
 **version_number** | **int**|  | 

### Return type

[**VersionResponse**](VersionResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Version details |  -  |
**404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_version_history**
> List[VersionResponse] get_version_history(id)

Get version history

Returns all versions of a prompt, ordered by version number.

### Example


```python
import promptly_query
from promptly_query.models.version_response import VersionResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    id = 'id_example' # str | Unique prompt identifier

    try:
        # Get version history
        api_response = api_instance.get_version_history(id)
        print("The response of PromptlyQueryApi->get_version_history:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->get_version_history: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**| Unique prompt identifier | 

### Return type

[**List[VersionResponse]**](VersionResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Version history |  -  |
**404** | The requested resource was not found |  * X-Request-Id - Unique request identifier for tracing <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_projects**
> List[ProjectResponse] list_projects()

List all projects

Returns all projects the current user has access to.

### Example

* Bearer (JWT) Authentication (BearerAuth):

```python
import promptly_query
from promptly_query.models.project_response import ProjectResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): BearerAuth
configuration = promptly_query.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)

    try:
        # List all projects
        api_response = api_instance.list_projects()
        print("The response of PromptlyQueryApi->list_projects:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->list_projects: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[ProjectResponse]**](ProjectResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | List of projects |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_prompts**
> List[PromptSummaryResponse] list_prompts(project_id=project_id)

List all prompts

Returns a list of prompt summaries. Optionally filter by project ID.

### Example


```python
import promptly_query
from promptly_query.models.prompt_summary_response import PromptSummaryResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    project_id = 'project_id_example' # str | Filter prompts by project ID (optional)

    try:
        # List all prompts
        api_response = api_instance.list_prompts(project_id=project_id)
        print("The response of PromptlyQueryApi->list_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->list_prompts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **project_id** | **str**| Filter prompts by project ID | [optional] 

### Return type

[**List[PromptSummaryResponse]**](PromptSummaryResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | List of prompt summaries |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_prompts**
> List[SearchResponse] search_prompts(q)

Semantic search for prompts

Uses embedding-based vector search to find prompts similar to the query text.

### Example


```python
import promptly_query
from promptly_query.models.search_response import SearchResponse
from promptly_query.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = promptly_query.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with promptly_query.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = promptly_query.PromptlyQueryApi(api_client)
    q = 'q_example' # str | Search query text

    try:
        # Semantic search for prompts
        api_response = api_instance.search_prompts(q)
        print("The response of PromptlyQueryApi->search_prompts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptlyQueryApi->search_prompts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **q** | **str**| Search query text | 

### Return type

[**List[SearchResponse]**](SearchResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Search results with relevance scores |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

