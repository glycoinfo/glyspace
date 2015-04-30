package org.glyspace.registry.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.glyspace.registry.dao.exceptions.ErrorCodes;
import org.glyspace.registry.dao.exceptions.ErrorMessage;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.http.HttpStatus;

import ch.qos.logback.classic.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
 
public class MyAccessDeniedHandler implements AccessDeniedHandler {
	
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.security.MyAccessDeniedHandler");
 
	public MyAccessDeniedHandler() {
	}
 
	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException,
		ServletException {
  
		Throwable mostSpecificCause = accessDeniedException.getCause();
	    ErrorMessage errorMessage;
	    List<String> errors = new ArrayList<String>();
	    errors.add (request.getUserPrincipal() + " is not allowed to access " + request.getRequestURI());
	    if (mostSpecificCause != null) {
	    //	String exceptionName = mostSpecificCause.getClass().getName();
	    	String message = mostSpecificCause.getMessage();
	    	//errors.add(exceptionName);
	    	errors.add(message);
	    	errorMessage = new ErrorMessage(errors);
	    } else {
	    	errors.add(accessDeniedException.getMessage());
	    	errorMessage = new ErrorMessage(errors);
	    }
	    errorMessage.setStatus(HttpStatus.FORBIDDEN.value());
	    errorMessage.setErrorCode(ErrorCodes.ACCESS_DENIED);
	    logger.warn("Access is Denied: {}", errorMessage);
		String acceptString = request.getHeader("Accept");
 		if (acceptString.contains("json")) {
			ObjectMapper jsonMapper = new ObjectMapper();          
			response.setContentType("application/json;charset=UTF-8");         
			response.setStatus(HttpStatus.FORBIDDEN.value());           
			PrintWriter out = response.getWriter();         
			out.print(jsonMapper.writeValueAsString(errorMessage));       
		} else {
			
			response.setContentType("application/xml;charset=UTF-8");
			response.setStatus(HttpStatus.FORBIDDEN.value());           
			PrintWriter out = response.getWriter();    
			try {
				JAXBContext errorContext = JAXBContext.newInstance(ErrorMessage.class);
				Marshaller errorMarshaller = errorContext.createMarshaller();
				errorMarshaller.marshal(errorMessage, out);  
			} catch (JAXBException jex) {
				logger.error("Cannot generate error message in xml: {}", jex.getStackTrace());
			}
		}
	}
}
