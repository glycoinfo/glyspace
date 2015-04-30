package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.view.User;

/**
 * All methods accessing the database should be defined here
 * 
 * @author sena
 *
 */

public interface UserDAO {
	public void addUser (UserEntity user);
	public List<UserEntity> getAllUsers();
	public List<UserEntity> getUsers(boolean validated, boolean active);
	public List<UserEntity> getUsersByActive(boolean active);
	public List<UserEntity> getUsersByValidated(boolean validated);
	public void deleteUser (Integer userId);
	public void updateUser (UserEntity user);
	public UserEntity getUser (Integer userId);
	public UserEntity getUserByLoginName (String loginName);
	public UserEntity getUserByLoginName (String loginName, boolean checkValidated);
	public UserEntity getUserByLoginName (String loginName, boolean checkValidated, boolean checkActive);
	public UserEntity getUserByOpenIdLogin (String openId, boolean checkValidated, boolean checkActive);
	public UserEntity getUserByEmail (String email);
	public UserEntity getUserByCriteria (User user, boolean checkValidated);
	
	public long getQuotaPeriod();
	public List<UserEntity> getUsersByRole(String moderator);
	void updateQuotaPeriod(SettingEntity quotaPeriodSetting);
}
