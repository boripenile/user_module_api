package app.dto;

import java.io.Serializable;

import app.models.User;

public class LoggedUserDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private User user;

	private String token;
	
	private String[] roles;
	
	private PermissionsDTO permissions;
	
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

	
}
