package org.glyspace.registry.view;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.wordnik.swagger.annotations.ApiModel;

@XmlRootElement(name="GlycanResponse")  
@ApiModel (value="GlycanResponse", description = "Glycan accession number together with existing/pending flags")
public class GlycanResponse {
	String structure;
	String accessionNumber;
	Boolean existing;
	Boolean pending;
	Boolean quotaExceeded;
	
	/**
	 * @return the accessionNumber
	 */
	@XmlAttribute
	public String getAccessionNumber() {
		return accessionNumber;
	}
	/**
	 * @param accessionNumber the accessionNumber to set
	 */
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	/**
	 * @return the existing
	 */
	@XmlAttribute
	public Boolean getExisting() {
		return existing;
	}
	/**
	 * @param existing the existing to set
	 */
	public void setExisting(Boolean existing) {
		this.existing = existing;
	}
	/**
	 * @return the pending
	 */
	@XmlAttribute
	public Boolean getPending() {
		return pending;
	}
	/**
	 * @param pending the pending to set
	 */
	public void setPending(Boolean pending) {
		this.pending = pending;
	}
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
	public Boolean getQuotaExceeded() {
		return quotaExceeded;
	}
	public void setQuotaExceeded(Boolean quotaExceeded) {
		this.quotaExceeded = quotaExceeded;
	}
	
}
