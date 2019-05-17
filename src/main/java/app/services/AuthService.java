package app.services;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;

import app.dto.LoggedUserDTO;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.InvalidTokenException;
import app.utils.Constants;

public interface AuthService {

	 	public LoggedUserDTO login(String username, String password) throws InvalidCredentialsException, JoseException;

	    public String getToken(String username) throws JoseException, InvalidCredentialsException;

	    public String extendToken(String previousToken) throws JoseException, MalformedClaimException, InvalidCredentialsException, InvalidTokenException;

	    public String getUsername(String token) throws JoseException, MalformedClaimException;

	    public String generateToken(String username) throws JoseException, InvalidCredentialsException;
	    
	    public boolean isValid(String username, String password);
	    
}
