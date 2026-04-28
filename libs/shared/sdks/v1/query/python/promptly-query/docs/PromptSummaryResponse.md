# PromptSummaryResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**project_id** | **str** |  | [optional] 
**status** | [**PromptStatus**](PromptStatus.md) |  | [optional] 
**current_version** | **int** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 

## Example

```python
from promptly_query.models.prompt_summary_response import PromptSummaryResponse

# TODO update the JSON string below
json = "{}"
# create an instance of PromptSummaryResponse from a JSON string
prompt_summary_response_instance = PromptSummaryResponse.from_json(json)
# print the JSON string representation of the object
print(PromptSummaryResponse.to_json())

# convert the object into a dict
prompt_summary_response_dict = prompt_summary_response_instance.to_dict()
# create an instance of PromptSummaryResponse from a dict
prompt_summary_response_from_dict = PromptSummaryResponse.from_dict(prompt_summary_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


