package org.glyspace.registry.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.glyspace.registry.dao.RoleDAO;
import org.glyspace.registry.dao.UserDAO;
import org.glyspace.registry.dao.exceptions.UserNotFoundException;
import org.glyspace.registry.dao.exceptions.UserRoleViolationException;
import org.glyspace.registry.database.RoleEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.view.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

/**
 * Service Layer class for managing users
 * @author sena
 *
 */

@Service
public class UserManagerImpl implements UserManager {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	RoleDAO roleDAO;
	
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.service.UserManagerImpl");
	
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	@Transactional
	public void addUser(UserEntity user) {
		// encrypt the password
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPassword);
		user.setDateRegistered(new Date());
		Set<RoleEntity> roles = new HashSet<RoleEntity>(1);
		// check if USER role already exists in db
		RoleEntity userRole = roleDAO.getRole(RoleEntity.USER);
		if (userRole == null) {  // create for the first time
			userRole = new RoleEntity(RoleEntity.USER);
		}
		roles.add(userRole); // by default all the users added are regular users
		user.setRoles(roles);  
		userDAO.addUser(user);
	}

	@Override
	@Transactional
	public List<UserEntity> getAllUsers() {
		return userDAO.getAllUsers();
	}

	@Override
	@Transactional
	public void deleteUser(Integer userId) {
		userDAO.deleteUser(userId);
	}
	
	/**
	 * Check the user's credentials against the database and allow login
	 * if login is allowed, set the lastloggedin date
	 * @param user
	 * @return  whether user logged in or login failed
	 */
/*	@Override
	@Transactional
	public boolean login (String loginId, String password) {
		UserEntity user = userDAO.getUserByLoginName(loginId);
		if (user == null) {
			throw new UserNotFoundException("No user is associated with " + loginId);
		}
		//TODO encrypt the password before checking
		if (user.getPassword() != password) {
			logger.warn ("Login Attempt: password incorrect for user {}", user.getLoginId());
			return false;
		}
		
		user.setLastLoggedIn(new Date());
		userDAO.updateUser(user);
		return true;
	}*/

	@Override
	@Transactional
	public String recoverLogin(String email) {
		UserEntity user = userDAO.getUserByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("No user is associated with " + email);
		}
		return user.getLoginId();
	}

	@Override
	@Transactional
	public User recoverPassword(String loginId) {
		UserEntity user = userDAO.getUserByLoginName(loginId);
		if (user == null) {
			throw new UserNotFoundException("No user is associated with " + loginId);
		}
		User userView = new User();
		userView.setAffiliation(user.getAffiliation());
		userView.setFullName(user.getUserName());
		userView.setEmail(user.getEmail());
		userView.setPassword(user.getPassword());
		userView.setLoginId(user.getLoginId());
		return userView;
	}

	/**
	 * user can change his/her password by providing the new password
	 */
	@Override
	@Transactional
	public void changePassword(String loginId, String newPassword) {
		UserEntity user = userDAO.getUserByLoginName(loginId);
		if (user == null) {
			throw new UserNotFoundException("No user is associated with " + loginId);
		}
		// encrypt the password
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(hashedPassword);
		userDAO.updateUser(user);
	}
	
	@Override
	@Transactional
	public UserEntity getUserByLoginId(String loginId, boolean checkValidated,
			boolean checkActive) {
		return userDAO.getUserByLoginName(loginId, checkValidated, checkActive);
	}

	@Override
	@Transactional
	public void modifyUser(UserEntity user) {
		userDAO.updateUser(user);
	}

	@Override
	@Transactional
	public void activateUser(Integer userId) {
		UserEntity user = userDAO.getUser(userId);
		user.setActive(true);
		userDAO.updateUser(user);
	}

	@Override
	@Transactional
	public void deactivateUser(Integer userId) {
		UserEntity user = userDAO.getUser(userId);
		user.setActive(false);
		userDAO.updateUser(user);
		
	}

	@Override
	@Transactional
	public void validateUser(Integer userId) {
		UserEntity user = userDAO.getUser(userId);
		user.setValidated(true);
		userDAO.updateUser(user);
		
	}

	@Override
	@Transactional
	public void addRole(Integer userId, String roleName) {
		UserEntity user = userDAO.getUser(userId);
		if (user == null) {
			logger.warn("Tried to add a role to a user - User with id {} does not exist in DB");
			throw new UserNotFoundException("User with id : " + userId + " does not exist in DB");
		}
		Set<RoleEntity> roles = user.getRoles();
		// if the user does not have this role, add it
		for (Iterator<RoleEntity> iterator = roles.iterator(); iterator.hasNext();) {
			RoleEntity roleEntity = (RoleEntity) iterator.next();
			if (roleEntity.getRoleName().equals(roleName)) {
				// already has the role
				return;
			}
		}
		// not found, add it to the list of roles
		// check if the role already exist in db
		RoleEntity role = roleDAO.getRole(roleName);
		if (role == null) { // create a new role
			role = new RoleEntity();
			role.setRoleName(roleName);
		}
		roles.add(role);
		user.setRoles(roles);
		userDAO.updateUser(user);
	}
	
	@Override
	@Transactional
	public void removeRole (Integer userId, String roleName) {
		UserEntity user = userDAO.getUser(userId);
		if (user == null) {
			logger.warn("Tried to delete a role of a user - User with id {} does not exist in DB");
			throw new UserNotFoundException("User with id : " + userId + " does not exist in DB");
		}
		
		Set<RoleEntity> roles = user.getRoles();
		// if the user has only one role, cannot remove
		if (roles.size() == 1) {
			throw new UserRoleViolationException ("Cannot remove role " + roleName + ". User needs to have at least one role.");
		}
		for (Iterator<RoleEntity> iterator = roles.iterator(); iterator.hasNext();) {
			RoleEntity roleEntity = (RoleEntity) iterator.next();
			
			if (roleEntity.getRoleName().equals(roleName)) {
				logger.debug("removing role {}", roleName);
				iterator.remove();
				break;
			}
		}
		userDAO.updateUser(user);
	}

	@Override
	@Transactional
	public UserEntity getUser(Integer userId) {
		return userDAO.getUser(userId);
	}

	@Override
	@Transactional
	public void setLoggedinDate(UserEntity user, Date loginDate) {
		user.setLastLoggedIn(loginDate);
		userDAO.updateUser(user);
	}

	/**
	 * List the names of the existing roles in DB
	 * @return list of role names
	 */
	@Override
	@Transactional
	public List<String> getRoles() {
		return roleDAO.listRoleNames();
	}

	@Override
	@Transactional
	public List<UserEntity> getUsers(boolean validated, boolean active) {
		return userDAO.getUsers(validated, active);
	}

	@Override
	@Transactional
	public List<UserEntity> getUsersByValidated(boolean validated) {
		return userDAO.getUsersByValidated(validated);
	}

	@Override
	@Transactional
	public List<UserEntity> getUsersByActive(boolean active) {
		return userDAO.getUsersByActive(active);
	}

	@Override
	@Transactional
	public UserEntity getUserByOpenIdLogin(String openId,
			boolean checkValidated, boolean checkActive) {
		return userDAO.getUserByOpenIdLogin(openId, checkValidated, checkActive);
	}

	@Override
	@Transactional
	public List<UserEntity> getModerators() {
		return userDAO.getUsersByRole (RoleEntity.MODERATOR);
	}

	@Override
	@Transactional
	public void updateUserQuota(Integer userId, Integer newQuota) {
		UserEntity user = userDAO.getUser(userId);
		user.setQuota(newQuota);
		userDAO.updateUser(user);
		
	}
}
