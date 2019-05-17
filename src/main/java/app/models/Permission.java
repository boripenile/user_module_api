package app.models;

import org.javalite.activejdbc.Model;

public class Permission extends Model{
	static {
		validatePresenceOf("permission_name", "description");
	}
}
