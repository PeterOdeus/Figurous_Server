package figurous.server.model;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractJSON {
	public abstract boolean setJSONValues(JSONObject jsonObj)throws ModelValidationException;
	public abstract JSONObject toJson();
	
	protected Boolean getJsonBooleanValue(JSONObject jsonObj, String key, Boolean original) throws ModelValidationException {
		Object tempObject = null;
		Boolean tempValue = null;
		if(jsonObj.has(key)){
			try{
				tempObject = jsonObj.get(key);
			} catch(JSONException e){
				return original;
			}
			if(tempObject == null || (tempObject != null && JSONObject.NULL.equals(tempObject))){
				return null;
			}
			if(!(tempObject instanceof Boolean)){
				throw new ModelValidationException(
						"'" + key + "' is expected to have a true/false value.");
			}
			tempValue = (Boolean) tempObject;
			return tempValue;
		} else{
			return original;
		}
	}
	
	protected Integer getJsonIntValue(JSONObject jsonObj, String key, int maxValue, Integer original) throws ModelValidationException {
		Object tempObject = null;
		Integer tempValue = null;
		if(jsonObj.has(key)){
			try{
				tempObject = jsonObj.get(key);
			} catch(JSONException e){
				return original;
			}
			if(tempObject == null || (tempObject != null && JSONObject.NULL.equals(tempObject))){
				return null;
			}
			if(!(tempObject instanceof Integer)){
				throw new ModelValidationException(
						"'" + key + "' is expected to have a numeric value.");
			}
			tempValue = (Integer) tempObject;
			if(tempValue < 0 || tempValue > maxValue){
				throw new ModelValidationException(
						"'" + key + "' cannot be negative or more than " + maxValue);
			}
			return tempValue;
		} else{
			return original;
		}
	}
	
	protected String getJsonStringValue(JSONObject jsonObj, String key, int maxLength, String original) throws ModelValidationException {
		Object tempObject = null;
		String tempValue = null;
		if(jsonObj.has(key)){
			try{
				tempObject = jsonObj.get(key);
			} catch(JSONException e){
				return original;
			}
			if(tempObject == null || (tempObject != null && JSONObject.NULL.equals(tempObject))){
				return null;
			}
			if(!(tempObject instanceof String)){
				throw new ModelValidationException(
						"'" + key + "' is expected to have a string value.");
			}
			tempValue = (String) tempObject;
			tempValue = tempValue.trim();
			if(tempValue.length() == 0){
				return null;
			}
			if(tempValue.length() > maxLength){
				throw new ModelValidationException(
						"'" + key + "' has more than " + maxLength + " characters.");
			}
			return tempValue;
		} else{
			return original;
		}
	}
}
