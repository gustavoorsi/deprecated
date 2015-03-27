package com.referaice.web.service.impl;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.referaice.model.entitties.User;
import com.referaice.model.repository.mongo.UserRepository;
import com.referaice.web.form.RegistrationForm;
import com.referaice.web.service.UserService;
import com.referaice.web.service.exceptions.DuplicateEmailException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	@Override
	public User registerNewUserAccount(RegistrationForm userAccountData) throws DuplicateEmailException {
		if (emailExist(userAccountData.getEmail())) {
			throw new DuplicateEmailException("The email address: " + userAccountData.getEmail() + " is already in use.");
		}

		String encodedPassword = encodePassword(userAccountData);

		User user = new User();
		user.setCompanyWebsite(userAccountData.getCompanyWebsite());
		user.setEmail(userAccountData.getEmail());
		user.setFirstName(userAccountData.getFirstName());
		user.setLastName(userAccountData.getLastName());
		user.setPassword(encodedPassword);

		if (userAccountData.isSocialSignIn()) {
			user.setSignInProvider(userAccountData.getSignInProvider());
		}

		return userRepository.save(user);
	}

	private boolean emailExist(String email) {

		try {
			userRepository.findByEmail(email).get();
		} catch (NoSuchElementException ex) {
			return false;
		}

		return true;
	}

	private String encodePassword(RegistrationForm dto) {
		String encodedPassword = null;

		if (dto.isNormalRegistration()) {
			encodedPassword = passwordEncoder.encode(dto.getPassword());
		}

		return encodedPassword;
	}

}
