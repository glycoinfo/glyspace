package org.glyspace.registry.dao.exceptions;

@SuppressWarnings("serial")
public class UserRoleViolationException extends RuntimeException {
	public UserRoleViolationException() { super(); }
	public UserRoleViolationException(String s) { super(s); }
	public UserRoleViolationException(String s, Throwable throwable) { super(s, throwable); }
	public UserRoleViolationException(Throwable throwable) { super(throwable); }
}
