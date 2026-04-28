# PromptResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Unique prompt ID (MongoDB ObjectId) | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**project_id** | **str** |  | [optional] 
**status** | [**PromptStatus**](PromptStatus.md) |  | [optional] 
**content_format** | [**ContentFormat**](ContentFormat.md) |  | [optional] 
**current_version** | **int** | Latest version number | [optional] 
**latest_content** | **str** | Content of the latest version | [optional] 
**tags** | **List[str]** |  | [optional] 
**created_at** | **datetime** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 

## Example

```python
from promptly_query.models.prompt_response import PromptResponse

# TODO update the JSON string below
json = "{}"
# create an instance of PromptResponse from a JSON string
prompt_response_instance = PromptResponse.from_json(json)
# print the JSON string representation of the object
print(PromptResponse.to_json())

# convert the object into a dict
prompt_response_dict = prompt_response_instance.to_dict()
# create an instance of PromptResponse from a dict
prompt_response_from_dict = PromptResponse.from_dict(prompt_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


