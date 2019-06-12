package app.services.impl;

import java.io.IOException;

import org.javalite.activejdbc.LazyList;

import app.dto.ExtraData;
import app.dto.ImageDTO;
import app.dto.PasswordParams;
import app.dto.RegistrationDTO;
import app.models.Address;
import app.models.Application;
import app.models.Organisation;
import app.models.Role;
import app.models.User;
import app.services.UserService;
import app.utils.CommonUtil;
import app.utils.Utils;

public class UserServiceImpl implements UserService {

	@Override
	public LazyList<?> findAll() throws Exception {
		try {
			LazyList<User> users = User.findAll();
			int length = users.size();
			if (length == 0) {
				throw new Exception("No users found");
			}
			return users;
		} finally {
		}
	}

	@Override
	public User findById(String id) throws Exception {
		try {
			User user = User.findById(id);
			if (user != null) {
				return user;
			}
			throw new Exception("No user found with id: " + id);
		} finally {
		}
	}

	@Override
	public User update(User model) throws Exception {
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
	public User create(User model) throws Exception {
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
	public boolean delete(String id) throws Exception {
		try {
			User user = User.findById(id);
			if (user != null) {
				int count = User.delete("id=?", id);
				if (count > 0) {
					return true;
				}
			}
			throw new Exception("No user found with id: " + id);
		} finally {
		}
	}

	@Override
	public int count() throws Exception {
		try {
			int count = User.count().intValue();
			if (count != 0) {
				throw new Exception("No users found to count");
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
	public void registerUser(RegistrationDTO registration) throws Exception {
		try {
			// Start
			if (registration.getAppCode() != null) {
				Application application = checkApplication(registration.getAppCode());
				if (registration.getOrganisation() != null) {
					String parentReferralCode = null;
					if (registration.getOrganisation().getReferralCode() != null) {
						if (parentReferralCodeExist(registration.getOrganisation().getReferralCode())) {
							parentReferralCode = registration.getOrganisation().getReferralCode();
						}
					}
					if (registration.getEmailAddress() != null && registration.getPhoneNumber() != null 
							&& registration.getUsername() != null) {
						
						checkEmailAndUsername(registration);
						checkPhoneNumber(registration);
						
						Address address = new Address();
						address.set("phone_number", registration.getPhoneNumber());
						address.set("created_by", registration.getUsername());
						
						if (address.save()) {
							String organisationCode = Utils.genOrganisationCode();
							String referralCode = Utils.genReferralCode();
							Organisation organisation = new Organisation();
							
							if (registration.getOrganisation().getImage() != null) {
								String imageUrl = CommonUtil.uploadImageRemotely(registration.getOrganisation().getImage(), 
										organisationCode);
								organisation.set("image_url", imageUrl);
							}
							
							organisation.set("code", organisationCode);
							organisation.set("referral_code", referralCode);
							organisation.set("name", registration.getOrganisation().getOrganisationName());
							organisation.set("description", registration.getOrganisation().getWorkingDescription());
							organisation.set("motto", registration.getOrganisation().getMotto() != null ? 
									registration.getOrganisation().getMotto() : null);
							organisation.set("parent_referral_code", parentReferralCode != null ? parentReferralCode : null);
							organisation.set("created_by", registration.getUsername());
							
							if (!organisation.save()) {
								throw new Exception("Unable to save organisation provided. Please try again");
							}
							
							User user = new User();
							user.set("username", registration.getUsername());
							user.set("email_address", registration.getEmailAddress());
							user.set("first_name", registration.getFirstName());
							user.set("last_name", registration.getLastName());
							user.setParent(address);
							user.save();
							
							
						}
					}else {
						throw new Exception("Email address, phone number and username are required.");
					}
				} else if (registration.getOrganisationCode() != null) {
					Organisation myOrganisation = checkOrganisation(registration.getOrganisationCode());
				} else {
					// add user to the top organisation
					
				}
			}
			
			// Check if email address, phone number and username already existed.
			// Check if organisation object is present, 
			// then check if organisation name already taken.
			// Send phone verification code to the phone number
			// Send email address verification code to the email address
			// Save the organisation
			// Save address for organisation using phone number
			// Save using information
			// End
			throw new Exception("Application code is required.");
		} catch(Exception e) {
			throw new Exception(e);
		}
	}

	private void checkEmailAndUsername(RegistrationDTO registration) throws Exception{
		try {
			User existedUser = User.findFirst("email_address=? and username=?", registration.getEmailAddress(), 
					registration.getUsername());
			if (existedUser != null) {
				throw new Exception("Email address and username already exist.");
			}
		} catch (Exception e) {
		}
	}
	
	private void checkPhoneNumber(RegistrationDTO registration) throws Exception{
		try {
			Address existedAddress = Address.findFirst("phone_number=?", registration.getPhoneNumber());
			if (existedAddress != null) {
				throw new Exception("Phone number already exist.");
			}
		} catch (Exception e) {
		}
	}
	
	private Application checkApplication(String appCode) throws Exception{
		try {
			Application application = Application.findFirst("app_code=?", appCode);
			if (application != null) {
				return application;
			}
			
		} catch (Exception e) {
		}
		throw new Exception("Application does not exist.");
	}
	
	private Organisation checkOrganisation(String organisationCode) throws Exception{
		try {
			Organisation organisation = Organisation.findFirst("code=?", organisationCode);
			if (organisation != null) {
				return organisation;
			}
		} catch (Exception e) {
		}
		throw new Exception("Organisation does not exist.");
	}
	
	private boolean parentReferralCodeExist(String parentReferralCode) throws Exception{
		try {
			Organisation organisation = Organisation.findFirst("parent_referral_code=?", parentReferralCode);
			if (organisation != null) {
				return true;
			}
		} catch (Exception e) {
		}
		throw new Exception("Referral code does not exist.");
	}
	@Override
	public void verifyPhoneNumber(String verificationCode) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyEmailAddress(String verificationCode) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestPasswordChangeUsingPhoneNumber(String phoneNumber) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestPasswordChangeUsingEmailAddress(String emailAddress) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetPassword(PasswordParams passwordParams) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public LazyList<User> getUsersByOrganisationCode(String organisationCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
