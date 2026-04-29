package com.promptly.query.api;

import com.promptly.query.ApiClient;

import com.promptly.query.model.DeliveryResponse;
import com.promptly.query.model.ProblemDetails;
import com.promptly.query.model.ProjectResponse;
import com.promptly.query.model.PromptResponse;
import com.promptly.query.model.PromptSummaryResponse;
import com.promptly.query.model.SearchResponse;
import com.promptly.query.model.VersionResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-04-29T16:30:36.848011900-05:00[America/Chicago]", comments = "Generator version: 7.21.0")
public class PromptlyQueryApi {
    private ApiClient apiClient;

    public PromptlyQueryApi() {
        this(new ApiClient());
    }

    public PromptlyQueryApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Deliver prompt to AI agent
     * Low-latency endpoint for AI agents to fetch the latest active prompt by application ID and optional use case / agent identifiers. 
     * <p><b>200</b> - Prompt content delivered
     * <p><b>404</b> - The requested resource was not found
     * @param appId Application/project identifier
     * @param usecase Use case filter
     * @param agent Agent identifier filter
     * @return DeliveryResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deliverPromptRequestCreation(@jakarta.annotation.Nonnull String appId, @jakarta.annotation.Nullable String usecase, @jakarta.annotation.Nullable String agent) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'appId' is set
        if (appId == null) {
            throw new WebClientResponseException("Missing the required parameter 'appId' when calling deliverPrompt", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "appId", appId));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "usecase", usecase));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "agent", agent));

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<DeliveryResponse> localVarReturnType = new ParameterizedTypeReference<DeliveryResponse>() {};
        return apiClient.invokeAPI("/api/v1/deliver", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Deliver prompt to AI agent
     * Low-latency endpoint for AI agents to fetch the latest active prompt by application ID and optional use case / agent identifiers. 
     * <p><b>200</b> - Prompt content delivered
     * <p><b>404</b> - The requested resource was not found
     * @param appId Application/project identifier
     * @param usecase Use case filter
     * @param agent Agent identifier filter
     * @return DeliveryResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<DeliveryResponse> deliverPrompt(@jakarta.annotation.Nonnull String appId, @jakarta.annotation.Nullable String usecase, @jakarta.annotation.Nullable String agent) throws WebClientResponseException {
        ParameterizedTypeReference<DeliveryResponse> localVarReturnType = new ParameterizedTypeReference<DeliveryResponse>() {};
        return deliverPromptRequestCreation(appId, usecase, agent).bodyToMono(localVarReturnType);
    }

    /**
     * Deliver prompt to AI agent
     * Low-latency endpoint for AI agents to fetch the latest active prompt by application ID and optional use case / agent identifiers. 
     * <p><b>200</b> - Prompt content delivered
     * <p><b>404</b> - The requested resource was not found
     * @param appId Application/project identifier
     * @param usecase Use case filter
     * @param agent Agent identifier filter
     * @return ResponseEntity&lt;DeliveryResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<DeliveryResponse>> deliverPromptWithHttpInfo(@jakarta.annotation.Nonnull String appId, @jakarta.annotation.Nullable String usecase, @jakarta.annotation.Nullable String agent) throws WebClientResponseException {
        ParameterizedTypeReference<DeliveryResponse> localVarReturnType = new ParameterizedTypeReference<DeliveryResponse>() {};
        return deliverPromptRequestCreation(appId, usecase, agent).toEntity(localVarReturnType);
    }

    /**
     * Deliver prompt to AI agent
     * Low-latency endpoint for AI agents to fetch the latest active prompt by application ID and optional use case / agent identifiers. 
     * <p><b>200</b> - Prompt content delivered
     * <p><b>404</b> - The requested resource was not found
     * @param appId Application/project identifier
     * @param usecase Use case filter
     * @param agent Agent identifier filter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deliverPromptWithResponseSpec(@jakarta.annotation.Nonnull String appId, @jakarta.annotation.Nullable String usecase, @jakarta.annotation.Nullable String agent) throws WebClientResponseException {
        return deliverPromptRequestCreation(appId, usecase, agent);
    }

    /**
     * Get project by ID
     * 
     * <p><b>200</b> - Project details
     * <p><b>404</b> - The requested resource was not found
     * @param id The id parameter
     * @return ProjectResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getProjectRequestCreation(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getProject", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "BearerAuth" };

        ParameterizedTypeReference<ProjectResponse> localVarReturnType = new ParameterizedTypeReference<ProjectResponse>() {};
        return apiClient.invokeAPI("/api/v1/projects/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get project by ID
     * 
     * <p><b>200</b> - Project details
     * <p><b>404</b> - The requested resource was not found
     * @param id The id parameter
     * @return ProjectResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ProjectResponse> getProject(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        ParameterizedTypeReference<ProjectResponse> localVarReturnType = new ParameterizedTypeReference<ProjectResponse>() {};
        return getProjectRequestCreation(id).bodyToMono(localVarReturnType);
    }

    /**
     * Get project by ID
     * 
     * <p><b>200</b> - Project details
     * <p><b>404</b> - The requested resource was not found
     * @param id The id parameter
     * @return ResponseEntity&lt;ProjectResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ProjectResponse>> getProjectWithHttpInfo(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        ParameterizedTypeReference<ProjectResponse> localVarReturnType = new ParameterizedTypeReference<ProjectResponse>() {};
        return getProjectRequestCreation(id).toEntity(localVarReturnType);
    }

    /**
     * Get project by ID
     * 
     * <p><b>200</b> - Project details
     * <p><b>404</b> - The requested resource was not found
     * @param id The id parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getProjectWithResponseSpec(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        return getProjectRequestCreation(id);
    }

    /**
     * Get prompt details
     * Returns full prompt details including latest content, tags, and timestamps.
     * <p><b>200</b> - Prompt details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return PromptResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getPromptRequestCreation(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getPrompt", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<PromptResponse> localVarReturnType = new ParameterizedTypeReference<PromptResponse>() {};
        return apiClient.invokeAPI("/api/v1/prompts/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get prompt details
     * Returns full prompt details including latest content, tags, and timestamps.
     * <p><b>200</b> - Prompt details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return PromptResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<PromptResponse> getPrompt(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        ParameterizedTypeReference<PromptResponse> localVarReturnType = new ParameterizedTypeReference<PromptResponse>() {};
        return getPromptRequestCreation(id).bodyToMono(localVarReturnType);
    }

    /**
     * Get prompt details
     * Returns full prompt details including latest content, tags, and timestamps.
     * <p><b>200</b> - Prompt details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return ResponseEntity&lt;PromptResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<PromptResponse>> getPromptWithHttpInfo(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        ParameterizedTypeReference<PromptResponse> localVarReturnType = new ParameterizedTypeReference<PromptResponse>() {};
        return getPromptRequestCreation(id).toEntity(localVarReturnType);
    }

    /**
     * Get prompt details
     * Returns full prompt details including latest content, tags, and timestamps.
     * <p><b>200</b> - Prompt details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getPromptWithResponseSpec(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        return getPromptRequestCreation(id);
    }

    /**
     * Get a specific version
     * Returns the content and metadata of a specific prompt version.
     * <p><b>200</b> - Version details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @param versionNumber The versionNumber parameter
     * @return VersionResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getSpecificVersionRequestCreation(@jakarta.annotation.Nonnull String id, @jakarta.annotation.Nonnull Integer versionNumber) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getSpecificVersion", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'versionNumber' is set
        if (versionNumber == null) {
            throw new WebClientResponseException("Missing the required parameter 'versionNumber' when calling getSpecificVersion", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);
        pathParams.put("versionNumber", versionNumber);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<VersionResponse> localVarReturnType = new ParameterizedTypeReference<VersionResponse>() {};
        return apiClient.invokeAPI("/api/v1/prompts/{id}/versions/{versionNumber}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get a specific version
     * Returns the content and metadata of a specific prompt version.
     * <p><b>200</b> - Version details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @param versionNumber The versionNumber parameter
     * @return VersionResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<VersionResponse> getSpecificVersion(@jakarta.annotation.Nonnull String id, @jakarta.annotation.Nonnull Integer versionNumber) throws WebClientResponseException {
        ParameterizedTypeReference<VersionResponse> localVarReturnType = new ParameterizedTypeReference<VersionResponse>() {};
        return getSpecificVersionRequestCreation(id, versionNumber).bodyToMono(localVarReturnType);
    }

    /**
     * Get a specific version
     * Returns the content and metadata of a specific prompt version.
     * <p><b>200</b> - Version details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @param versionNumber The versionNumber parameter
     * @return ResponseEntity&lt;VersionResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<VersionResponse>> getSpecificVersionWithHttpInfo(@jakarta.annotation.Nonnull String id, @jakarta.annotation.Nonnull Integer versionNumber) throws WebClientResponseException {
        ParameterizedTypeReference<VersionResponse> localVarReturnType = new ParameterizedTypeReference<VersionResponse>() {};
        return getSpecificVersionRequestCreation(id, versionNumber).toEntity(localVarReturnType);
    }

    /**
     * Get a specific version
     * Returns the content and metadata of a specific prompt version.
     * <p><b>200</b> - Version details
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @param versionNumber The versionNumber parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getSpecificVersionWithResponseSpec(@jakarta.annotation.Nonnull String id, @jakarta.annotation.Nonnull Integer versionNumber) throws WebClientResponseException {
        return getSpecificVersionRequestCreation(id, versionNumber);
    }

    /**
     * Get version history
     * Returns all versions of a prompt, ordered by version number.
     * <p><b>200</b> - Version history
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return List&lt;VersionResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getVersionHistoryRequestCreation(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getVersionHistory", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<VersionResponse> localVarReturnType = new ParameterizedTypeReference<VersionResponse>() {};
        return apiClient.invokeAPI("/api/v1/prompts/{id}/versions", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get version history
     * Returns all versions of a prompt, ordered by version number.
     * <p><b>200</b> - Version history
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return List&lt;VersionResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<VersionResponse> getVersionHistory(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        ParameterizedTypeReference<VersionResponse> localVarReturnType = new ParameterizedTypeReference<VersionResponse>() {};
        return getVersionHistoryRequestCreation(id).bodyToFlux(localVarReturnType);
    }

    /**
     * Get version history
     * Returns all versions of a prompt, ordered by version number.
     * <p><b>200</b> - Version history
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return ResponseEntity&lt;List&lt;VersionResponse&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<VersionResponse>>> getVersionHistoryWithHttpInfo(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        ParameterizedTypeReference<VersionResponse> localVarReturnType = new ParameterizedTypeReference<VersionResponse>() {};
        return getVersionHistoryRequestCreation(id).toEntityList(localVarReturnType);
    }

    /**
     * Get version history
     * Returns all versions of a prompt, ordered by version number.
     * <p><b>200</b> - Version history
     * <p><b>404</b> - The requested resource was not found
     * @param id Unique prompt identifier
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getVersionHistoryWithResponseSpec(@jakarta.annotation.Nonnull String id) throws WebClientResponseException {
        return getVersionHistoryRequestCreation(id);
    }

    /**
     * List projects (paginated)
     * Returns a paginated list of projects the current user has access to.
     * <p><b>200</b> - List of projects
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return List&lt;ProjectResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listProjectsRequestCreation(@jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] { "BearerAuth" };

        ParameterizedTypeReference<ProjectResponse> localVarReturnType = new ParameterizedTypeReference<ProjectResponse>() {};
        return apiClient.invokeAPI("/api/v1/projects", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List projects (paginated)
     * Returns a paginated list of projects the current user has access to.
     * <p><b>200</b> - List of projects
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return List&lt;ProjectResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<ProjectResponse> listProjects(@jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        ParameterizedTypeReference<ProjectResponse> localVarReturnType = new ParameterizedTypeReference<ProjectResponse>() {};
        return listProjectsRequestCreation(page, size, sort).bodyToFlux(localVarReturnType);
    }

    /**
     * List projects (paginated)
     * Returns a paginated list of projects the current user has access to.
     * <p><b>200</b> - List of projects
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return ResponseEntity&lt;List&lt;ProjectResponse&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<ProjectResponse>>> listProjectsWithHttpInfo(@jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        ParameterizedTypeReference<ProjectResponse> localVarReturnType = new ParameterizedTypeReference<ProjectResponse>() {};
        return listProjectsRequestCreation(page, size, sort).toEntityList(localVarReturnType);
    }

    /**
     * List projects (paginated)
     * Returns a paginated list of projects the current user has access to.
     * <p><b>200</b> - List of projects
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listProjectsWithResponseSpec(@jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        return listProjectsRequestCreation(page, size, sort);
    }

    /**
     * List prompts (paginated)
     * Returns a paginated, streaming list of prompt summaries. Optionally filter by project ID.
     * <p><b>200</b> - List of prompt summaries
     * @param projectId Filter prompts by project ID
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return List&lt;PromptSummaryResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listPromptsRequestCreation(@jakarta.annotation.Nullable String projectId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "projectId", projectId));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<PromptSummaryResponse> localVarReturnType = new ParameterizedTypeReference<PromptSummaryResponse>() {};
        return apiClient.invokeAPI("/api/v1/prompts", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List prompts (paginated)
     * Returns a paginated, streaming list of prompt summaries. Optionally filter by project ID.
     * <p><b>200</b> - List of prompt summaries
     * @param projectId Filter prompts by project ID
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return List&lt;PromptSummaryResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<PromptSummaryResponse> listPrompts(@jakarta.annotation.Nullable String projectId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        ParameterizedTypeReference<PromptSummaryResponse> localVarReturnType = new ParameterizedTypeReference<PromptSummaryResponse>() {};
        return listPromptsRequestCreation(projectId, page, size, sort).bodyToFlux(localVarReturnType);
    }

    /**
     * List prompts (paginated)
     * Returns a paginated, streaming list of prompt summaries. Optionally filter by project ID.
     * <p><b>200</b> - List of prompt summaries
     * @param projectId Filter prompts by project ID
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return ResponseEntity&lt;List&lt;PromptSummaryResponse&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<PromptSummaryResponse>>> listPromptsWithHttpInfo(@jakarta.annotation.Nullable String projectId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        ParameterizedTypeReference<PromptSummaryResponse> localVarReturnType = new ParameterizedTypeReference<PromptSummaryResponse>() {};
        return listPromptsRequestCreation(projectId, page, size, sort).toEntityList(localVarReturnType);
    }

    /**
     * List prompts (paginated)
     * Returns a paginated, streaming list of prompt summaries. Optionally filter by project ID.
     * <p><b>200</b> - List of prompt summaries
     * @param projectId Filter prompts by project ID
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sort Sort criteria (e.g. createdAt,desc)
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listPromptsWithResponseSpec(@jakarta.annotation.Nullable String projectId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable String sort) throws WebClientResponseException {
        return listPromptsRequestCreation(projectId, page, size, sort);
    }

    /**
     * Semantic search for prompts
     * Uses embedding-based vector search to find prompts similar to the query text.
     * <p><b>200</b> - Search results with relevance scores
     * @param q Search query text
     * @return List&lt;SearchResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec searchPromptsRequestCreation(@jakarta.annotation.Nonnull String q) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'q' is set
        if (q == null) {
            throw new WebClientResponseException("Missing the required parameter 'q' when calling searchPrompts", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "q", q));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<SearchResponse> localVarReturnType = new ParameterizedTypeReference<SearchResponse>() {};
        return apiClient.invokeAPI("/api/v1/search", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Semantic search for prompts
     * Uses embedding-based vector search to find prompts similar to the query text.
     * <p><b>200</b> - Search results with relevance scores
     * @param q Search query text
     * @return List&lt;SearchResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<SearchResponse> searchPrompts(@jakarta.annotation.Nonnull String q) throws WebClientResponseException {
        ParameterizedTypeReference<SearchResponse> localVarReturnType = new ParameterizedTypeReference<SearchResponse>() {};
        return searchPromptsRequestCreation(q).bodyToFlux(localVarReturnType);
    }

    /**
     * Semantic search for prompts
     * Uses embedding-based vector search to find prompts similar to the query text.
     * <p><b>200</b> - Search results with relevance scores
     * @param q Search query text
     * @return ResponseEntity&lt;List&lt;SearchResponse&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<SearchResponse>>> searchPromptsWithHttpInfo(@jakarta.annotation.Nonnull String q) throws WebClientResponseException {
        ParameterizedTypeReference<SearchResponse> localVarReturnType = new ParameterizedTypeReference<SearchResponse>() {};
        return searchPromptsRequestCreation(q).toEntityList(localVarReturnType);
    }

    /**
     * Semantic search for prompts
     * Uses embedding-based vector search to find prompts similar to the query text.
     * <p><b>200</b> - Search results with relevance scores
     * @param q Search query text
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec searchPromptsWithResponseSpec(@jakarta.annotation.Nonnull String q) throws WebClientResponseException {
        return searchPromptsRequestCreation(q);
    }
}
