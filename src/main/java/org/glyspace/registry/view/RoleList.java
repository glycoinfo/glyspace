package org.glyspace.registry.view;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.database.UserEntity;

import com.wordnik.swagger.annotations.ApiModel;

@XmlRootElement(name="roles")      
@ApiModel (value="RoleList", description="List of role names")
public class RoleList {

	List<String> roleNames;
	
	public RoleList() {
		this.roleNames = new ArrayList<String>();
	}
	
	public RoleList(List<String> roleNames) {
		super();
		this.roleNames = roleNames;
	}

	/**
	 * @return the roleNames
	 */
	@XmlElement(name="role")
	public List<String> getRoleNames() {
		return roleNames;
	}

	/**
	 * @param roleNames the roleNames to set
	 */
	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}
	
}
