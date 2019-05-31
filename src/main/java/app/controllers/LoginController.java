package app.controllers;

import org.javalite.activeweb.AppController;
import org.javalite.activeweb.annotations.OPTIONS;
import org.javalite.activeweb.annotations.POST;
import org.javalite.activeweb.annotations.RESTful;
import org.javalite.common.JsonHelper;

import com.google.gson.Gson;
import com.google.inject.Inject;

import app.dto.LoggedUserDTO;
import app.dto.UserLogin;
import app.services.AuthService;

/**
 * @author Igor Polevoy on 9/29/14.
 */
@RESTful
public class LoginController extends AppController {

	@Inject
	private AuthService authService;
	
	@POST
    public void create(){
    	try {
    		if (header("app_code") != null) {
    			String payload = getRequestString();
    			UserLogin userLogin = new Gson().fromJson(payload, UserLogin.class);
    			if (authService.isValid(userLogin.getUsername(), userLogin.getPassword())) {
    				LoggedUserDTO loggedUser = authService.login(userLogin.getUsername(), userLogin.getPassword(), 
    						header("app_code"));
    				loggedUser.getUser().set("password", null);
    				if (loggedUser.getApplication() != null) {
        				view("code", 200, "message", "Successful", "data", loggedUser.getUser().toJson(true),
        						"token", loggedUser.getToken(), "roles", JsonHelper.toJsonString(loggedUser.getRoles()),
        						"permissions", JsonHelper.toJsonString(loggedUser.getPermissions()), 
        						"application", loggedUser.getApplication() != null ? loggedUser.getApplication().toJson(true) : null,
        						"organisation", loggedUser.getOrganisation() != null ? loggedUser.getOrganisation().toJson(true) : null);
        				render("message");
    				} else {
        				view("code", 200, "messages", "Successful", "data", loggedUser.getUser().toJson(true),
        						"token", loggedUser.getToken(), "roles", JsonHelper.toJsonString(loggedUser.getRoles()),
        						"permissions", JsonHelper.toJsonString(loggedUser.getPermissions()));
    				}
    				
        		} else {
        			view("code", 400, "message", "Invalid username or password");
        			render("error");
        		}
    		} else {
    			view("code", 400, "message", "app_code is required in the header.");
    			render("error");
    		}
		} catch (Exception e) {
			view("code", 400, "message", e.getMessage());
			render("error");
		}
    }
    
	@Override
	protected String getLayout() {
		return null;
	}

	@Override
	protected String getContentType() {
		return "application/json";
	}
    
	public void options() {
		view("code", 400, "message", "Successful");
		render("error");
		return;
	}
}
