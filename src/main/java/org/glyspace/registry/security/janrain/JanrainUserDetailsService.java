package org.glyspace.registry.security.janrain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.glyspace.registry.dao.exceptions.UserNotFoundException;
import org.glyspace.registry.database.RoleEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.service.UserManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ch.qos.logback.classic.Logger;

public class JanrainUserDetailsService implements
		AuthenticationUserDetailsService<JanrainAuthenticationToken> {
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.security.JanrainUserDetailsService");
	private static final List<GrantedAuthority> DEFAULT_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_USER");
	
	@Autowired
	UserManager userManager;
	
	@Override
	public UserDetails loadUserDetails(JanrainAuthenticationToken token)
			throws UsernameNotFoundException {
		String id = token.getIdentifier();

        boolean userNotFound = false;
        
        try {
	        UserEntity domainUser = userManager.getUserByOpenIdLogin(id, false, false);
	        if (domainUser != null) {
	        	if (!domainUser.getValidated()) {
					// cannot use the system yet.
					logger.info("The user {} is still waiting for approval. Please contact the Administrator", id);
					throw new BadCredentialsException("The user" + id + " is still waiting for approval. Please contact the Administrator");
				}
	        	if (!domainUser.getActive()) {
	        		// cannot use the system yet.
					logger.info("The user {} is deactivated. Please contact the Administrator", id);
					throw new BadCredentialsException("The user" + id + " is deactivated. Please contact the Administrator");
	        	}
	        	boolean enabled = true;
				boolean accountNonExpired = true;
				boolean credentialsNonExpired = true;
				boolean accountNonLocked = true;
				List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
	    		//check the user's roles
	    		Set<RoleEntity> userRoles = domainUser.getRoles();
	    		for (Iterator<RoleEntity> iterator = userRoles.iterator(); iterator
						.hasNext();) {
					RoleEntity roleEntity = (RoleEntity) iterator.next();
					grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + roleEntity.getRoleName()));
				}
				return new User(
					domainUser.getLoginId(),
					"token",
					enabled,
					accountNonExpired,
					credentialsNonExpired,
					accountNonLocked,
					grantedAuths
				);
	        }
	        else {
	        	userNotFound = true;
	        }
        } catch (UserNotFoundException e) {
        	userNotFound = true;
        }
        
        // signed in to the system for the first time, register the user automatically 
        if (userNotFound) {
        	String email = token.getEmail();
        	if (null == email)
        		email = token.getVerifiedEmail();
            String fullName = token.getName();
            
            User user = new User(id, "unused", true,
            		true,
            		true,
            		true,
    				DEFAULT_AUTHORITIES);
            
            // save the user in our user database
            UserEntity domainUser = new UserEntity();
            domainUser.setLoginId(id);  // user can change it later
            domainUser.setOpenIdLogin(id);
            domainUser.setEmail(email);
            domainUser.setPassword("token");
            domainUser.setUserName(fullName);

            if (email == null || fullName == null) {
            	// cannot register the user
            	List<String> fieldErrors = new ArrayList<String>();
            	if (email == null) {
           			fieldErrors.add("Email cannot be obtained");
            	}
            	if (fullName == null) {
            		fieldErrors.add("Fullname cannot be obtained");
            	}
            	throw new JanrainException("The user " + id + " does not exist and cannot be registered", fieldErrors);
            }
            userManager.addUser(domainUser);

            return user;
        }

        return null;
	}

}
