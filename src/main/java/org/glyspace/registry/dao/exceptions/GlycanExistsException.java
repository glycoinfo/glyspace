package org.glyspace.registry.dao.exceptions;

@SuppressWarnings("serial")
public class GlycanExistsException extends RuntimeException {
	public GlycanExistsException() { super(); }
	public GlycanExistsException(String s) { super(s); }
	public GlycanExistsException(String s, Throwable throwable) { super(s, throwable); }
	public GlycanExistsException(Throwable throwable) { super(throwable); }
}
