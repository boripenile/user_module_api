package app.services.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.javalite.activejdbc.LazyList;
import org.joda.time.DateTime;

import com.google.inject.Inject;

import app.dto.LoggedUserDTO;
import app.dto.PasswordParams;
import app.dto.RegistrationDTO;
import app.dto.UsersOrganisationDTO;
import app.mail.EmailService;
import app.mail.Mail;
import app.models.Address;
import app.models.Application;
import app.models.Organisation;
import app.models.Role;
import app.models.User;
import app.models.UsersOrganisations;
import app.models.UsersRoles;
import app.services.AuthService;
import app.services.RoleService;
import app.services.UserService;
import app.utils.CommonUtil;
import app.utils.HashPassword;
import app.utils.Utils;
import freemarker.template.TemplateException;

public class UserServiceImpl implements UserService {

	@Inject
	private AuthService authService;

	@Inject
	private RoleService roleService;
	
	static Properties properties = null;

	static {
		properties = CommonUtil.loadPropertySettings("mail");
	}

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
		} catch (Exception e) {
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
		} catch (Exception e) {
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
	public String registerUser(RegistrationDTO registration) throws Exception {
		if (!CommonUtil.checkInternetConnectivity()) {
			throw new Exception("No internet connectivity.");
		}
		if (registration.getAppCode() != null) {
			Application application = checkApplication(registration.getAppCode());
			LazyList<Role> roles = null;
			Organisation myOrganisation = null;
			if (registration.getOrganisation() != null) {
				System.out.println("I am loading roles...");
				roles = Role.findBySQL("select roles.* from roles where roles.role_name=?", "admin");
				int length = roles.size();
				if (length == 0) {
					throw new Exception("Invalid role name");
				}
				return registerUserInformation(registration, roles, null, application);
			} else if (registration.getOrganisationCode() != null) {
				myOrganisation = checkOrganisation(registration.getOrganisationCode());
				roles = Role.findBySQL("select roles.* from roles where roles.role_name=?", registration.getRoleName());
				int length = roles.size();
				if (length == 0) {
					throw new Exception("Invalid role name");
				}
				return registerUserInformation(registration, roles, myOrganisation, application);
			} else {
				return registerUserInformation(registration, null, null, application);
			}
		} else {
			throw new Exception("Application code is required.");
		}
	}

	private String registerUserInformation(RegistrationDTO registration, LazyList<Role> roles, 
			Organisation myOrganisation, Application application)
			throws Exception, MessagingException, IOException, TemplateException {
		if (registration.getEmailAddress() != null && registration.getPhoneNumber() != null
				&& registration.getUsername() != null && registration.getPassword() != null) {

			checkEmailAndUsername(registration);
			checkPhoneNumber(registration);

			String verificationCodePhone = Utils.genVerificationCode();
			String verificationCodeEmail = Utils.genVerificationCode();

			Utils.sendVerificationSMS(new String[] { registration.getPhoneNumber().trim() }, verificationCodePhone,
					Boolean.FALSE, Boolean.FALSE, "APP");

			boolean sent = sendVerificationEmail(registration, application, verificationCodeEmail);

			if (sent) {
				if (registration.getOrganisation() != null) {
					Organisation organisation = createOrganistion(registration, application);
					System.out.println(roles.get(0).toJson(true));
					return createUserDetails(registration, roles, application, organisation, 
							verificationCodePhone, verificationCodeEmail);
				} else if (myOrganisation != null) {
					return createUserDetails(registration, roles, application, myOrganisation,
							verificationCodePhone, verificationCodeEmail);
				} else {
					return createUserDetails(registration, null, application, null,
							verificationCodePhone, verificationCodeEmail);
				}
			} else {
				return null;
			}
		} else {
			throw new Exception("Email address, phone number and username are required.");
		}
	}

	private boolean sendVerificationEmail(RegistrationDTO registration, Application application,
			String verificationCodeEmail) throws MessagingException, IOException, TemplateException {
		Mail mail = new Mail();
		mail.setTo(registration.getEmailAddress());
		mail.setSubject("Verify Email");
		Map<String, Object> model = new HashMap<>();
		model.put("app_name", application.get("app_name"));
		model.put("action_url", properties.getProperty("verify_email_url") + verificationCodeEmail);
		mail.setModel(model);
		return EmailService.sendSimpleMessage(mail, "verify-email.ftl");
	}

	private Organisation createOrganistion(RegistrationDTO registration, Application application) throws Exception, IOException {
		String parentReferralCode = null;
		if (registration.getOrganisation().getReferralCode() != null 
				&& !registration.getOrganisation().getReferralCode().isEmpty()) {
			if (parentReferralCodeExist(registration.getOrganisation().getReferralCode())) {
				parentReferralCode = registration.getOrganisation().getReferralCode();
			}
		}
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
		organisation.set("motto",
				registration.getOrganisation().getMotto() != null ? registration.getOrganisation().getMotto() : null);
		organisation.set("parent_referral_code", parentReferralCode != null ? parentReferralCode : null);
		organisation.set("created_by", registration.getUsername());
		organisation.setParent(application);
		if (organisation.save()) {
			roleService.copyRolePermissions("admin", organisationCode);
			roleService.copyRolePermissions("client", organisationCode);
			roleService.copyRolePermissions("staff", organisationCode);
			roleService.copyRolePermissions("customer", organisationCode);
		} else {
			throw new Exception("Unable to save organisation provided. Please try again");
		}
		return organisation;
	}

	private String createUserDetails(RegistrationDTO registration, LazyList<Role> roles, Application application,
			Organisation organisation, String verificationCodePhone, String verificationCodeEmail) throws Exception{
		Address address = new Address();
		address.set("phone_number", registration.getPhoneNumber());
		address.set("created_by", registration.getUsername());
		if (address.save()) {
			User user = new User();
			user.set("username", registration.getUsername());
			try {
				user.set("password", HashPassword.hashPassword(registration.getPassword()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			user.set("email_address", registration.getEmailAddress());
			user.set("first_name", registration.getFirstName());
			user.set("last_name", registration.getLastName());
			user.set("phone_verification_code", verificationCodePhone);
			user.set("email_verification_code", verificationCodeEmail);
			user.set("verification_expired_date", getExpiryDate());
			user.set("created_by", registration.getUsername());
			user.set("active", 0);
			user.setParent(address);
			if (user.save()) {
				if (organisation != null) {
					UsersOrganisations usersOrgs = new UsersOrganisations();
					usersOrgs.set("user_id", user.getId());
					usersOrgs.set("organisation_id", organisation.getId());
					usersOrgs.set("application_id", application.getId());
					if (usersOrgs.save()) {
						UsersRoles userRole = new UsersRoles();
						userRole.set("user_id", user.getId());
						userRole.set("role_id", roles.get(0).getId());
						userRole.set("organisation_id", organisation.getId());
						if (userRole.save()) {
							return "Verification code sent to your phone number and email address. ";
						}
					}
				} else {
					return "Verification code sent to your phone number and email address. ";
				}
			} else {
				address.delete();
			}
		}
		return "Unable to complete registration. Please try again";
	}

	private Date getExpiryDate() {
		DateTime expiryDate = DateTime.now();
		int days = 7;
		DateTime increasedDate = expiryDate.plusDays(days);
		return increasedDate.toDate();
	}

	private void checkEmailAndUsername(RegistrationDTO registration) throws Exception {
		User existedUser = User.findFirst("email_address=? or username=?", registration.getEmailAddress(),
				registration.getUsername());
		System.out.println(existedUser);
		if (existedUser != null) {
			System.out.println("Existing user");
			throw new Exception("Email address or username already exist.");
		} else {
			return;
		}
	}

	private void checkPhoneNumber(RegistrationDTO registration) throws Exception {
		Address existedAddress = Address.findFirst("phone_number=?", registration.getPhoneNumber());
		System.out.println(existedAddress);
		if (existedAddress != null) {
			System.out.println("Phone number");
			throw new Exception("Phone number already exist.");
		} else {
			return;
		}
	}

	private Application checkApplication(String appCode) throws Exception {
		Application application = Application.findFirst("app_code=?", appCode);
		if (application != null) {
			return application;
		}
		throw new Exception("Application does not exist.");
	}

	private Organisation checkOrganisation(String organisationCode) throws Exception {
		Organisation organisation = Organisation.findFirst("code=?", organisationCode);
		if (organisation != null) {
			return organisation;
		}
		throw new Exception("Organisation does not exist.");
	}

	private boolean parentReferralCodeExist(String parentReferralCode) throws Exception {
		Organisation organisation = Organisation.findFirst("referral_code=?", parentReferralCode);
		if (organisation != null) {
			return true;
		}
		throw new Exception("Referral code does not exist.");
	}

	@Override
	public String verifyPhoneNumber(String verificationCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoggedUserDTO verifyEmailAddress(String verificationCode, String appCode) throws Exception {
		if (verificationCode == null) {
			throw new Exception("Email verification code is required");
		}
		if (verificationCode.length() < 6 || verificationCode.length() > 6) {
			throw new Exception("Invalid email verification code");
		}
		LazyList<User> users = User.findBySQL("select users.* from users where email_verification_code=?",
				verificationCode);
		int length = users.size();
		if (length == 0) {
			throw new Exception("Email verification code does not exist or has been verified by you.");
		}
		User user = users.get(0);
		user.set("email_verified_date", new Date());
		user.set("email_verification_code", null);
		user.set("email_verified", true);
		user.set("active", true);
		if (user.save()) {
			return authService.login(user.getString("username"), appCode);
		}
		throw new Exception("Invalid email verification code or it has been verified");
	}

	@Override
	public void requestPasswordChangeUsingPhoneNumber(String phoneNumber) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String requestPasswordChangeUsingEmailAddress(String emailAddress) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetPassword(PasswordParams passwordParams) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public LazyList<User> getUsersByOrganisationCode(String organisationCode) throws Exception {
		try {
			LazyList<User> users = User.findBySQL("select users.* from users inner join users_organisations "
					+ "on users.id=users_organisations.user_id inner join organisations on "
					+ "organisations.id=users_organisations.organisation_id where organisations.code=? "
					+ "and organisations.active=?", organisationCode, 1);
			if (users.size() == 0) {
				throw new Exception("No users found for the organisation code provided");
			} else {
				return users;
			}
		} catch (Exception e) {
			throw new Exception("No users found for the organisation code provided");
		}
	}

	@Override
	public boolean validateReferralCode(String referralCode) throws Exception {
		try {
			LazyList<Organisation> organisations = Organisation.findBySQL("select organisations.* from organisations"
					+ " where organisations.referral_code=? and organisations.active=?", referralCode, 1);
			if (organisations.size() == 0) {
				throw new Exception("We cannot verify the referral code. Please check it.");
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new Exception("We cannot verify the referral code. Please check it.");
		}
	}

	@Override
	public User getUserByEmailOrUsername(String uniqueParameter) throws Exception {
		try {
			User user = User.findFirst("username=? or email_address=? and active=?", uniqueParameter, uniqueParameter, 1);
			user.set("password", null);
			return user;
		} catch (Exception e){
			throw new Exception("We cannot verify the user unique identifier. Please check.");
		}
	}

	@Override
	public String[] addUsersToOrganisation(UsersOrganisationDTO usersOrganisation) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] removeUsersFromOrganisation(UsersOrganisationDTO usersOrganisation) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
