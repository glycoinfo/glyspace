package org.glyspace.registry.database;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name="roles")
@XmlRootElement (name="role")
@JsonSerialize
public class RoleEntity {
	
	public final static String ADMIN="ADMIN";
	public final static String USER="USER";
	public final static String MODERATOR="MODERATOR";
	
	Integer roleId;
	String roleName;
	Set <UserEntity> users;
	
	public RoleEntity() {
	}
	
	public RoleEntity (String role) {
		this.roleName=role;
	}
	
	/**
	 * @return the roleId
	 */
	@XmlAttribute
	@Id
	@Column(name="roleId")
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="role_seq")
	@SequenceGenerator(name="role_seq", sequenceName="ROLE_SEQ", schema="glyspace", initialValue=4)
	public Integer getRoleId() {
		return roleId;
	}
	
	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	/**
	 * @return the roleName
	 */
	@Column(name="name", nullable=false, unique=true)
	@XmlAttribute
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * @return the users
	 */
	@ManyToMany(fetch=FetchType.EAGER, mappedBy = "roles")
	@XmlTransient  // so that from the role we should not go back to users - prevent cycles
	@JsonIgnore
	public Set<UserEntity> getUsers() {
		return users;
	}
	
	/**
	 * @param users the users to set
	 */
	public void setUsers(Set<UserEntity> users) {
		this.users = users;
	}
}
