package org.glyspace.registry.security.janrain;

import java.util.List;

public class JanrainException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	List<String> fieldErrors;

	public JanrainException(String message) {
		super(message);
	}
	
	public JanrainException(String message, List<String> fieldErrors) {
		super(message);
		this.fieldErrors = fieldErrors;
	}

	public JanrainException(String message, Throwable cause) {
		super(message, cause);
	}

}