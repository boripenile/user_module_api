package app.dto;

import java.io.Serializable;

import app.models.Application;
import app.models.Organisation;
import app.models.User;

public class LoggedUserDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private User user;

	private String token;
	
	private String[] roles;
	
	private PermissionsDTO permissions;
	
	private Application application;
	
	private Organisation organisation;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public PermissionsDTO getPermissions() {
		return permissions;
	}

	public void setPermissions(PermissionsDTO permissions) {
		this.permissions = permissions;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}
