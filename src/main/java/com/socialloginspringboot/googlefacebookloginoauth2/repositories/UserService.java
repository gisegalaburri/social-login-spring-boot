package com.socialloginspringboot.googlefacebookloginoauth2.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socialloginspringboot.googlefacebookloginoauth2.entities.User;
import com.socialloginspringboot.googlefacebookloginoauth2.servicies.UserRepository;

/**
 * @author Gisele Galaburri <gisele.galaburri at gmail.com>
 */

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Transactional
	public void create(String id, String name, String email, String provider, String profilePicture) {

		User user = null;
		Optional<User> optional = userRepository.findById(id);
		if (!optional.isPresent()) {
			user = new User();
			user.setId(id);
			user.setName(name);
			user.setEmail(email);
			user.setProvider(provider);
			user.setProfilePicture(profilePicture);
		} else {
			user = optional.get();
			user.setId(id);
			user.setName(name);
			user.setEmail(email);
			user.setProvider(provider);
			user.setProfilePicture(profilePicture);
		}
		
		userRepository.save(user);

	}

}
