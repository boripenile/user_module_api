package app.services;

import app.dto.RegistrationDTO;

import org.javalite.activejdbc.LazyList;

import app.dto.PasswordParams;
import app.models.User;

public interface UserService extends ModelService<User>{

	 public void registerUser(RegistrationDTO registration) throws Exception;
	 
	 public void verifyPhoneNumber(String verificationCode) throws Exception;
	 
	 public void verifyEmailAddress(String verificationCode) throws Exception;
	 
	 public void requestPasswordChangeUsingPhoneNumber(String phoneNumber) throws Exception;
	 
	 public void requestPasswordChangeUsingEmailAddress(String emailAddress) throws Exception;
	 
	 public void resetPassword(PasswordParams passwordParams) throws Exception;
	 
	 public LazyList<User> getUsersByOrganisationCode(String organisationCode) throws Exception;
	 
}
