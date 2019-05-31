package app.controllers;

import org.javalite.activejdbc.LazyList;
import org.javalite.activeweb.AppController;
import org.javalite.activeweb.annotations.RESTful;
import org.javalite.common.Collections;
import org.javalite.common.JsonHelper;
import com.google.inject.Inject;

import app.models.Application;
import app.models.Role;
import app.services.AppService;
import app.services.RoleService;

@RESTful
public class RolesController extends AppController {

	@Inject
	private RoleService roleService;

	@Inject
	private AppService appService;

	public void index() {
		if (header("action") != null) {
			String actionName = header("action").toString();
			switch (actionName) {
			case "view":
				view();
				break;
			default:
				break;
			}
		} else {
			findAll();
		}
	}

	public void create() {
		if (header("action") != null) {
			String actionName = header("action").toString();
			switch (actionName) {
			case "updateRole":
				updateRole();
				break;
			case "createRole":
				createRole();
				break;
			default:
				break;
			}
		}
	}

	public void findAll() {
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
			render("/system/error", Collections.map("code", 400, "message", e.getMessage()));
		}
	}

	public void view() {
		try {
			if (header("id") != null) {
				String id = header("id");
				Role role = roleService.findById(id);
				if (role != null) {
					render("/system/message", Collections.map("code", 200, "data", role.toJson(true)));
				} else {
					render("/system/error", Collections.map("code", 400, "message", "No role found with id: " + id));
				}
			} else {
				render("/system/error",
						Collections.map("code", 400, "message", "id is required as a header parameter"));
			}
		} catch (Exception e) {
			logError(e.toString(), e);
			e.printStackTrace();
			render("/system/error", Collections.map("code", 400, "message", e.getMessage()));
		}
	}

	public void updateRole() {
		try {
			if (header("id") != null) {
				String id = header("id");
				Role roleExisted = roleService.findById(id);
				if (roleExisted != null) {
					String payload = getRequestString();
					Role updatedRole = roleExisted.fromMap(JsonHelper.toMap(payload));
					updatedRole = roleService.update(updatedRole);
					render("/system/message", Collections.map("code", 200, "data", updatedRole.toJson(true)));
				} else {
					render("/system/error", Collections.map("code", 400, "message", "No role exist with id: " + id));
				}
			} else {
				render("/system/error", Collections.map("code", 400, "message", "id is required as a header parameter"));
			}
			
		} catch (Exception e) {
			logError(e.toString(), e);
			e.printStackTrace();
			render("/system/error", Collections.map("code", 400, "message", e.getMessage()));
		}
	}

	public void createRole() {
		try {
			if (header("app_code") == null) {
				render("/system/error", Collections.map("code", 400, "message", "app_code is required as a parameter"));
			} else {
				String appCode = header("app_code");
				Application app = appService.findByAppCode(appCode);
				String payload = getRequestString();
				Role role = new Role();
				Role newRole = role.fromMap(JsonHelper.toMap(payload));
				newRole.setParent(app);
				newRole = roleService.create(newRole);
				if (newRole.getId() != null) {
					render("/system/message", Collections.map("code", 200, "data", newRole.toJson(true)));
				} else {
					render("/system/error", Collections.map("code", 400, "message", "Unable to create new role"));
				}
			}
		} catch (Exception e) {
			logError(e.toString(), e);
			e.printStackTrace();
			render("/system/error", Collections.map("code", 400, "message", e.getMessage()));
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
