package figurous.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FigurousUser extends AbstractJSON{
	
	@PrimaryKey
	@Persistent
	private Key key;
	public static String KEY = "id";
	@Persistent
	private String websiteURL;
	public static String WEBSITEURL = "websiteURL";
	@Persistent
	private String websiteName;
	public static String WEBSITENAME = "websiteName";
	@Persistent
	private String nickName;
	public static String NICKNAME = "nickName";	
	@Persistent
	private List<Clothing> clothingList;
	public static String CLOTHINGLIST = "clothingList";
	
	public boolean setJSONValues(JSONObject jsonObj) throws ModelValidationException{
		//Web site URL
		setWebsiteURL(getJsonStringValue(jsonObj, WEBSITEURL, 255, getWebsiteURL()));		
		//Web site name
		setWebsiteName(getJsonStringValue(jsonObj, WEBSITENAME, 20, getWebsiteName()));
		//Nick
		setNickName(getJsonStringValue(jsonObj, NICKNAME, 15, getNickName()));
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson(){
		JSONObject json = new JSONObject();
		try{
			json.put(KEY, (key == null?JSONObject.NULL:KeyFactory.keyToString(key)));
			json.put(WEBSITEURL, (websiteURL==null?JSONObject.NULL:getWebsiteURL()));
			json.put(WEBSITENAME, (websiteName==null?JSONObject.NULL:getWebsiteName()));
			json.put(NICKNAME, (nickName==null?JSONObject.NULL:getNickName()));
			json.put(CLOTHINGLIST, new ArrayList(0));
			if(clothingList != null){
				JSONArray jsonArray = json.getJSONArray(CLOTHINGLIST);
				for(Clothing clothing: clothingList){
					jsonArray.put(clothing.toJson());
				}
			}
		} catch (JSONException e) {
			try {
				json.put("error", "Failed to create JSON string from User object.");
			} catch (JSONException e1) {
				//TODO
			}
		}
		return json;
	}
	
	public FigurousUser(Key figurousUserKey){
		this.key = figurousUserKey;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	public List<Clothing> getClothingList() {
		return clothingList;
	}

	public void setClothingList(List<Clothing> clothingList) {
		this.clothingList = clothingList;
	}

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
