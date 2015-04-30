package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.dao.exceptions.MotifNotFoundException;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.MotifTag;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

@Repository
public class MotifDAOImpl implements MotifDAO {
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.dao.MotifDAOImpl");

	@Autowired
	private SessionFactory sessionFactory;
	
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public MotifEntity getMotif(Integer motifId) {
		MotifEntity motif;
		try {
			motif = (MotifEntity) sessionFactory.getCurrentSession().get(MotifEntity.class, motifId);
		} catch (ObjectNotFoundException oe) {
			logger.error ("motif with id {} does not exist in the database", motifId);
			throw new MotifNotFoundException (oe);
		}
		return motif;
	}

	@Override
	public MotifEntity getMotifByName(String motifName) {
		Query query = sessionFactory.getCurrentSession().createQuery("from MotifEntity where name= :name");
		query.setParameter("name", motifName);
		List<?> list = query.list();
		if (list.isEmpty()) {
			throw new MotifNotFoundException ("Tried to retrieve a nonexistent motif with name:" + motifName);
		}
		MotifEntity motif = (MotifEntity)list.get(0);
		return motif;
	}

	/**
	 * returns all motifs containing (at the least) all the tags in the given list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MotifEntity> getMotifsByTags(List<String> tags, boolean conjunction) {	
		String hql;
		if (conjunction) {
			hql= "select m from MotifEntity m " +
		                "join m.tags t " +
		                "where t.tag in (:tags) " +
		                "group by m " +
		                "having count(t)=:tag_count";
		} else {
			hql = "select distinct m from MotifEntity m " +
	                "join m.tags t " +
	                "where t.tag in (:tags)";
		}
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameterList("tags", tags);
		if (conjunction) query.setInteger("tag_count", tags.size());
		List<MotifEntity> motifs = query.list();
		return motifs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MotifSequence> getMotifSequences(String motifName) {
		Query query = sessionFactory.getCurrentSession().createQuery("from MotifSequence ms where ms.motif.name = :motif");
		query.setParameter("motif", motifName);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MotifTag> getMotifTags(String motifName) {
		Query query = sessionFactory.getCurrentSession().createQuery("select mt from MotifTag mt "
				+ "inner join mt.motifs m where m.name= :motifName");
		query.setParameter("motifName", motifName);
		return query.list();
	}

	@Override
	public void deleteMotif(Integer motifId) {
		try {
			MotifEntity motif = (MotifEntity) sessionFactory.getCurrentSession().load(MotifEntity.class, motifId);
			if (motif != null) {
				logger.debug("deleting motif with id {} from the database", motifId);
				this.sessionFactory.getCurrentSession().delete(motif);
			}
		} catch (ObjectNotFoundException oe) {
			logger.warn ("Tried to delete a nonexistent motif with id {}", motifId);
			throw new MotifNotFoundException (oe);
		}
	}

	@Override
	public void addMotif(MotifEntity motif) {
		this.sessionFactory.getCurrentSession().save(motif); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MotifEntity> getAllMotifs() {
//		List<GlycanEntity> list = this.sessionFactory.getCurrentSession().createQuery("from GlycanEntity where accession_number like 'G000%MO'");
//		for (GlycanEntity glycanEntity : list) {
//			MotifEntity me = new MotifEntity();
//			me.setglycanEntity.getAccessionNumber();
//		}
//		return 

		return this.sessionFactory.getCurrentSession().createQuery("from MotifEntity").list();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public MotifTag getTag(String tag) {
		MotifTag mtag=null;
		Query query = this.sessionFactory.getCurrentSession().createQuery ("from MotifTag where tag= :tag");
		query.setParameter("tag", tag);
		List res = query.list();
		if (res != null && !res.isEmpty()) {
			mtag = (MotifTag) res.get(0);
		}	
		return mtag;	
	}

	@Override
	public void addTag(MotifTag tag) {
		this.sessionFactory.getCurrentSession().save(tag);
	}

	@Override
	public void deleteTag(Integer tagId) {
		// TODO Auto-generated method stub
		
	}


	@SuppressWarnings("rawtypes")
	@Override
	public MotifSequence getSequence(String sequence) {
		MotifSequence seq = null;
		Query query = this.sessionFactory.getCurrentSession().createQuery ("from MotifSequence where sequence= :sequence");
		query.setParameter ("sequence", sequence);
		List res = query.list();
		if (res != null && !res.isEmpty()) {
			seq = (MotifSequence) res.get(0);
		}	
		return seq;	
	}

	@Override
	public void updateMotif(MotifEntity motif) {
		this.sessionFactory.getCurrentSession().update(motif);
	}

	@Override
	public MotifSequence getSequenceById(Integer sequenceId) {
		try {
			return (MotifSequence) this.sessionFactory.getCurrentSession().get(MotifSequence.class, sequenceId);
		} catch (ObjectNotFoundException oe) {
			logger.error ("motif sequence with id {} does not exist in the database", sequenceId);
			throw new MotifNotFoundException (oe);
		}
		
	}

	@Override
	public void updateMotifSequence(MotifSequence seq) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(seq);
	}

	@Override
	public MotifEntity getMotifBySequence(Integer sequenceId) {
		Query query = this.sessionFactory.getCurrentSession().createQuery("select m from MotifEntity m join m.sequences s where s.sequenceId = :sequenceId");
		query.setParameter("sequenceId", sequenceId);
		
		MotifEntity motif=null;
		List res = query.list();
		if (res != null && !res.isEmpty()) {
			motif = (MotifEntity) res.get(0);
		}
		return motif;
	}

}
