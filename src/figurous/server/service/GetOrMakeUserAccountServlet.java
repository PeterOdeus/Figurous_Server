package figurous.server.service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import figurous.server.model.Clothing;
import figurous.server.model.FigurousUser;
import figurous.server.persistence.PMF;

@SuppressWarnings("serial")
public class GetOrMakeUserAccountServlet extends AuthServlet {
	
	private static final Logger log = Logger.getLogger(GetOrMakeUserAccountServlet.class.getName());
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		User user = authorizeUser(req, resp);
		if(user == null){
			return;
		}
		UserService userService = UserServiceFactory.getUserService();
		log.info("userId: " + user.getUserId());
		resp.setContentType("application/json; charset=utf-8");
		FigurousUser figurousUser = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String returnString = null;
		try{
			Key userKey = KeyFactory.createKey(FigurousUser.class.getSimpleName(), user.getUserId());
			try{
				figurousUser = pm.getObjectById(FigurousUser.class, userKey);				
			} catch(JDOObjectNotFoundException jdo){
				log.info("creating new figurousUser");
				figurousUser = new FigurousUser(userKey);
				pm.makePersistent(figurousUser);
			}	
			JSONObject jsonFigurousUser = figurousUser.toJson();
			jsonFigurousUser.put("logoutURL",userService.createLogoutURL("/"));
			returnString = jsonFigurousUser.toString();
		}catch(Exception e){
			log.severe(e.getMessage());
			String errString = getErrorJsonString("Error when getting/creating user account.");
			//errString = getURLEncodedString(errString);
			resp.getWriter().println(errString);
			return;
		} finally{
				pm.close();
		}
		
		//returnString= getURLEncodedString(returnString);
		resp.getWriter().println(returnString);
	}
	
	private FigurousUser getOrMakeUserAccount(PersistenceManager pm, String userId) {
		FigurousUser figurousUser = null;		
		Key userKey = KeyFactory.createKey(FigurousUser.class.getSimpleName(), userId);
		Query q = pm.newQuery(FigurousUser.class, "key == keyParam");
		q.declareParameters("com.google.appengine.api.datastore.Key keyParam");
		List<FigurousUser> results = (List<FigurousUser>) q.execute(userKey);
		if(results.size() == 0){
			log.info("creating new figurousUser");
			figurousUser = new FigurousUser(userKey);
			pm.makePersistent(figurousUser);
			pm.close();
		}else{
			figurousUser = results.get(0);
			List<Clothing> clothingList = getClothing(pm, userKey);
			pm.close();
			figurousUser.setClothingList(clothingList);
		}
		return figurousUser;
	}

	@SuppressWarnings("unchecked")
	protected List<Clothing> getClothing(PersistenceManager pm, Key userKey){
		Query q = pm.newQuery(Clothing.class,"userKey == keyParam");
		q.declareParameters("com.google.appengine.api.datastore.Key keyParam");
		return (List<Clothing>) q.execute(userKey);	
	}
}
