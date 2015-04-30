package org.glyspace.registry.service;

import java.util.Date;
import java.util.List;

import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.view.User;

public interface UserManager {

	public void addUser(UserEntity user);
	public void modifyUser (UserEntity user);
	public List<UserEntity> getAllUsers();
	public List<UserEntity> getUsers (boolean validated, boolean active);
	
	public List<UserEntity> getUsersByValidated (boolean validated);
	public List<UserEntity> getUsersByActive (boolean active);
	
	public void activateUser (Integer userId);
	public void deactivateUser (Integer userId);
	public void validateUser (Integer userId);
	public void deleteUser (Integer userId);
	public void addRole(Integer userId, String roleName);
	public UserEntity getUser (Integer userId);
	public UserEntity getUserByLoginId (String loginId, boolean checkValidated, boolean checkActive);
	public UserEntity getUserByOpenIdLogin (String openId, boolean checkValidated, boolean checkActive);
	public String recoverLogin(String email);
	public User recoverPassword (String loginId);
	public void changePassword (String loginId, String newPassword);
	public void setLoggedinDate (UserEntity user, Date loginDate);
	
	//public boolean login (String loginId, String password);
	void removeRole(Integer userId, String roleName);
	List<String> getRoles ();
	public List<UserEntity> getModerators();
	
	public void updateUserQuota (Integer userId, Integer newQuota);
	
}
