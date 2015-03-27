package com.referaice.model.entitties;

public class User extends BaseEntity<String> {

	private String firstName;

	private String companyWebsite;

	private String lastName;

	private String email;

	private String password;

	private String grantedAuthorities = Role.ROLE_USER.getName();

	private SocialMediaService signInProvider;

	public User() {
	}

	public User(User user) {
		super.setId(user.getId());
		this.companyWebsite = user.companyWebsite;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.password = user.password;
		this.grantedAuthorities = user.grantedAuthorities;
		this.signInProvider = user.signInProvider;
	}

	public User(final String email) {
		this.email = email;
	}

	public User(String email, String password) {
		this.email = email;
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGrantedAuthorities() {
		return grantedAuthorities;
	}

	public void setGrantedAuthorities(String grantedAuthorities) {
		this.grantedAuthorities = grantedAuthorities;
	}

	public SocialMediaService getSignInProvider() {
		return signInProvider;
	}

	public void setSignInProvider(SocialMediaService signInProvider) {
		this.signInProvider = signInProvider;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("ID[").append(getId()).append("], User[").append(this.email).append("].")
				.toString();
	}

}
