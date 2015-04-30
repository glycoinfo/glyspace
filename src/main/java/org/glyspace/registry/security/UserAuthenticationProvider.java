package org.glyspace.registry.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.glyspace.registry.dao.exceptions.UserNotFoundException;
import org.glyspace.registry.database.RoleEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.security.janrain.JanrainAuthenticationToken;
import org.glyspace.registry.security.janrain.JanrainException;
import org.glyspace.registry.security.janrain.JanrainService;
import org.glyspace.registry.service.UserManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.qos.logback.classic.Logger;

public class UserAuthenticationProvider implements AuthenticationProvider{

	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.security.UserAuthenticationProvider");
	
	@Autowired
	UserManager userManager;
	
	@Autowired
	JanrainService janrainService;
	
	private AuthenticationUserDetailsService<JanrainAuthenticationToken> authenticationUserDetailsService;
	
	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public JanrainService getJanrainService() {
		return janrainService;
	}

	public void setJanrainService(JanrainService janrainService) {
		this.janrainService = janrainService;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		if (authentication instanceof JanrainAuthenticationToken) {
			JanrainAuthenticationToken token = (JanrainAuthenticationToken) authentication;
			logger.debug("janrain:>" + token + "<");
			try {
				UserDetails userDetails = authenticationUserDetailsService.loadUserDetails(token);
				return new JanrainAuthenticationToken(userDetails, userDetails.getAuthorities(), token);
			} catch (JanrainException e)
			{
				throw new BadCredentialsException(e.getMessage(), e);
			}
		}
		
		String username = authentication.getName();
        String password = authentication.getCredentials().toString();

		logger.debug("username:>" + username + "<");
		logger.debug("password:>" + password + "<");

        if (null != password && password.equals("token")) {
			try {
				JanrainAuthenticationToken janrainToken = getJanrainService().authenticate(username);
				if (null != janrainToken) {
					// already registered - not using janrain token.
					UserDetails userDetails = authenticationUserDetailsService.loadUserDetails(janrainToken);
					username = userDetails.getUsername();
				}
			} catch (JanrainException e)
			{
				throw new BadCredentialsException(e.getMessage(), e);
			}
        }

		logger.debug("username:>" + username + "<");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        try {
        	UserEntity user = userManager.getUserByLoginId (username, true, true); // only validated and active users can use the system
        	if (user == null) {
        		throw new BadCredentialsException("User " + username + " not found.");
        	}
        	
        	if (passwordEncoder.matches(password, user.getPassword()) || password.equals("token")) {
        		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        		//check the user's roles
        		Set<RoleEntity> userRoles = user.getRoles();
        		for (Iterator<RoleEntity> iterator = userRoles.iterator(); iterator
						.hasNext();) {
					RoleEntity roleEntity = (RoleEntity) iterator.next();
					grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + roleEntity.getRoleName()));
				}
                Authentication auth = new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
                // set user's last logged in date
                userManager.setLoggedinDate(user, new Date());
                return auth;
        	}
        	else {
        		throw new BadCredentialsException("Wrong password!");
        	}
        } catch (UserNotFoundException une) {
        	throw new BadCredentialsException("Username not found. The user might still be waiting for approval", une);
        }
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class) || 
				JanrainAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Required
	public void setAuthenticationUserDetailsService(
			AuthenticationUserDetailsService<JanrainAuthenticationToken> authenticationUserDetailsService) {
		this.authenticationUserDetailsService = authenticationUserDetailsService;
	}
	
}
