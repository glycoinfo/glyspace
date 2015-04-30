package org.glyspace.registry.view;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.view.conversion.DateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name="contributor")
public class Contributor {

	String loginId;
	String fullName;
	String affiliation;
	Date dateRegistered;
	Date lastLoggedIn;
	GlycanList glycans;
	
	/**
	 * @return the loginId
	 */
	@XmlAttribute
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the fullName
	 */
	@XmlAttribute
	public String getFullName() {
		return fullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * @return the affiliation
	 */
	@XmlAttribute
	public String getAffiliation() {
		return affiliation;
	}
	/**
	 * @param affiliation the affiliation to set
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	/**
	 * @return the dateRegistered
	 */
	@JsonSerialize(using = DateSerializer.class)
	public Date getDateRegistered() {
		return dateRegistered;
	}
	/**
	 * @param dateRegistered the dateRegistered to set
	 */
	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	/**
	 * @return the lastLoggedIn
	 */
	@JsonSerialize(using = DateSerializer.class)
	public Date getLastLoggedIn() {
		return lastLoggedIn;
	}
	/**
	 * @param lastLoggedIn the lastLoggedIn to set
	 */
	public void setLastLoggedIn(Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}
	/**
	 * @return the glycans
	 */
	public GlycanList getGlycans() {
		return glycans;
	}
	/**
	 * @param glycans the glycans to set
	 */
	public void setGlycans(GlycanList glycans) {
		this.glycans = glycans;
	}
	
}
