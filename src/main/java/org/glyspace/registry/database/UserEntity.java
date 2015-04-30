package org.glyspace.registry.database;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.glyspace.registry.view.conversion.DateSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author sena
 *
 */
@Entity
@Table(name="users")
@XmlRootElement (name="user")
@JsonSerialize
@JsonIgnoreProperties({"password", "openIdLogin", "quota"})
public class UserEntity {
	
	public static int DEFAULT_QUOTA = 10;
	
	int userId;
	String userName;
	String openIdLogin;
	String loginId;
	String email;
	String password;
	Boolean active = true;
	Boolean validated = false;
	String affiliation;
	Date dateRegistered;
	Date lastLoggedIn;
	Set<RoleEntity> roles;
	Integer quota = DEFAULT_QUOTA;
	

	@Column(name="affiliation", length=300)
	@XmlAttribute
	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	@JsonSerialize(using = DateSerializer.class)
	@Column(name="date_registered")
	@XmlAttribute
	public Date getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	
	@JsonSerialize(using = DateSerializer.class)
	@Column(name="date_lastloggedin")
	@XmlAttribute
	public Date getLastLoggedIn() {
		return lastLoggedIn;
	}

	public void setLastLoggedIn(Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}
	
	/**
	 * 
	 * @return the email associated with the user
	 */
	@Column(name="email", unique=true, nullable=false, length=100)
	@XmlAttribute
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email email of the user
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return password for the user
	 */
	@Column(name="password", nullable=false, length=255)
	@JsonIgnore
	@XmlTransient
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password password to be set for the user
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return whether this user is active
	 */
	@Column(name="active")
	@XmlAttribute
	public Boolean getActive() {
		return active;
	}

	/**
	 * 
	 * @param active whether this user is active/inactive
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	
	/**
	 * 
	 * @return login name for the user
	 */
	
	@XmlAttribute
	@Column(name="login_name", unique = true, nullable = false, length = 255)
	public String getLoginId() {
		return loginId;
	}
	
	/**
	 * 
	 * @param loginId login name for the user
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	/**
	 * 
	 * @return assigned user id for the user
	 */
	@XmlAttribute
	@Id
	@Column(name="userid")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="user_seq")
	@SequenceGenerator(name="user_seq", sequenceName="USER_SEQ", schema="glyspace", initialValue=2)
	public int getUserId() {
		return userId;
	}
	
	/**
	 * 
	 * @param userid userid of the user
	 */
	public void setUserId(int userid) {
		this.userId = userid;
	}
	
	/**
	 * 
	 * @return user's full name
	 */
	@XmlAttribute
	@Column(name="full_name", nullable = false, length = 100)
	public String getUserName() {
		return userName;
	}
	
	/**
	 * 
	 * @param userName user's full name
	 */
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return the roles
	 */
	@ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.MERGE)
	@JoinTable(name = "user_role", joinColumns = { 
			@JoinColumn(name = "userId", nullable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "roleId", 
					nullable = false) })
	public Set<RoleEntity> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}

	/**
	 * @return the validated
	 */
	@Column(name="validated", nullable=false)
	@XmlAttribute
	public Boolean getValidated() {
		return validated;
	}

	/**
	 * @param validated the validated to set
	 */
	public void setValidated(Boolean validated) {
		this.validated = validated;
	}
	
	/**
	 * @return the openIdLogin
	 */
	@Column(name="openid_login", nullable=true, length=255)
	@XmlTransient
	public String getOpenIdLogin() {
		return openIdLogin;
	}

	/**
	 * @param openIdLogin the openIdLogin to set
	 */
	public void setOpenIdLogin(String openIdLogin) {
		this.openIdLogin = openIdLogin;
	}

	/**
	 * 
	 * @return the quota
	 */
	@Column(name="quota")
	@XmlTransient
	public Integer getQuota() {
		return quota;
	}

	public void setQuota(Integer quota) {
		this.quota = quota;
	}
}
