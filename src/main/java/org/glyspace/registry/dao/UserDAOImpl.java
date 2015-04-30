package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.dao.exceptions.UserNotFoundException;
import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.view.User;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

@Repository
public class UserDAOImpl implements UserDAO {
	
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.dao.UserDAOImpl");
	public static String PERIOD = "quotaPeriod"; 
	
	public long quotaPeriod = 86400; // default: 24 hours (in seconds)

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addUser(UserEntity user) {
		this.sessionFactory.getCurrentSession().save(user); 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> getAllUsers() {
		logger.debug("Getting users from database");
		return this.sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
	}

	@Override
	public void deleteUser(Integer userId) {
		try {
			UserEntity user = (UserEntity) sessionFactory.getCurrentSession().load(UserEntity.class, userId);
			if (user != null) {
				logger.debug("deleting user with id {} from the database", userId);
				this.sessionFactory.getCurrentSession().delete(user);
			}
		} catch (ObjectNotFoundException oe) {
			logger.warn ("Tried to delete a nonexistent user with id {}", userId);
			throw new UserNotFoundException (oe);
		}
	}

	@Override
	public void updateUser(UserEntity user) {
		this.sessionFactory.getCurrentSession().update(user);
	}

	@Override
	public UserEntity getUser(Integer userId) {
		UserEntity user;
		try {
			user = (UserEntity) sessionFactory.getCurrentSession().get(UserEntity.class, userId);
		} catch (ObjectNotFoundException oe) {
			logger.error ("user with id {} does not exist in the database", userId);
			throw new UserNotFoundException (oe);
		}
		return user;
	}

	/**
	 * returns the user regardless of validated value;
	 */
	@Override
	public UserEntity getUserByLoginName(String loginName) {
		return getUserByLoginName(loginName, false);
	}

	@Override
	public UserEntity getUserByEmail(String email) {
		Query query = sessionFactory.getCurrentSession().createQuery("from UserEntity where email= :email");
		query.setParameter("email", email);
		List<?> list = query.list();
		if (list.isEmpty()) {
			logger.error ("user with email {} does not exist in the database", email);
			throw new UserNotFoundException ("user with email " + email + " does not exist in the database");
		}
		UserEntity user = (UserEntity)list.get(0);
		return user;
	}

	/**
	 * @param login name for the user
	 * @param checkValidated if set to true, return the user information only if the user is validated
	 */
	@Override
	public UserEntity getUserByLoginName(String loginName, boolean checkValidated) {
		String queryString = "from UserEntity where loginId= :loginName";
		if (checkValidated) 
			queryString += " and validated=true";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("loginName", loginName);
		List<?> list = query.list();
		if (list.isEmpty()) {
			logger.error ("user with loginId {} does not exist in the database or still not validated", loginName);
			throw new UserNotFoundException ("user with loginId " + loginName + " does not exist in the database or still waiting for approval");
		}
		UserEntity user = (UserEntity)list.get(0);
		return user;
	}

	/**
	 * @param validated true to filter out non-validated users
	 * @param active true to filter out deactive users
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> getUsers(boolean validated, boolean active) {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from UserEntity where validated = :valid and active= :active");
		query.setParameter("valid", validated);
		query.setParameter("active", active);
		return query.list();
	}
	
	/**
	 * @param validated if false return all users awaiting approval, if true return all users already validated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> getUsersByValidated(boolean validated) {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from UserEntity where validated = :valid ");
		query.setParameter("valid", validated);
		return query.list();
	}
	
	/**
	 * @param active if true return all users who are active, if false return all deactivated users
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> getUsersByActive(boolean active) {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from UserEntity where active= :active");
		query.setParameter("active", active);
		return query.list();
	}

	@Override
	public UserEntity getUserByLoginName(String loginName,
			boolean checkValidated, boolean checkActive) {
		String queryString = "from UserEntity where loginId= :loginName";
		if (checkValidated) 
			queryString += " and validated=true";
		if (checkActive)
			queryString += " and active=true";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("loginName", loginName);
		List<?> list = query.list();
		if (list.isEmpty()) {
			logger.error ("user with loginId {} does not exist in the database or still not validated or the user is deactivated", loginName);
			throw new UserNotFoundException ("user with loginId " + loginName + " does not exist in the database or still waiting for approval or deactivated");
		}
		UserEntity user = (UserEntity)list.get(0);
		return user;
	}

	@Override
	public UserEntity getUserByCriteria(User user, boolean checkValidated) {
		if (user == null) throw new UserNotFoundException ("No user is found with the given criteria");
		String queryString ="from UserEntity where ";
		boolean first = true;
		if (user.getLoginId() != null && !user.getLoginId().isEmpty()) {
			if (first) {
				queryString += " loginId= :loginName";
				first = false;
			}
			else {
				queryString += " and loginId= :loginName";
			}
		}
		if (user.getEmail() != null && !user.getEmail().isEmpty()) {
			if (first) {
				queryString += " email= :email";
				first = false;
			}
			else {
				queryString += " and email= :email";
			}
		}
		if (user.getAffiliation() != null && !user.getAffiliation().isEmpty()) {
			if (first) {
				queryString += " affiliation= :affiliation";
				first = false;
			}
			else {
				queryString += " and affiliation= :affiliation";
			}
		}
		if (user.getFullName() != null && !user.getFullName().isEmpty()) {
			if (first) {
				queryString += " userName= :userName";
				first = false;
			}
			else {
				queryString += " and userName= :userName";
			}
		}
		if (checkValidated) 
			queryString += " and validated=true";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		if (user.getLoginId() != null && !user.getLoginId().isEmpty()) 
			query.setParameter("loginName", user.getLoginId());
		if (user.getEmail() != null && !user.getEmail().isEmpty())
			query.setParameter("email", user.getEmail());
		if (user.getAffiliation() != null && !user.getAffiliation().isEmpty()) 
			query.setParameter("affiliation", user.getAffiliation());
		if (user.getFullName() != null && !user.getFullName().isEmpty())
			query.setParameter("userName", user.getFullName());
		List<?> list = query.list();
		if (list.isEmpty()) {
			logger.error ("user with given criteria does not exist in the database or still not validated");
			throw new UserNotFoundException ("User matching the criteria does not exist in the database or still waiting for approval");
		}
		UserEntity userEntity = (UserEntity)list.get(0);
		return userEntity;
	}

	@Override
	public UserEntity getUserByOpenIdLogin(String openId,
			boolean checkValidated, boolean checkActive) {
		String queryString = "from UserEntity where openId_login= :loginName";
		if (checkValidated) 
			queryString += " and validated=true";
		if (checkActive)
			queryString += " and active=true";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("loginName", openId);
		List<?> list = query.list();
		if (list.isEmpty()) {
			logger.error ("user with openIdLogin {} does not exist in the database or still not validated or the user is deactivated", openId);
			throw new UserNotFoundException ("user with openIdLogin " + openId + " does not exist in the database or still waiting for approval or deactivated");
		}
		UserEntity user = (UserEntity)list.get(0);
		return user;	}

	@Override
	public long getQuotaPeriod() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from SettingEntity where name= :name");
		query.setParameter("name", PERIOD);
		List res = query.list();
		if (res != null && !res.isEmpty()) {
			SettingEntity setting = (SettingEntity) res.get(0);
			quotaPeriod = Long.parseLong(setting.getValue());
		}
		
		return quotaPeriod;
	}
	
	@Override
	public void updateQuotaPeriod (SettingEntity quotaPeriodSetting) {
		if (quotaPeriodSetting.getName().equals(PERIOD)) {
			this.quotaPeriod = Long.parseLong(quotaPeriodSetting.getValue());
			this.sessionFactory.getCurrentSession().update(quotaPeriodSetting);
		}
	}

	@Override
	public List<UserEntity> getUsersByRole(String role) {
		String hql ="select distinct u from UserEntity u " +
		        "join u.roles r " +
		        "where r.roleName = :role";
		
		Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("role", role);
		
		return query.list();
	}
}
