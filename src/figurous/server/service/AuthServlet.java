package figurous.server.service;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AuthServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AuthServlet.class.getName());
	
	public User authorizeUser(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null){
			failUser(userService, req, resp);
		}
		return user;
	}
	
	public void failUser(UserService userService, HttpServletRequest req, HttpServletResponse resp) 
		throws IOException{
		String avoidHttpRedirect = req.getParameter("AVOID_HTTP_REDIRECT");
		if(avoidHttpRedirect != null && avoidHttpRedirect.equals("AVOID_HTTP_REDIRECT")) {
			JSONObject json;
			try {
				json = new JSONObject(getErrorJsonString("User not authenticated."));
				json.put("loginURL",userService.createLoginURL("/"));
				//String errString = getURLEncodedString(json.toString());
				resp.setContentType("application/json; charset=utf-8");
				resp.getWriter().println(json.toString());
				return;
			} catch (JSONException e) {
				log.severe(e.getMessage());
			}
		}
		resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
	}
	
	public User authorizeAdmin(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		User user = authorizeUser(req, resp);
		UserService userService = UserServiceFactory.getUserService();
		if(user != null && !userService.isUserAdmin()){
			failUser(userService, req, resp);
		}
		return user;
	}
	
//	protected String getURLEncodedString(String string){
//		try {
//			return URLEncoder.encode(string, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			log.severe(e.getMessage());	
//			return "%7B%22error%22%3A%22Return+data+URL+Encoding+failed%22%7D";
//		}
//	}
	protected String getErrorJsonString(String msg){
		return "{\"error\":\"" + msg + "\"}";
	}
}
