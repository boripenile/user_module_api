package app.models;

import org.javalite.activejdbc.Model;

public class Role extends Model{

	static {
		validatePresenceOf("role_name").message("role.rolename.required");
		validatePresenceOf("description").message("permission.description.required");;
	}
}
