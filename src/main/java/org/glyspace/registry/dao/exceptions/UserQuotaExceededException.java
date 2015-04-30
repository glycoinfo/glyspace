package org.glyspace.registry.dao.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="User quota exceeded")  
public class UserQuotaExceededException extends RuntimeException {
	
	public UserQuotaExceededException() { super(); }
	public UserQuotaExceededException(String s) { super(s); }
	public UserQuotaExceededException(String s, Throwable throwable) { super(s, throwable); }
	public UserQuotaExceededException(Throwable throwable) { super(throwable); }
}
