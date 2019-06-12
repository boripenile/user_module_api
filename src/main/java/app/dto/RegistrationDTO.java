package app.dto;

public class RegistrationDTO {

	private String appCode;
	
	private String organisationCode;
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private String emailAddress;
	
	private String phoneNumber;

	private OrganisationRegistrationDTO organisation;
	
	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public OrganisationRegistrationDTO getOrganisation() {
		return organisation;
	}

	public void setOrganisation(OrganisationRegistrationDTO organisation) {
		this.organisation = organisation;
	}

	public String getOrganisationCode() {
		return organisationCode;
	}

	public void setOrganisationCode(String organisationCode) {
		this.organisationCode = organisationCode;
	}
	
}
