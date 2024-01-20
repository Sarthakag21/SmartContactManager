package com.smart.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Fetch user from Database
		User user = userRepository.getUserByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException("Could not find user!");
		}

		// If the user is not null, return a CustomUserDetails who is authorized based on role
		return new CustomUserDetails(user);
	}
}
