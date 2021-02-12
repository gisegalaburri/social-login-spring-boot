package com.socialloginspringboot.googlefacebookloginoauth2;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Gisele Galaburri <gisele.galaburri at gmail.com>
 */

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GoogleFacebookLoginOauth2Application.class);
	}

}