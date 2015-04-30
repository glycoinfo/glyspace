package org.glyspace.registry.dao.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Glycan does not exist")  // 404
public class GlycanNotFoundException extends RuntimeException {
	public GlycanNotFoundException() { super(); }
	public GlycanNotFoundException(String s) { super(s); }
	public GlycanNotFoundException(String s, Throwable throwable) { super(s, throwable); }
	public GlycanNotFoundException(Throwable throwable) { super(throwable); }
}
