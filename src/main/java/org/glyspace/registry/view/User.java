package org.glyspace.registry.view;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.view.validation.Password;
import org.glyspace.registry.view.validation.Username;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 
 * This class is used to collect user information from the client
 * View of UserEntity
 * @author sena
 *
 */
@XmlRootElement(name="user-input")
@ApiModel (value="User", description="User representation")
public class User {
	
	String loginId;
	String password;
	String fullName;
	String affiliation;
	String email;
	
	/**
	 * @return the loginId
	 */
	@XmlAttribute
	@NotEmpty
	@Username
	@ApiModelProperty(required=true)
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
	 * @return the password
	 */
	@XmlAttribute
	@NotEmpty
	@Password
	@ApiModelProperty(required=true)
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the fullName
	 */
	@XmlAttribute
	@NotEmpty
	@ApiModelProperty(required=true)
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
	@ApiModelProperty(required=false)
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
	 * @return the email
	 */
	@XmlAttribute
	@NotEmpty
	@Email
	@ApiModelProperty(required=true)
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
