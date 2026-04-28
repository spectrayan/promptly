# DeliveryResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**prompt_id** | **str** |  | [optional] 
**name** | **str** |  | [optional] 
**content** | **str** | The full prompt content for runtime use | [optional] 
**version** | **int** |  | [optional] 
**content_format** | **str** |  | [optional] 

## Example

```python
from promptly_query.models.delivery_response import DeliveryResponse

# TODO update the JSON string below
json = "{}"
# create an instance of DeliveryResponse from a JSON string
delivery_response_instance = DeliveryResponse.from_json(json)
# print the JSON string representation of the object
print(DeliveryResponse.to_json())

# convert the object into a dict
delivery_response_dict = delivery_response_instance.to_dict()
# create an instance of DeliveryResponse from a dict
delivery_response_from_dict = DeliveryResponse.from_dict(delivery_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


