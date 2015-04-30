/**
 * @author sena
 *
 */
package org.glyspace.registry.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.glyspace.registry.dao.exceptions.GlycanNotFoundException;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.utils.AccessionNumberGenerator;
import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

/**
 * @author sena
 *
 */
@Repository
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GlycanDAOImpl implements GlycanDAO {

	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.dao.GlycanDAOImpl");
	public static final String DELAY = "delaySetting";

	@Autowired
	private SessionFactory sessionFactory;
	
	private long controlDelay = 600;  // in seconds - default value is 10 minutes

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return the controlDelay
	 */
	public void setControlDelayFromDB() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from SettingEntity where name= :name");
		query.setParameter("name", DELAY);
		List res = query.list();
		if (res != null && !res.isEmpty()) {
			SettingEntity setting = (SettingEntity) res.get(0);
			controlDelay = Long.parseLong(setting.getValue());
		}
	}
	
	@Override
	public void updateControlDelay (SettingEntity delaySetting) {
		if (delaySetting.getName().equals(DELAY)) {
			this.controlDelay = Long.parseLong(delaySetting.getValue());
			this.sessionFactory.getCurrentSession().update(delaySetting);
		}
	}
	
	/**
	 * 
	 * @return the current setting for the control delay
	 */
	@Override
	public long getControlDelay () {
		setControlDelayFromDB();
		return controlDelay;
	}

	/**
	 * @param controlDelay the controlDelay to set
	 */
	public void setControlDelay(long controlDelay) {
		this.controlDelay = controlDelay;
	}

	/**
	 * When we are adding a glycan to the database, we save it with null accession number and use the generated
	 * glycanid to create an accession number and update it again.
	 * 
	 * @see org.glyspace.registry.dao.GlycanDAO#addGlycan(org.glyspace.registry.database.GlycanEntity)
	 * @return accession number assigned for this glycan
	 * @throws EncoderException 
	 */
	@Override
	public String addGlycan(GlycanEntity glycan) {
		glycan.setDateEntered(new Date());
//		this.sessionFactory.getCurrentSession().save(glycan);
		String accessionNumber = "G" + AccessionNumberGenerator.generateRandomString(7);
		glycan.setAccessionNumber(accessionNumber);
		this.sessionFactory.getCurrentSession().save(glycan);
//		this.sessionFactory.getCurrentSession().update(glycan);
		return accessionNumber;
	}

	/**
	 * @see org.glyspace.registry.dao.GlycanDAO#deleteGlycanById(java.lang.Integer)
	 */
	@Override
	public void deleteGlycanById(Integer glycanId) {
		try {
			GlycanEntity glycan = (GlycanEntity) sessionFactory.getCurrentSession().load(GlycanEntity.class, glycanId);
			if (glycan != null) {
				logger.debug("deleting glycan with id {} from the database", glycanId);
				this.sessionFactory.getCurrentSession().delete(glycan);
			}
		} catch (ObjectNotFoundException oe) {
			logger.warn ("Tried to delete a nonexistent glycan with id {}", glycanId);
			throw new GlycanNotFoundException (oe);
		}

	}

	/**
	 * @see org.glyspace.registry.dao.GlycanDAO#deleteGlycanByAccession(java.lang.String)
	 * @param acessionNumber unique accession number for the glycan
	 */
	@Override
	public void deleteGlycanByAccession(String accessionNumber) {
		try {
			GlycanEntity glycan = getGlycanByAccession(accessionNumber);
			if (glycan != null) {
				logger.debug("deleting glycan with accession number {} from the database", accessionNumber);
				this.sessionFactory.getCurrentSession().delete(glycan);
			}
		} catch (ObjectNotFoundException oe) {
			logger.warn ("Tried to delete a nonexistent glycan with accession number {}", accessionNumber);
			throw new GlycanNotFoundException (oe);
		}
	}

	/* (non-Javadoc)
	 * @see org.glyspace.registry.dao.GlycanDAO#getGlycanById(java.lang.Integer)
	 */
	@Override
	public GlycanEntity getGlycanById(Integer glycanId) {
		GlycanEntity glycan;
		try {
			glycan = (GlycanEntity) sessionFactory.getCurrentSession().get(GlycanEntity.class, glycanId);
			Hibernate.initialize(glycan.getMotifs());
		} catch (ObjectNotFoundException oe) {
			//logger.warn ("Tried to retrieve a nonexistent glycan with id {}", glycanId);
			throw new GlycanNotFoundException (oe);
		}
		return glycan;
	}

	/* (non-Javadoc)
	 * @see org.glyspace.registry.dao.GlycanDAO#getGlycanByAccession(java.lang.String)
	 */
	@Override
	public GlycanEntity getGlycanByAccession(String accessionNumber) {
		
		Query query = sessionFactory.getCurrentSession().createQuery("from GlycanEntity where accessionNumber= :accession");
		query.setParameter("accession", accessionNumber);
		List<?> list = query.list();
		if (list.isEmpty()) {
			//logger.error ("Tried to retrieve a nonexistent glycan with accession number {}", accessionNumber);
			throw new GlycanNotFoundException ("Tried to retrieve a nonexistent glycan with accession number:" + accessionNumber);
		}
		GlycanEntity glycan = (GlycanEntity)list.get(0);
		Hibernate.initialize(glycan.getMotifs());
		return glycan;
	}
	
	@Override
	public GlycanEntity getGlycanByStructure (String structure) {
		Query query = sessionFactory.getCurrentSession().createQuery("from GlycanEntity where structure= :structure");
		query.setParameter("structure", structure);
		List<?> list = query.list();
		if (list.isEmpty()) {
			throw new GlycanNotFoundException ("Tried to retrieve a nonexistent glycan with given structure:" + structure);
		}
		GlycanEntity glycan = (GlycanEntity)list.get(0);
		Hibernate.initialize(glycan.getMotifs());
		return glycan;
	}

	/* (non-Javadoc)
	 * @see org.glyspace.registry.dao.GlycanDAO#updateGlycan(org.glyspace.registry.database.GlycanEntity)
	 */
	@Override
	public void updateGlycan(GlycanEntity glycan) {
		this.sessionFactory.getCurrentSession().update(glycan);
	}

	@Override
	public List<GlycanEntity> getAllGlycans() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from GlycanEntity where dateEntered <= :time");
		// Get all glycans that passed the control period after the original submission date
		Date currentDate = new Date();
		Timestamp currentTime = new Timestamp(currentDate.getTime() - controlDelay*1000L);   // convert controlDelay to miliseconds
		query.setParameter("time", currentTime);
		return query.list();
	}
	
	@Override
	public List<GlycanEntity> getAllGlycansWithMotifs() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("from GlycanEntity where dateEntered <= :time");
		// Get all glycans that passed the control period after the original submission date
		Date currentDate = new Date();
		Timestamp currentTime = new Timestamp(currentDate.getTime() - controlDelay*1000L);   // convert controlDelay to miliseconds
		query.setParameter("time", currentTime);
		List<GlycanEntity> glycans = query.list();
		for (Iterator iterator = glycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			Hibernate.initialize(glycanEntity.getMotifs());
		}
		return glycans;
	}
	
	@Override
	public List<String> getAllGlycanIds() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("select g.accessionNumber from GlycanEntity as g where g.dateEntered <= :time");
		// Get all glycans that passed the control period after the original submission date
		Date currentDate = new Date();
		Timestamp currentTime = new Timestamp(currentDate.getTime() - controlDelay*1000L);   // convert controlDelay to miliseconds
		query.setParameter("time", currentTime);
		return query.list();
	}

	@Override
	public boolean isPending(GlycanEntity glycan) {
		Date currentDate = new Date();
		Timestamp currentTime = new Timestamp(currentDate.getTime() - controlDelay*1000L);   // convert controlDelay to miliseconds
		if (glycan.getDateEntered().before(currentTime)) 
			return false;
		return true;
	}
	
	/** this one gets all non-pending glycans submitted by the user **/
	@Override
	public List<String> getGlycansByContributor(UserEntity user) {
		return getGlycansByContributor(user, true, false);
	}

	@Override
	public List<String> getGlycansByContributor(UserEntity user, boolean checkPending, boolean pending) {
		String sql = "select g.accessionNumber from GlycanEntity as g where g.contributor.userId = :userId";
		if (checkPending) {
			if (pending) { // pending only
				sql += " and g.dateEntered > :time";
			} else {
				sql += " and g.dateEntered <= :time";
			}
		}
		Query query = this.sessionFactory.getCurrentSession().createQuery(sql);
		query.setParameter("userId", user.getUserId());
		if (checkPending) {
			Date currentDate = new Date();
			Timestamp currentTime = new Timestamp(currentDate.getTime() - controlDelay*1000L);   // convert controlDelay to miliseconds
			query.setParameter("time", currentTime);
		}
		return query.list();
	}

	@Override
	public List<GlycanEntity> getGlycansByMotif(MotifEntity motif) {
		String hql ="select distinct g from GlycanEntity g " +
        "join g.motifs m " +
        "where m.motifId = :motifId and g.dateEntered <= :time";
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("motifId", motif.getMotifId());
		Date currentDate = new Date();
		Timestamp currentTime = new Timestamp(currentDate.getTime() - controlDelay*1000L);   // convert controlDelay to miliseconds
		query.setParameter("time", currentTime);
		List<GlycanEntity> glycans = query.list();
		for (Iterator iterator = glycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			Hibernate.initialize(glycanEntity.getMotifs());
		}
		return glycans;
	}

	@Override
	public void deleteGlycansByAccessionNumber(List<String> accessionNumbers) {
		Query query = this.sessionFactory.getCurrentSession().createQuery("delete from GlycanEntity where accession-number IN ( :list )");
		query.setParameterList("list", accessionNumbers);
		query.executeUpdate();
	}

	@Override
	public int getNumberGlycansByUserDate(long quotaPeriod, Integer userId) {
		String hql ="select distinct g from GlycanEntity g " +
		        "where g.contributor.userId = :userId and g.dateEntered >= :time";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("userId", userId);
		Date currentDate = new Date();
		Timestamp currentTime = new Timestamp(currentDate.getTime() - quotaPeriod*1000L);   // convert quotaPeriod to miliseconds
		query.setParameter("time", currentTime);
		List<GlycanEntity> glycans = query.list();
		return glycans.size();	
	}
}
