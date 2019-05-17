package app.services.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.javalite.activejdbc.LazyList;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

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

	@Override
	public LoggedUserDTO login(String username, String password) throws InvalidCredentialsException, JoseException {
		try {
			String hashedPassword = HashPassword.hashPassword(password);
			
			User user = User.findFirst("username=? and password=?", username, hashedPassword);
			LoggedUserDTO loggedUser = addRolesAndPermissions(user);
			
			
			return loggedUser;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
            Token login = CommonUtil.getJsonConvertor().fromJson(plaintext, Token.class);
            if (System.currentTimeMillis() - login.getTimestamp() <= Constants.TOKEN_TTL_MS) {
            	return login.getUsername();
            }
        }
        return null;
	}

	@Override
	public String generateToken(String username) throws JoseException, InvalidCredentialsException {
		User user = User.findFirst("username=?", username);
		String token = addRolesAndPermissions(user).getToken();
        return token;
	}

	@Override
	public boolean isValid(String username, String password) {
		try {
			return User.findFirst("username=? and password=?", username, HashPassword.hashPassword(password)) == null ? false : true;
		} catch (NoSuchAlgorithmException e) {
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
		List<Role> roles = Role.findBySQL("select roles.* from roles inner join users_roles on users_roles.role_id=roles.id "
				+ "inner join users on users_roles.user_id=users.id where users.id=?", user.getId());
		int length = roles.size();
		PermissionsDTO userPermissions = null;
		if (length > 0) {
			userPermissions = computePermissions(logged, roles, length);
		}
		List<Permission> permissions = Permission.findBySQL("select permissions.* from permissions inner join users_permissions "
				+ "on users_permissions.permission_id=permissions.id " + 
			"inner join users on users_permissions.user_id=users.id where users.id=?", user.getId());
		
		int pemLength = permissions.size();
		if (pemLength > 0) {
			String[] strPermissions = new String[pemLength];
			for (int j = 0; j < pemLength; j++) {
				strPermissions[j] = permissions.get(j).getString("permission_name");
			}
			userPermissions.setSelf(strPermissions);
		}
		try {
			String token = generateToken(user.getString("username"), logged.getRoles(), 
					logged.getPermissions().getSelf(), logged.getPermissions().getBasic());
			logged.setToken(token);
		} catch (JoseException e) {
			e.printStackTrace();
		}
		logged.setPermissions(userPermissions);
		logged.setUser(user);
		return logged;
	}

	private PermissionsDTO computePermissions(LoggedUserDTO logged, List<Role> roles, int length) {
		PermissionsDTO userPermissions;
		userPermissions = new PermissionsDTO();
		String[] strRoles = new String[length];
		for (int i = 0; i < length; i++) {
			strRoles[i] = roles.get(i).getString("role_name");
		}
		logged.setRoles(strRoles);
		List<String> totalPermissions = new ArrayList<>();;
		for (String role : strRoles) {
			computeRoles(totalPermissions, role);
		}
		if (totalPermissions.size() > 0) {
			String[] perms = new String[totalPermissions.size()];
			for (int k=0; k < totalPermissions.size(); k++) {
				perms[k] = totalPermissions.get(k);
			}
			userPermissions.setBasic(perms);
		}
		return userPermissions;
	}

	private void computeRoles(List<String> totalPermissions, String role) {
		List<Permission> permissions = Permission.findBySQL("select permissions.* from permissions inner join roles_permissions "
				+ "on roles_permissions.permission_id=permissions.id " + 
			"inner join roles on roles_permissions.role_id=roles.id where roles.role_name=? and roles.active=?", 
			role, 1);
		
		int pemLength = permissions.size();
		if (pemLength > 0) {
			List<String> strPermissions = new ArrayList<>();
			for (int j = 0; j < pemLength; j++) {
				strPermissions.add(permissions.get(j).getString("permission_name"));
			}
			totalPermissions.addAll(strPermissions);
		}
	}
	
	
	private static String generateToken(String username, String[] roles, 
			String[] self, String[] basic) throws JoseException {
		JsonWebSignature jws = new JsonWebSignature();
		Properties tokenize = CommonUtil.loadPropertySettings("tokenize");
		Token login = new Token(username, System.currentTimeMillis());
		if (roles != null && roles.length > 0) {
			login.setRoles(roles);
		}
		if (basic != null && basic.length > 0) {
			login.setBasicPermissions(basic);
		}
		if (self != null && self.length > 0) {
			login.setSelfPermissions(self);
		}
		jws.setHeader("typ", "JWT");
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
		jws.setPayload(CommonUtil.getJsonConvertor().toJson(login));
		jws.setKey(new HmacKey(tokenize.getProperty("token_secret").getBytes()));
		jws.setDoKeyValidation(false);

		return jws.getCompactSerialization();
	}
}
