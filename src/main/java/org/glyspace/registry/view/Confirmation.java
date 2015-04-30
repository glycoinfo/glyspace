package org.glyspace.registry.view;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;

@XmlRootElement(name="result")
@ApiModel(value="Confirmation", description="Confirmation message with status code")
public class Confirmation {
	
	public Confirmation() {
	}
	
	public Confirmation(String message, int statusCode) {
		this.statusCode = statusCode;
		this.message = message;
	}
	
	final static String status = "success";
	
	/**
	 * @return the status
	 */
	@XmlAttribute
	public String getStatus() {
		return status;
	}

	int statusCode;
	String message;
	
	/**
	 * @return the statusCode
	 */
	@XmlAttribute (name="code")
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the message
	 */
	@XmlAttribute
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
