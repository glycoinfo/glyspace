package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.database.CompositionEntity;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

@Repository
@SuppressWarnings({ "unchecked" })
public class CompositionDAOImpl implements CompositionDAO {

	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.dao.GlycanDAOImpl");

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	@Transactional
	public List<CompositionEntity> getAllCompositions() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from CompositionEntity");
		return query.list();
	}

	@Override
	@Transactional
	public void saveOrUpdate(CompositionEntity compEntity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(compEntity);		
	}

}
