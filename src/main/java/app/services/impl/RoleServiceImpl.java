package app.services.impl;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import app.models.Role;
import app.services.RoleService;

public class RoleServiceImpl implements RoleService{

	@Override
	public LazyList<Role> findAll() throws Exception {
		try {
			Base.open();
			LazyList<Role> roles = Role.findAll();
			int length = roles.size();
			if (length == 0) {
				throw new Exception("No roles found");
			}
			return roles;
		} finally {
			Base.close();
		}
	}

	@Override
	public Role findById(String id) throws Exception {
		try {
			Base.open();
			Role role = Role.findById(id);
			if (role != null) {
				return role;
			}
			throw new Exception("No role found with id: " + id);
		} finally {
			Base.close();
		}
	}

	@Override
	public Role update(Role model) throws Exception {
		try {
			Base.open();
			if (!model.save()) {
				return model;
			}
			Base.commitTransaction();
			return model;
		} catch(Exception e) {
			Base.rollbackTransaction();
			throw new Exception(e);
		} finally { 
			Base.close();
		}
	}

	@Override
	public Role create(Role model) throws Exception {
		try {
			Base.open();
			if (!model.save()) {
				return model;
			}
			Base.commitTransaction();
			return model;
		} catch(Exception e) {
			Base.rollbackTransaction();
			throw new Exception(e);
		} finally { 
			Base.close();
		}
	}

	@Override
	public int count() throws Exception {
		try {
			Base.open();
			int count = Role.count().intValue();
			if (count != 0) {
				throw new Exception("No roles found to count");
			}
			return count;
		} finally {
			Base.close();
		}
	}

	@Override
	public boolean exist(String id) throws Exception {
		try {
			Base.open();
			Role role = Role.findById(id);
			return role != null ? true : false;
		} finally {
			Base.close();
		}
	}

	
}
