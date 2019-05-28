package app.services;

import org.javalite.activejdbc.LazyList;

import app.dto.RolesPermissionsDTO;
import app.dto.UsersRolesDTO;
import app.models.Permission;
import app.models.Role;
import app.models.User;

public interface RoleService extends ModelService<Role> {
	
	public String[] addUsersToRoles(UsersRolesDTO usersRoles);
	
	public String[] removeUsersFromRoles(UsersRolesDTO usersRoles);
	
	public String addPermissionsToRoles(RolesPermissionsDTO rolesPerms);
	
	public String[] removePermissionsFromRoles(RolesPermissionsDTO rolesPerms);
	
	public LazyList<User> findUsersByRoleName(String roleName);
	
	public LazyList<User> findUsersByRoleId(String roleId);
	
	public LazyList<Permission> findPermissionsByRoleId(String roleId);
	
	public String updateUsersRoles(UsersRolesDTO usersRoles, boolean active);
	
	public String updateRolesPermissions(RolesPermissionsDTO rolesPerms, boolean active);
	
}
