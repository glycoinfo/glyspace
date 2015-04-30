package org.glyspace.registry.security.janrain;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import ch.qos.logback.classic.Logger;

public class AuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	public static Logger logger = (Logger) LoggerFactory
			.getLogger("org.glyspace.registry.security.janrain.AuthenticationSuccessHandler");

	private AuthenticationUserDetailsService<JanrainAuthenticationToken> authenticationUserDetailsService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		// String token = request.getParameter("token");
		//
		// logger.debug("token:>" + token + "<");

		logger.debug("name:>" + authentication.getName());
		logger.debug("authentication:>" + authentication + "<");
		JanrainAuthenticationToken token = (JanrainAuthenticationToken) authentication;
		logger.debug("janrain:>" + token + "<");
		// try {
		// UserDetails userDetails =
		// authenticationUserDetailsService.loadUserDetails(token);
		// } catch (JanrainException e)
		// {
		// throw new BadCredentialsException(e.getMessage(), e);
		// }
		logger.debug("janrain return:>" + getDefaultTargetUrl() + URLEncoder.encode(token.getIdentifier(), "UTF-8") + "<");
		response.sendRedirect(getDefaultTargetUrl() + URLEncoder.encode(token.getIdentifier(), "UTF-8"));
	}

	public void setAuthenticationUserDetailsService(
			AuthenticationUserDetailsService<JanrainAuthenticationToken> authenticationUserDetailsService) {
		this.authenticationUserDetailsService = authenticationUserDetailsService;
	}
}