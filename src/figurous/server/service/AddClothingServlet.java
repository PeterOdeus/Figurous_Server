package figurous.server.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

import figurous.server.model.Clothing;
import figurous.server.model.FigurousUser;
import figurous.server.model.ModelValidationException;
import figurous.server.persistence.PMF;

@SuppressWarnings("serial")
public class AddClothingServlet extends GetOrMakeUserAccountServlet {
	
	private static final Logger log = Logger.getLogger(AddClothingServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		User user = authorizeUser(req, resp);
		if(user == null){
			return;
		}
		
		resp.setContentType("application/json; charset=utf-8");
		
		String clothingItemString = null;
		JSONObject jsonClothing = null;
		try {
			clothingItemString = req.getParameter("clothingItem");//getURLDecodedRequestParameter(req, "clothingItem");
			jsonClothing = getJSONObject(clothingItemString);
		} catch (Exception e1) {
			resp.getWriter().println();
			String errString = getErrorJsonString("Failed to understand input. " + e1.getMessage());
			//errString = getURLEncodedString(errString);
			resp.getWriter().println(errString);
			return;
		}
				
		Clothing clothing = new Clothing();
		try {
			clothing.setJSONValues(jsonClothing);
		} catch (ModelValidationException e) {
			String errString = getErrorJsonString(e.getMessage());
			//errString = getURLEncodedString(errString);
			resp.getWriter().println(errString);
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key userKey = KeyFactory.createKey(FigurousUser.class.getSimpleName(), user.getUserId());
		FigurousUser figurousUser = null;
		List<Clothing> clothingList = null;
		try{
			figurousUser = pm.getObjectById(FigurousUser.class, userKey);
			clothingList = figurousUser.getClothingList();
			if(clothingList.size() < 28){
				clothingList.add(clothing);
				pm.makePersistent(figurousUser);
				log.fine("2"+clothing.toJson().toString());
			}else{
				throw new Exception("Maximum number of items reached. " 
						+ "Please drop us a line, info@figurous.com, and let us know you need more space in your wardrobe.");
			}
		} catch(Exception e){
			String errString = getErrorJsonString(e.getMessage());
			//errString = getURLEncodedString(errString);
			resp.getWriter().println(errString);
			return;
		} finally{
			pm.close();
		}
		String returnString = clothing.toJson().toString();
		//returnString= getURLEncodedString(returnString);
		resp.getWriter().println(returnString);
	}
	
	protected JSONObject getJSONObject(String clothingItemString) throws Exception{
		JSONObject jsonClothing = null;
		try{
			jsonClothing = new JSONObject(clothingItemString);
			if(jsonClothing == null){throw new JSONException("resulting JSON object is null.");}
		}catch(JSONException jsonExc){
			String errString = getErrorJsonString("Syntax error in the JSON source string or a duplicated key.");
			//errString = getURLEncodedString(errString);
			throw new Exception(errString);
		}
		return jsonClothing;
	}
	
//	protected String getURLDecodedRequestParameter(HttpServletRequest req, String key) throws Exception{
//		String resultingString = req.getParameter(key);
//		if(resultingString == null){
//			String errString = getErrorJsonString("No clothing item to be added.");
//			errString = getURLEncodedString(errString);
//			throw new Exception(errString);
//		}
//		
//		try {
//			resultingString = getURLDecodedString(resultingString);
//		} catch (UnsupportedEncodingException e) {
//			String errString = getErrorJsonString("Could not decode input data using UTF-8.");
//			errString = getURLEncodedString(errString);
//			throw new Exception(errString);
//		}
//		return resultingString;
//	}
	
//	protected Integer getClothingCount(PersistenceManager pm, Key userKey){
//		Query q = pm.newQuery(Clothing.class,"userKey == keyParam");
//		q.declareParameters("com.google.appengine.api.datastore.Key keyParam");
//		q.setResult("count(id)");
//		return  (Integer)q.execute(userKey);	
//	}
	
	protected String getURLDecodedString(String string) throws UnsupportedEncodingException{
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.severe(e.getMessage() + "inString=" + string);	
			throw new UnsupportedEncodingException("Decoding of input data failed.");
		}
	}
}
