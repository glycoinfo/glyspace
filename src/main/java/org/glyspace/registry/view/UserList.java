package org.glyspace.registry.view;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.database.UserEntity;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Wrapper for a list of users
 * Needed for creating meaningful xml elements for collections uf users
 * 
 * @author sena
 *
 */

@XmlRootElement(name="users")      // when XML is generated, the list of users will be under "users" tag
@ApiModel (value="UserList", description="List of Users")
public class UserList {
	private List<UserEntity> users;
	
	public UserList() {
		this.users = new ArrayList<UserEntity>();
	}
	
	public UserList (List<UserEntity> usrs) {
		this.users = usrs;
	}

	@XmlElement(name="user")       // each user will be inside "user" element in XML
	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}
	
}
