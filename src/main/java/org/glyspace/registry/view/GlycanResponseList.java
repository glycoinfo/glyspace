package org.glyspace.registry.view;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GlycanResponseList")
public class GlycanResponseList {

	List <GlycanResponse> responses;
	List <GlycanErrorResponse> errorResponses;
	
	public GlycanResponseList() {
		this.responses = new ArrayList<GlycanResponse>();
		this.errorResponses = new ArrayList<GlycanErrorResponse>();
	}
	
	public GlycanResponseList(List<GlycanResponse> list, List<GlycanErrorResponse> errors) {
		this.responses = list;
		this.errorResponses=errors;
	}
	
	/**
	 * @param responses the responses to set
	 */
	public void setResponses(List<GlycanResponse> responses) {
		this.responses = responses;
	}

	@XmlElement(name="GlycanResponse")
	public List<GlycanResponse> getResponses() {
		return responses;
	}

	/**
	 * @return the errorResponses
	 */
	@XmlElement(name="GlycanErrorResponse")
	public List<GlycanErrorResponse> getErrorResponses() {
		return errorResponses;
	}

	/**
	 * @param errorResponses the errorResponses to set
	 */
	public void setErrorResponses(List<GlycanErrorResponse> errorResponses) {
		this.errorResponses = errorResponses;
	}
	
	
}
