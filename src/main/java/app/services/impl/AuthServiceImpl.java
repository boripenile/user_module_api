package app.services.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.common.JsonHelper;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.google.gson.Gson;

import app.dto.LoggedUserDTO;
import app.dto.PermissionsDTO;
import app.dto.Token;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.InvalidTokenException;
import app.models.Permission;
import app.models.Role;
import app.models.User;
import app.services.AuthService;
import app.utils.CommonUtil;
import app.utils.Constants;
import app.utils.HashPassword;

public class AuthServiceImpl implements AuthService {

	private String appCode = null;
	
	@Override
	public LoggedUserDTO login(String username, String password, String appCode) throws InvalidCredentialsException, JoseException {
		try {
			this.appCode = appCode;
			String hashedPassword = HashPassword.hashPassword(password);
			Base.open();
			LazyList<User> user = User.findBySQL("select users.* from users inner join users_applications "
					+ "on users_applications.user_id=users.id inner join applications on "
					+ "applications.id=users_applications.application_id where username=? and "
					+ "password=? and applications.app_code=?", username, hashedPassword, appCode);
			if (user.size() > 0) {
				LoggedUserDTO loggedUser = addRolesAndPermissions(user.get(0));
				return loggedUser;
			}
		} catch (NoSuchAlgorithmException e) {
			Base.rollbackTransaction();
			e.printStackTrace();
		} finally {
			Base.close();
		}
		return null;
	}

	@Override
	public String getToken(String username) throws JoseException, InvalidCredentialsException {
		return generateToken(username);
	}

	@Override
	public String getUsername(String token) throws JoseException, MalformedClaimException {
		if (token == null) {
			return null;
		}
		Properties tokenize = CommonUtil.loadPropertySettings("tokenize");
		JsonWebSignature receiverJws = new JsonWebSignature();
		receiverJws.setCompactSerialization(token);
		receiverJws.setKey(new HmacKey(tokenize.getProperty("token_secret").getBytes()));
		boolean signatureVerified = receiverJws.verifySignature();
		if (signatureVerified) {
			String plaintext = receiverJws.getPayload();
			Token login = new Gson().fromJson(plaintext, Token.class);
			if (System.currentTimeMillis() - login.getTimestamp() <= Constants.TOKEN_TTL_MS) {
				return login.getUsername();
			}
		}
		return null;
	}

	@Override
	public String generateToken(String username) throws JoseException, InvalidCredentialsException {
		try {
			Base.open();
			User user = User.findFirst("username=? and active=?", username, 1);
			String token = addRolesAndPermissions(user).getToken();
			return token;
		} finally {
			Base.close();
		}

	}

	@Override
	public boolean isValid(String username, String password) {
		try {
			Base.open();
			return User.findFirst("username=? and password=?", username, HashPassword.hashPassword(password)) == null
					? false
					: true;
		} catch (NoSuchAlgorithmException e) {
		} finally {
			Base.close();
		}
		return false;
	}

	@Override
	public String extendToken(String previousToken)
			throws JoseException, MalformedClaimException, InvalidCredentialsException, InvalidTokenException {
		String username = getUsername(previousToken);
		if (username == null) {
			throw new InvalidTokenException();
		}
		return generateToken(username);
	}

	private LoggedUserDTO addRolesAndPermissions(User user) {
		LoggedUserDTO logged = new LoggedUserDTO();
		List<Role> roles = Role
				.findBySQL("select roles.* from roles inner join users_roles on users_roles.role_id=roles.id "
						+ "inner join users on users_roles.user_id=users.id inner join applications "
						+ "on applications.id=roles.application_id inner join users_applications on "
						+ "users_applications.user_id=users.id where users.id=? and applications.app_code=?", 
						user.getId(), this.appCode);
		int length = roles.size();
		PermissionsDTO userPermissions = null;
		if (length > 0) {
			userPermissions = computeBasicPermissions(logged, roles, length);
		}
		List<Permission> permissions = Permission
				.findBySQL(
						"select permissions.* from permissions inner join users_permissions "
								+ "on users_permissions.permission_id=permissions.id "
								+ "inner join users on users_permissions.user_id=users.id inner join applications "
								+ "on applications.id=permissions.application_id inner join users_applications on " 
								+ "users_applications.user_id=users.id where users.id=? and applications.app_code=?",
						user.getId(), this.appCode);
		if (permissions != null) {
			int pemLength = permissions.size();
			if (pemLength > 0) {
				String[] strPermissions = new String[pemLength];
				for (int j = 0; j < pemLength; j++) {
					strPermissions[j] = permissions.get(j).getString("permission_name");
				}
				userPermissions.setSelf(strPermissions);
			}
		}
		userPermissions = confirmPermissions(userPermissions);
		logged.setPermissions(userPermissions);
		try {
			String token = generateToken(user, logged);
			logged.setToken(token);
		} catch (JoseException e) {
			e.printStackTrace();
		}
		logged.setPermissions(userPermissions);
		logged.setUser(user);
		return logged;
	}

	private PermissionsDTO confirmPermissions(PermissionsDTO userPermissions) {
		if (userPermissions == null) {
			userPermissions = new PermissionsDTO();
			userPermissions.setBasic(new String[] {});
			userPermissions.setSelf(new String[] {});
		} else if (userPermissions.getBasic() != null && userPermissions.getSelf() == null) {
			userPermissions.setSelf(new String[] {});
		} else if (userPermissions.getSelf() != null && userPermissions.getBasic() == null) {
			userPermissions.setBasic(new String[] {});
		}
		return userPermissions;
	}

	private String generateToken(User user, LoggedUserDTO logged) throws JoseException {
		String username = user.getString("username");
		Properties tokenize = CommonUtil.loadPropertySettings("tokenize");
		JsonWebSignature jws = new JsonWebSignature();

		Token login = new Token(username, System.currentTimeMillis());
		if (logged.getRoles() != null && logged.getRoles().length > 0) {
			login.setRoles(logged.getRoles());
		}
		if (logged.getPermissions() != null) {
			if (logged.getPermissions().getBasic() != null) {
				login.setBasicPermissions(logged.getPermissions().getBasic());
			}
			if (logged.getPermissions().getSelf() != null) {
				login.setSelfPermissions(logged.getPermissions().getSelf());
			}

		}

		jws.setHeader("type", "JWT");
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
		jws.setPayload(JsonHelper.toJsonString(login));
		jws.setKey(new HmacKey(tokenize.getProperty("token_secret").getBytes()));
		jws.setDoKeyValidation(false);
		String token = jws.getCompactSerialization();
		return token;
	}

	private PermissionsDTO computeBasicPermissions(LoggedUserDTO logged, List<Role> roles, int length) {
		PermissionsDTO userPermissions;
		userPermissions = new PermissionsDTO();
		String[] strRoles = new String[length];
		for (int i = 0; i < length; i++) {
			strRoles[i] = roles.get(i).getString("role_name");
		}
		logged.setRoles(strRoles);
		List<String> totalPermissions = new ArrayList<>();
		
		for (String role : strRoles) {
			computeRolePermissions(totalPermissions, role);
		}
		if (totalPermissions.size() > 0) {
			String[] perms = new String[totalPermissions.size()];
			for (int k = 0; k < totalPermissions.size(); k++) {
				perms[k] = totalPermissions.get(k);
			}
			userPermissions.setBasic(perms);
		}
		return userPermissions;
	}

	private void computeRolePermissions(List<String> totalPermissions, String role) {
		List<Permission> permissions = Permission.findBySQL(
				"select permissions.* from permissions inner join roles_permissions "
						+ "on roles_permissions.permission_id=permissions.id "
						+ "inner join roles on roles_permissions.role_id=roles.id inner join applications " 
						+ "on applications.id=permissions.application_id where roles.role_name=? "
						+ "and roles.active=? and applications.app_code=?",
				role, 1, this.appCode);
		if (permissions != null) {
			int pemLength = permissions.size();
			if (pemLength > 0) {
				List<String> strPermissions = new ArrayList<>();
				for (int j = 0; j < pemLength; j++) {
					strPermissions.add(permissions.get(j).getString("permission_name"));
				}
				totalPermissions.addAll(strPermissions);
			}
		}
	}

	@Override
	public Token getTokenObject(String token) throws JoseException, MalformedClaimException {
		if (token == null) {
			return null;
		}
		Properties tokenize = CommonUtil.loadPropertySettings("tokenize");
		JsonWebSignature receiverJws = new JsonWebSignature();
		receiverJws.setCompactSerialization(token);
		receiverJws.setKey(new HmacKey(tokenize.getProperty("token_secret").getBytes()));
		boolean signatureVerified = receiverJws.verifySignature();
		if (signatureVerified) {
			String plaintext = receiverJws.getPayload();
			Token login = new Gson().fromJson(plaintext, Token.class);
			if (System.currentTimeMillis() - login.getTimestamp() <= Constants.TOKEN_TTL_MS) {
				return login;
			}
		}
		return null;
	}

}
