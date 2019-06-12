package app.services.impl;

import java.util.List;

import org.javalite.activejdbc.LazyList;

import com.google.inject.Inject;

import app.dto.PermissionsDTO;
import app.dto.RoleDTO;
import app.dto.RolesPermissionsDTO;
import app.dto.UsersPermissionsDTO;
import app.dto.UsersRolesDTO;
import app.models.Organisation;
import app.models.Permission;
import app.models.Role;
import app.models.RolesPermissions;
import app.models.User;
import app.models.UsersPermissions;
import app.models.UsersRoles;
import app.services.AuthService;
import app.services.RoleService;

public class RoleServiceImpl implements RoleService{

	@Inject
	private AuthService authService;
	
	@Override
	public LazyList<Role> findAll() throws Exception {
		try {
			LazyList<Role> roles = Role.findAll();
			int length = roles.size();
			if (length == 0) {
				throw new Exception("No roles found");
			}
			return roles;
		} finally {
		}
	}

	@Override
	public Role findById(String id) throws Exception {
		try {
			Role role = Role.findById(id);
			if (role != null) {
				return role;
			}
			throw new Exception("No role found with id: " + id);
		} finally {
		}
	}

	@Override
	public Role update(Role model) throws Exception {
		try {
			if (!model.save()) {
				return model;
			}
			return model;
		} catch(Exception e) {
			throw new Exception(e);
		} finally { 
		}
	}

	@Override
	public Role create(Role model) throws Exception {
		try {
			if (!model.save()) {
				return model;
			}
			return model;
		} catch(Exception e) {
			throw new Exception(e);
		} finally { 
		}
	}

	@Override
	public int count() throws Exception {
		try {
			int count = Role.count().intValue();
			if (count != 0) {
				throw new Exception("No roles found to count");
			}
			return count;
		} finally {
		}
	}

	@Override
	public boolean exist(String id) throws Exception {
		try {
			Role role = Role.findById(id);
			return role != null ? true : false;
		} finally {
		}
	}

	@Override
	public String[] addUsersToRoles(UsersRolesDTO usersRoles) throws Exception {
		try {
			if (usersRoles != null && 
					(usersRoles.getRoleNames().length > 0 && usersRoles.getUserIds().length > 0)) {
				LazyList<Organisation> organisations = Organisation.findBySQL("select organisations.* from organisations "
						+ "where organisations.code", usersRoles.getOrgCode());
				int total = usersRoles.getRoleNames().length * usersRoles.getUserIds().length;
				String[] totalRoles = new String[total];
				for (int i = 0; i < usersRoles.getUserIds().length; i++) {
					try {
						User user = User.findById(usersRoles.getUserIds()[i]);
						if (user != null) {
							for (int j=0; j < usersRoles.getRoleNames().length; j++) {
								try {
									LazyList<Role> role = Role.findBySQL("select roles.* from roles where roles.role_name=?", 
											usersRoles.getRoleNames()[j]);
									if (role.size() > 0) {
										UsersRoles userRole = new UsersRoles();
										userRole.set("user_id", user.get("id"));
										userRole.set("role_id", role.get(0).get("id"));
										userRole.set("organisation_id", organisations.get(0).get("id"));
										if (!userRole.save()) {
											continue;
										} else {
											totalRoles[i*j] = role.get(0).getString("role_name");
										}
									}
								} catch (Exception e) {
									continue;
								}
							}
						}
					} catch (Exception e) {
						continue;
					}
				}
				return totalRoles;
			}
			throw new Exception("User ids and role names are required");
		} finally {
		}
	}


	@Override
	public boolean delete(String id) throws Exception {
		try {
			Role role = Role.findById(id);
			if (role != null) {
				int count = Role.delete("id=?", id);
				if (count > 0) {
					return true;
				}
			}
			throw new Exception("No role found with id: " + id);
		} finally {
			//Base.close();
		}
	}

	@Override
	public String[] removeUsersFromRoles(UsersRolesDTO usersRoles) throws Exception {
		try {
			if (usersRoles != null && 
					(usersRoles.getRoleNames().length > 0 && usersRoles.getUserIds().length > 0)) {
				int total = usersRoles.getRoleNames().length * usersRoles.getUserIds().length;
				String[] totalUsers = new String[total];
				for (int i = 0; i < usersRoles.getRoleNames().length; i++) {
					for (int j=0; j < usersRoles.getUserIds().length; j++) {
						try {
							LazyList<UsersRoles> userRoles = 
									UsersRoles.findBySQL("select users_roles.* from users_roles inner join roles "
											+ "on roles.id=users_roles.role_id inner join users on users.id=users_roles.user_id "
											+ "inner join organisations on users_roles.organisation_id=organisations.id "
											+ "where users.id=? and roles.role_name=? and organisations.code=?", 
											usersRoles.getUserIds()[j], usersRoles.getRoleNames()[i], 
											usersRoles.getOrgCode());
							int length = userRoles.size();
							if (length > 0) {
								if (!userRoles.get(0).delete()) {
									continue;
								} else {
									totalUsers[i*j] = usersRoles.getUserIds()[j];
								}								
							} 
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
				}
				return totalUsers;
			}
			throw new Exception("User ids and role names are required");
		} finally {
		}
	}

	@Override
	public String[] addPermissionsToRoles(RolesPermissionsDTO rolesPerms) throws Exception {
		try {
			if (rolesPerms != null && 
					(rolesPerms.getPermissionNames().length > 0 && rolesPerms.getRolesNames().length > 0)) {
				LazyList<Organisation> organisations = Organisation.findBySQL("select organisations.* from organisations "
						+ "where organisations.code", rolesPerms.getOrgCode());
				int total = rolesPerms.getRolesNames().length * rolesPerms.getPermissionNames().length;
				String[] totalRoles = new String[total];
				for (int i = 0; i < rolesPerms.getPermissionNames().length; i++) {
					try {
						LazyList<Permission> perm = Permission.findBySQL("select permissions.* from permissions "
								+ "where permissions.permission_name=?", 
								rolesPerms.getPermissionNames()[i]);
						if (perm.size() > 0) {
							for (int j=0; j < rolesPerms.getRolesNames().length; j++) {
								try {
									LazyList<Role> role = Role.findBySQL("select roles.* from roles where roles.role_name=?", 
											rolesPerms.getRolesNames()[j]);
									if (role.size() > 0) {
										RolesPermissions rolePerm = new RolesPermissions();
										rolePerm.set("permission_id", perm.get(0).get("id"));
										rolePerm.set("role_id", role.get(0).get("id"));
										rolePerm.set("organisation_id", organisations.get(0).get("id"));
										if (!rolePerm.save()) {
											continue;
										} else {
											totalRoles[i*j] = role.get(0).getString("role_name");
										}
									}
								} catch (Exception e) {
									continue;
								}
							}
						}
					} catch (Exception e) {
						continue;
					}	
				}
				return totalRoles;
			}
			throw new Exception("Permission names and role names are required");
		} finally {
		}
	}

	@Override
	public String[] removePermissionsFromRoles(RolesPermissionsDTO rolesPerms) throws Exception {
		try {
			if (rolesPerms != null && 
					(rolesPerms.getRolesNames().length > 0 && rolesPerms.getPermissionNames().length > 0)) {
				int total = rolesPerms.getRolesNames().length * rolesPerms.getPermissionNames().length;
				String[] totalPerms = new String[total];
				for (int i = 0; i < rolesPerms.getRolesNames().length; i++) {
					for (int j=0; j < rolesPerms.getPermissionNames().length; j++) {
						try {
							List<RolesPermissions> rolePerms = 
									UsersRoles.findBySQL("select roles_permissions.* from roles_permissions inner join roles "
											+ "on roles.id=roles_permissions.role_id inner join permissions on "
											+ "permissions.id=roles_permissions.permission_id "
											+ "inner join organisations on roles_permissions.organisation_id=organisations.id "
											+ "where permissions.permission_name=? and roles.role_name=? and organisations.code=?", 
											rolesPerms.getPermissionNames()[j].trim(), rolesPerms.getRolesNames()[i].trim(), 
											rolesPerms.getOrgCode());
							if (rolePerms.size() > 0) {
								rolePerms.get(0).delete();
								totalPerms[i*j] = rolesPerms.getPermissionNames()[j];
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
				return totalPerms;
			}
			throw new Exception("Permission and role names are required");
		} finally {
		}
	}

	@Override
	public LazyList<User> findUsersByRoleName(String roleName, String orgCode) throws Exception {
		try {
			try {
				LazyList<Role> roles = Role.findBySQL("select roles.* from roles "
						+ "where roles.role_name=?", 
						roleName);
				if(roles.size() > 0) {
					try {
						LazyList<User> users = UsersRoles.findBySQL("select users.* from users inner join users_roles "
								+ "on users.id=users_roles.user_id inner join roles on roles.id=users_roles.role_id "
								+ "inner join organisations "
								+ "on users_roles.organisation_id=organisations.id where "
								+ "roles.id=? and organisations.code=?", roles.get(0).getId(), orgCode);
						if (users.size() > 0) {
							return users;
						}
					} catch (Exception e) {
						throw new Exception("No users found for role: " + roleName);
					}
				}
			} catch (Exception e) {
				throw new Exception("No role found for roleName: " + roleName + ", orgCode: " + orgCode);
			}
			throw new Exception("roleName and appCode are required.");
		} finally {
			// TODO: handle finally clause
		}
	}

	@Override
	public LazyList<User> findUsersByRoleId(String roleId, String orgCode) throws Exception {
		try {
			try {
				LazyList<Role> roles = Role.findBySQL("select roles.* from roles "
						+ "where roles.id=?", 
						roleId);
				if(roles.size() > 0) {
					try {
						LazyList<User> users = UsersRoles.findBySQL("select users.* from users inner join users_roles "
								+ "on users.id=users_roles.user_id inner join roles on roles.id=users_roles.role_id "
								+ "inner join organisations "
								+ "on users_roles.organisation_id=organisations.id where "
								+ "roles.id=? and organisations.code=?", roles.get(0).getId(), orgCode);
						if (users.size() > 0) {
							return users;
						}
					} catch (Exception e) {
						throw new Exception("No users found for role id: " + roleId);
					}
				}
			} catch (Exception e) {
				throw new Exception("No role found for role id: " + roleId + ", orgCode: " + orgCode);
			}
			throw new Exception("roleName and appCode are required.");
		} finally {
			// TODO: handle finally clause
		}
	}

	@Override
	public LazyList<Permission> findPermissionsByRoleId(String roleId) throws Exception {
		try {
			try {
				LazyList<Role> roles = Role.findBySQL("select roles.* from roles "
						+ "where roles.id=?", roleId);
				if(roles.size() > 0) {
					try {
						LazyList<Permission> users = UsersRoles.findBySQL("select permissions.* from permissions "
								+ "inner join roles_permissions "
								+ "on permissions.id=roles_permissions.permission_id inner join roles on "
								+ "roles.id=roles_permissions.role_id where "
								+ "roles.id=?", roles.get(0).getId());
						if (users.size() > 0) {
							return users;
						}
					} catch (Exception e) {
						throw new Exception("No users found for role id: " + roleId);
					}
				}
			} catch (Exception e) {
				throw new Exception("No role found for role id: " + roleId);
			}
			throw new Exception("roleName and appCode are required.");
		} finally {
			// TODO: handle finally clause
		}
	}

	@Override
	public String[] updateUsersRoles(UsersRolesDTO usersRoles, boolean active) throws Exception {
		try {
			if (usersRoles != null && 
					(usersRoles.getUserIds().length > 0 && usersRoles.getRoleNames().length > 0)) {
				int total = usersRoles.getUserIds().length * usersRoles.getRoleNames().length;
				String[] totalUsers = new String[total];
				for (int j=0; j < usersRoles.getRoleNames().length; j++) {
					for (int i = 0; i < usersRoles.getUserIds().length; i++) {
						try {
							LazyList<UsersRoles> roles = UsersRoles.findBySQL("select users_roles.* from users_roles "
									+ "inner join roles on roles.id=users_roles.role_id inner join users on "
									+ "users.id=users_roles.user_id inner join organisations on "
									+ "users_roles.organisation_id=organisations.id "
									+ "where users.id=? and organisations.code=? and roles.role_name=?", 
									usersRoles.getUserIds()[i].trim(), usersRoles.getOrgCode(), 
									usersRoles.getRoleNames()[j].trim());
							if (roles.size() > 0) {
								UsersRoles userRole = roles.get(0);
								if (active) {
									userRole.set("active", 1);
								}else {
									userRole.set("active", 0);
								}
								if (!userRole.save()) {
									continue;
								} else {
									totalUsers[i*j] = userRole.getString("user_id");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
				}
				return totalUsers;
			}
			throw new Exception("Permission names and role names are required");
		} finally {
		}
	}     

	@Override
	public String[] updateRolesPermissions(RolesPermissionsDTO rolesPerms, boolean active) throws Exception {
		try {
			if (rolesPerms != null && 
					(rolesPerms.getPermissionNames().length > 0 && rolesPerms.getRolesNames().length > 0)) {
				int total = rolesPerms.getRolesNames().length * rolesPerms.getPermissionNames().length;
				String[] totalRoles = new String[total];
				for (int j=0; j < rolesPerms.getRolesNames().length; j++) {
					for (int i = 0; i < rolesPerms.getPermissionNames().length; i++) {
						try {
							LazyList<RolesPermissions> perm = RolesPermissions.findBySQL("select roles_permissions.* from roles_permissions "
									+ "inner join roles on roles.id=roles_permissions.role_id inner join permissions on "
									+ "permissions.id=roles_permissions.permission_id inner join organisations on "
									+ "roles_permissions.organisation_id=organisations.id "
									+ "where permissions.permission_name=? and organisations.code=? and roles.role_name=?", 
									rolesPerms.getPermissionNames()[i].trim(), rolesPerms.getOrgCode(), 
									rolesPerms.getRolesNames()[j].trim());
							if (perm.size() > 0) {
								RolesPermissions rolePerm = perm.get(0);
								if (active) {
									rolePerm.set("active", 1);
								}else {
									rolePerm.set("active", 0);
								}
								if (!rolePerm.save()) {
									continue;
								} else {
									totalRoles[i*j] = rolePerm.getString("role_id");
								}
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
				return totalRoles;
			}
			throw new Exception("Permission names and role names are required");
		} finally {
		}
	}

	@Override
	public String[] removeRolesFromUsers(UsersRolesDTO usersRoles) throws Exception {
		try {
			if (usersRoles != null && 
					(usersRoles.getRoleNames().length > 0 && usersRoles.getUserIds().length > 0)) {
				LazyList<Organisation> organisations = Organisation.findBySQL("select organisations.* from organisations "
						+ "where organisations.code", usersRoles.getOrgCode());
				int total = usersRoles.getUserIds().length * usersRoles.getRoleNames().length;
				String[] totalRoles = new String[total];
				for (int j=0; j < usersRoles.getUserIds().length; j++) {
					try {
						User user = User.findById(usersRoles.getUserIds()[j]);
						if (user != null) {
							for (int i = 0; i < usersRoles.getRoleNames().length; i++) {
								try {
									LazyList<Role> role = Role.findBySQL("select roles.* from roles "
											+ "where roles.role_name=?", 
											usersRoles.getRoleNames()[i]);
									if (role.size() > 0) {
										List<UsersRoles> userRoles = 
												UsersRoles.find("user_id=? and role_id=? and organisation_id=?", 
														user.get("id"), role.get(0).get("id"), organisations.get(0).get("id"));
										if (userRoles.size() > 0) {
											userRoles.get(0).delete();
											totalRoles[i*j] = role.get(0).getString("role_name");
										}
									}
								} catch (Exception e) {
									continue;
								}						
							}
						}
					} catch (Exception e) {
						continue;
					}
				}
				return totalRoles;
			}
			throw new Exception("User ids and role names are required");
		} finally {
		}
	}

	@Override
	public PermissionsDTO findUserPermissionsByUserId(String userId, String appCode) throws Exception {
		try {
			PermissionsDTO permission = authService.getPermissionDTO(userId, appCode);
			if (permission != null) {
				return permission;
			}
			throw new Exception("No permissions found for the user.");
		} catch (Exception e) {
			throw new Exception("No permissions found for the user.");
		}
	}

	@Override
	public String[] updateUsersPermissions(UsersPermissionsDTO usersPermissions, boolean active) throws Exception {
		try {
			if (usersPermissions != null && 
					(usersPermissions.getUserIds().length > 0 && usersPermissions.getPermissionNames().length > 0)) {
				int total = usersPermissions.getUserIds().length * usersPermissions.getPermissionNames().length;
				String[] totalUsers = new String[total];
				for (int j=0; j < usersPermissions.getUserIds().length; j++) {
					for (int i = 0; i < usersPermissions.getPermissionNames().length; i++) {
						try {
							LazyList<UsersPermissions> perms = UsersPermissions.findBySQL("select users_permissions.* from users_permissions "
									+ "inner join permissions on permissions.id=users_permissions.permission_id inner join users on "
									+ "users.id=users_permissions.user_id inner join organisations on "
									+ "users_permissions.organisation_id=organisations.id "
									+ "where users.id=? and organisations.code=? and permissions.permission_name=?", 
									usersPermissions.getUserIds()[j].trim(), usersPermissions.getOrgCode(), 
									usersPermissions.getPermissionNames()[i].trim());
							if (perms.size() > 0) {
								UsersPermissions userPerm = perms.get(0);
								if (active) {
									userPerm.set("active", 1);
								}else {
									userPerm.set("active", 0);
								}
								if (!userPerm.save()) {
									continue;
								} else {
									totalUsers[i*j] = userPerm.getString("user_id");
								}
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
				return totalUsers;
			}
			throw new Exception("User ids and Permission names are required");
		} finally {
		}
	}

	@Override
	public RoleDTO[] findUserRolesByUserId(String userId, String orgCode) throws Exception {
		try {
			RoleDTO[] roles = authService.getRoleDTO(userId, orgCode);
			if (roles != null) {
				return roles;
			}
			throw new Exception("No roles found for the user.");
		} catch (Exception e) {
			throw new Exception("No roles found for the user.");
		}
	}

	
}
