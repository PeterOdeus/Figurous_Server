package figurous.server.service;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

import figurous.server.model.Clothing;
import figurous.server.model.FigurousUser;
import figurous.server.persistence.PMF;

@SuppressWarnings("serial")
public class UpdateClothingServlet extends AddClothingServlet {
	
	private static final Logger log = Logger.getLogger(UpdateClothingServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		User user = authorizeUser(req, resp);
		if(user == null){
			return;
		}
		resp.setContentType("application/json; charset=utf-8");
		
		String clothingItemString = null;
		JSONObject jsonClothing = null;
		String clothingId = null;
		try {
			clothingItemString = req.getParameter("clothingItem");// getURLDecodedRequestParameter(req, "clothingItem");
			jsonClothing = getJSONObject(clothingItemString);
			if(jsonClothing.has("id") == false){
				throw new Exception("Missing id.");
			}
			clothingId = jsonClothing.getString("id");
			if(clothingId == null){
				throw new Exception("Missing id.");
			}
		} catch (Exception e1) {
			String errString = getErrorJsonString(e1.getMessage());
			//errString = getURLEncodedString(errString);
			resp.getWriter().println(errString);
			return;
		}
		
		//Key userKey = KeyFactory.createKey(FigurousUser.class.getSimpleName(), user.getUserId());
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Clothing clothing = null;
		
		try{
			Key clothingKey = KeyFactory.stringToKey(clothingId);
			Key userKey = clothingKey.getParent();
			Key generatedUserKey = KeyFactory.createKey(FigurousUser.class.getSimpleName(), user.getUserId());
			if(!userKey.equals(generatedUserKey)){
				throw new Exception("Invalid user.");
			}
			clothing = pm.getObjectById(Clothing.class, clothingKey);
			clothing.setJSONValues(jsonClothing);
			pm.makePersistent(clothing);
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
}
