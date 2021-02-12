package com.socialloginspringboot.googlefacebookloginoauth2.servicies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.socialloginspringboot.googlefacebookloginoauth2.entities.User;

/**
 * @author Gisele Galaburri <gisele.galaburri at gmail.com>
 */

@Repository
public interface UserRepository extends JpaRepository<User, String>{

}
