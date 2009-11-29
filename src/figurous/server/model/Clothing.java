package figurous.server.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Clothing extends AbstractJSON{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	public static String KEY = "id";
	@Persistent
	private String title;
	public static String TITLE = "title";
	@Persistent
	private String barCodeNumber;
	public static String BARCODENUMBER = "barCodeNumber";
	@Persistent
	private String description;
	public static String DESCRIPTION = "description";
	@Persistent
	private Integer rating;
	public static String RATING = "rating";
	@Persistent
	private Integer wardrobePlacement;
	public static String WARDROBEPLACEMENT = "wardrobePlacement";
	@Persistent
	private Boolean isPrivate;
	public static String ISPRIVATE = "isPrivate";
	@Persistent
	private String image;
	public static String IMAGE = "image";
	
	
	public boolean setJSONValues(JSONObject jsonObj) throws ModelValidationException{
		//Title
		setTitle(getJsonStringValue(jsonObj, TITLE, 160, getTitle()));		
		//Barcode number
		setBarCodeNumber(getJsonStringValue(jsonObj, BARCODENUMBER, 25, getBarCodeNumber()));
		//Description
		setDescription(getJsonStringValue(jsonObj, DESCRIPTION, 255, getDescription()));
		//Rating
		setRating(getJsonIntValue(jsonObj, RATING, 5, getRating()));
		//WardrobePlacement
		setWardrobePlacement(getJsonIntValue(jsonObj, WARDROBEPLACEMENT, 8, getWardrobePlacement()));
		//IsPrivate
		setIsPrivate(getJsonBooleanValue(jsonObj, ISPRIVATE, getIsPrivate()));
		//Image
		setImage(getJsonStringValue(jsonObj, IMAGE, 512, getImage()));
		
		return true;
	}
	public JSONObject toJson(){
		JSONObject json = new JSONObject();
		try {
			json.put(KEY, (key == null?JSONObject.NULL:KeyFactory.keyToString(key)));
			json.put(TITLE, (title==null?JSONObject.NULL:getTitle()));
			json.put(BARCODENUMBER, (barCodeNumber==null?JSONObject.NULL:getBarCodeNumber()));
			json.put(DESCRIPTION, (description==null?JSONObject.NULL:getDescription()));
			json.put(RATING, (rating==null?new Integer(0):getRating()));
			json.put(WARDROBEPLACEMENT, (wardrobePlacement==null?new Integer(4):getWardrobePlacement()));
			json.put(ISPRIVATE, (isPrivate==null?new Boolean(false):getIsPrivate()));
			json.put(IMAGE, ((image==null || image != null && image.trim().equals(""))?JSONObject.NULL:getImage()));
		} catch (JSONException e) {
			try {
				json = new JSONObject();
				json.put("error", "Failed to create JSON string from Clothing object.");
			} catch (JSONException e1) {
				//TODO
			}
		}
		return json;
	}
	
	
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBarCodeNumber() {
		return barCodeNumber;
	}

	public void setBarCodeNumber(String barCodeNumber) {
		this.barCodeNumber = barCodeNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public Integer getWardrobePlacement() {
		return wardrobePlacement;
	}
	public void setWardrobePlacement(Integer wardrobePlacement) {
		this.wardrobePlacement = wardrobePlacement;
	}
	public Boolean getIsPrivate() {
		return isPrivate;
	}
	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
