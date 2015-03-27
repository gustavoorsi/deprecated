
package com.referaice.web.config.security.userrepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.referaice.model.entitties.User;
import com.referaice.model.repository.mongo.UserRepository;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;

	@Autowired
	public UserRepositoryUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

		return new CustomUserDetails(user);
	}

}
