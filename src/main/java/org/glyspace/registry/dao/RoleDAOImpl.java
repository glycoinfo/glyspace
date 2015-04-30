package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.database.RoleEntity;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

@Repository
public class RoleDAOImpl implements RoleDAO {
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.dao.roleDAOImpl");

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public RoleEntity getRole(String name) {
		RoleEntity role=null;
		Query query = this.sessionFactory.getCurrentSession().createQuery ("from RoleEntity where name= :rolename");
		query.setParameter("rolename", name);
		List res = query.list();
		if (res != null && !res.isEmpty()) {
			role = (RoleEntity) res.get(0);
		}	
		return role;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listRoleNames() {
		return this.sessionFactory.getCurrentSession().createQuery("select r.roleName from RoleEntity r").list();
	}

}
