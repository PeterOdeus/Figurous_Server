package figurous.server.service;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import figurous.server.model.FigurousUser;
import figurous.server.persistence.PMF;

@SuppressWarnings("serial")
public class GetFigurousUserServlet extends GetOrMakeUserAccountServlet{
	
	private static final Logger log = Logger.getLogger(GetFigurousUserServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.sendError(405);//Method not allowed
		return;
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String figurousIdString = req.getParameter("id");
		if(figurousIdString == null){
			resp.sendError(400);//Bad request
			return;
		}
		
		FigurousUser figurousUser = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String returnString = null;
		try{
			Key figurousUserKey = KeyFactory.stringToKey(figurousIdString);
			try{
				figurousUser = pm.getObjectById(FigurousUser.class, figurousUserKey);				
			} catch(JDOObjectNotFoundException jdo){
				resp.sendError(404);//Not Found
				return;
			}	
			JSONObject jsonFigurousUser = figurousUser.toJson();
			returnString = jsonFigurousUser.toString();
		}catch(IllegalArgumentException e){
			resp.sendError(400);//Bad request
			return;
		}catch(Exception e){
			log.warning(e.getMessage());
			returnString = getErrorJsonString("Server error when getting Figurous user.");
		} finally{
				pm.close();
		}
		
		resp.setContentType("application/json; charset=utf-8");
		resp.getWriter().println(returnString);
	}
}
