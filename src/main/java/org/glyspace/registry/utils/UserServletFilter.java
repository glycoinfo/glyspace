package org.glyspace.registry.utils;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

public class UserServletFilter implements Filter {

	private final String USER_KEY = "username";
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		boolean successfulRegistration = false;
		HttpServletRequest req = (HttpServletRequest) request;
		Principal principal = req.getUserPrincipal();
		
		if (principal != null) {
			String username = principal.getName();
			successfulRegistration = registerUsername(username);
		}
  
		try {
			chain.doFilter(request, response);
		} finally {
			if (successfulRegistration) {
				MDC.remove(USER_KEY);
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	/**
     * Register the user in the MDC under USER_KEY.
     * 
     * @param username
     * @return true id the user can be successfully registered
     */
    private boolean registerUsername(String username) {
    	if (username != null && username.trim().length() > 0) {
    		MDC.put(USER_KEY, username);
    		return true;
    	}
    	return false;
    }
}
