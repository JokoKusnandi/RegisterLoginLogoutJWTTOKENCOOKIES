package com.joko.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joko.models.User;
import com.joko.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	 @Autowired
	  UserRepository userRepository;

	  @Override
	  @Transactional
	  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User user = userRepository.findByUsername(username)
	        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

	    return UserDetailsImpl.build(user);
	  }
	  
		/*
		 * private final static String USER_NOT_FOUND_MSG =
		 * "user with email %s not found"; private final UserRepository userRepository;
		 * 
		 * @Override public UserDetails loadUserByUsername(String email) throws
		 * UsernameNotFoundException { return userRepository.findByEmail(email)
		 * .orElseThrow(() -> new UsernameNotFoundException(String.format(
		 * USER_NOT_FOUND_MSG, email ))); }
		 */
	  
	  //Optional <User> user = userRepository.findByEmail(email);
	 // if (user.isEmpty()) user = userRepository.findByUsername(email);
	  //if you are using non optional you just check if the user==null instead of isEmp
	
}
