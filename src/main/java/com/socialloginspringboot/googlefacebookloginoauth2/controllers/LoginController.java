package com.socialloginspringboot.googlefacebookloginoauth2.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.socialloginspringboot.googlefacebookloginoauth2.repositories.UserService;

@Controller
public class LoginController {
	
	private static String authoriationRequestBaseUri = "oauth2/authorization";
	Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@Autowired
	private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
	
	@Autowired
	UserService userService;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/login-success")
	public String login(Model model, OAuth2AuthenticationToken oauth2authentication) {
		OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(
				oauth2authentication.getAuthorizedClientRegistrationId(), oauth2authentication.getName());
		
		String userEndPointUri = client.getClientRegistration()
								.getProviderDetails()
								.getUserInfoEndpoint()
								.getUri();
		
		if (!ObjectUtils.isEmpty(userEndPointUri)) {
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
			
			
			HttpEntity<String> httpEntity = new HttpEntity<>(headers);
			ResponseEntity<Map> responseEntity = restTemplate.exchange(
					userEndPointUri, HttpMethod.GET, httpEntity, Map.class);
			
			Map userAttributes = responseEntity.getBody();
			
			String provider = oauth2authentication.getAuthorizedClientRegistrationId();
			String userId = "";
			String userName = userAttributes.get("name").toString();
			String userEmail = userAttributes.get("email").toString();
			String userPicUrl = "";
			
			if (provider.equals("google")) {
				userId = userAttributes.get("sub").toString();
				userPicUrl = userAttributes.get("picture").toString();
			} else if (provider.equals("facebook")) {
				userId = userAttributes.get("id").toString();
				userPicUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";
			}
			
			model.addAttribute("name", userName);
			model.addAttribute("id", userId);
			model.addAttribute("email", userEmail);
			model.addAttribute("clientId", provider);
			
			model.addAttribute("picture", userPicUrl);
			System.out.println(userPicUrl);
			
			
			userService.create(userId, userName, userEmail, provider, userPicUrl);
			
		}
		
		return "success.html";

		
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/oauth_login")
	public String getLoginPage(Model model) {
		
		Iterable<ClientRegistration> clientRegistrations = null;
		ResolvableType resolvableType = ResolvableType
				.forInstance(clientRegistrationRepository).as(Iterable.class);
		
		if (resolvableType != ResolvableType.NONE 
				&& ClientRegistration.class.isAssignableFrom(resolvableType.resolveGenerics()[0])) {
			
			clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
		}
		
		clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), 
				authoriationRequestBaseUri + "/" + registration.getRegistrationId() ));
		
		model.addAttribute("urls", oauth2AuthenticationUrls);
		
		return "oauth_login";
	}

}
