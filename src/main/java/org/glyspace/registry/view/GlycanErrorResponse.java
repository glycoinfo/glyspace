package org.glyspace.registry.view;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glyspace.registry.dao.exceptions.ErrorCodes;

@XmlRootElement(name="GlycanErrorResponse")
public class GlycanErrorResponse {

	String structure;
	String errorMessage;
	int statusCode;
	ErrorCodes errorCode;
	
	/**
	 * @return the structure
	 */
	@XmlJavaTypeAdapter(value=CDATAAdapter.class)
	public String getStructure() {
		return structure;
	}
	/**
	 * @param structure the structure to set
	 */
	public void setStructure(String structure) {
		this.structure = structure;
	}
	/**
	 * @return the errorMessage
	 */
	@XmlElement
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the statusCode
	 */
	@XmlAttribute
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * @param status the statusCode to set
	 */
	public void setStatusCode(int status) {
		this.statusCode = status;
	}
	/**
	 * @return the errorCode
	 */
	public ErrorCodes getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(ErrorCodes errorCode) {
		this.errorCode = errorCode;
	}
}
