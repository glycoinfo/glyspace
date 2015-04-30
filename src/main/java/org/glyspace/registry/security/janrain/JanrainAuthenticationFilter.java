/*
 *  (C) Copyright 2012 Insula Tecnologia da Informacao Ltda.
 *
 *  This file is part of spring-security-janrain.
 *
 *  spring-security-janrain is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  spring-security-janrain is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with spring-security-janrain.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.glyspace.registry.security.janrain;

import static org.glyspace.registry.security.janrain.Janrain.JANRAIN_URI;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ch.qos.logback.classic.Logger;

public class JanrainAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.security.janrain.JanrainAuthenticationFilter");
	
	private JanrainService janrainService;

	protected JanrainAuthenticationFilter() {
		super(JANRAIN_URI);
	}

	/**
	 *
	 * 
	 * 
	 */
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String token = request.getParameter("token");
		logger.debug(token);

		Marker notifyAdmin = MarkerFactory.getMarker("NOTIFY_ADMIN");
		logger.info(notifyAdmin, "token {}", token);
		
		if (token != null && !token.isEmpty()) {
			try {
				JanrainAuthenticationToken authentication = janrainService.authenticate(token);
				if (authentication != null) {
					return getAuthenticationManager().authenticate(authentication);
				}
				else {
					throw new AuthenticationServiceException(
							"Unable to parse authentication. Is your 'applicationName' correct?");
				}
			}
			catch (Exception ex) {
				throw new BadCredentialsException(ex.getMessage(), ex);
			}
		}

		return null;
	}

	@Required
	public void setJanrainService(JanrainService janrainService) {
		this.janrainService = janrainService;
	}
}