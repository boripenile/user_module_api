package app.controllers;

import org.javalite.activejdbc.LazyList;
import org.javalite.activeweb.AppController;
import org.javalite.activeweb.annotations.GET;
import org.javalite.activeweb.annotations.RESTful;
import org.javalite.common.Collections;
import com.google.inject.Inject;

import app.models.Role;
import app.services.RoleService;

@RESTful
public class RolesController extends AppController{

	@Inject
	private RoleService roleService;
	
	public void index() {
		try {
			LazyList<?> roles = roleService.findAll();
			if (roles != null) {
				render("/system/message", Collections.map("code", 200, "data", roles.toJson(true)));
			} else {
				render("/system/error", Collections.map("code", 400, "message", "No roles"));
			}
		} catch (Exception e) {
			logError(e.toString(), e);
			e.printStackTrace();
			render("/system/error", Collections.map("code", 400, "message", e.toString()));
		}
	}
	
	@GET
	public void show() {
		try {
			if(header("id") != null) {
				String id = header("id");
				Role role = roleService.findById(id);
				if (role != null) {
					render("/system/message", Collections.map("code", 200, "data", role.toJson(true)));
				} else {
					render("/system/error", Collections.map("code", 400, "message", "No role found with id: " + id));
				}
			} else {
				render("/system/error", Collections.map("code", 400, "message", "id is required as a header parameter"));
			}
		} catch (Exception e) {
			logError(e.toString(), e);
			e.printStackTrace();
			render("/system/error", Collections.map("code", 400, "message", e.toString()));
		}
	}
	@Override
	protected String getContentType() {
		return "application/json";
	}
	
	@Override
	protected String getLayout() {
		return null;
	}
}
