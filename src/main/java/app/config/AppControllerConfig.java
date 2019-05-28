package app.config;

import org.javalite.activeweb.AbstractControllerConfig;
import org.javalite.activeweb.AppContext;
import org.javalite.activeweb.controller_filters.TimingFilter;

import app.controllers.LoginController;
import app.filters.AuthorizationFilter;
import app.filters.CatchFilter;
import app.filters.CorsFilter;

@SuppressWarnings("rawtypes")
public class AppControllerConfig extends AbstractControllerConfig {

    @SuppressWarnings("unchecked")
	public void init(AppContext context) {
    	add(new CorsFilter());
    	add(new TimingFilter(), new CatchFilter());
    	add(new AuthorizationFilter()).exceptFor(LoginController.class);
    }
}
