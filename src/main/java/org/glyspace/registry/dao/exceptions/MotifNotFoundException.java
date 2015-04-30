package org.glyspace.registry.dao.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Motif does not exist")  // 404
public class MotifNotFoundException extends RuntimeException {
	public MotifNotFoundException() { super(); }
	public MotifNotFoundException(String s) { super(s); }
	public MotifNotFoundException(String s, Throwable throwable) { super(s, throwable); }
	public MotifNotFoundException(Throwable throwable) { super(throwable); }
}
