package com.referaice.web.service;

import com.referaice.model.entitties.User;
import com.referaice.web.form.RegistrationForm;
import com.referaice.web.service.exceptions.DuplicateEmailException;

public interface UserService {

	/**
	 * Creates a new user account to the service.
	 * 
	 * @param userAccountData
	 *            The information of the created user account.
	 * @return The information of the created user account.
	 * @throws DuplicateEmailException
	 *             Thrown when the email address is found from the database.
	 */
	public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException;
}
