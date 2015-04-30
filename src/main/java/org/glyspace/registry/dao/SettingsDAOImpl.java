package org.glyspace.registry.dao;

import org.glyspace.registry.database.SettingEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SettingsDAOImpl implements SettingsDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public SettingEntity getSetting(String name) {
		SettingEntity setting = (SettingEntity) this.sessionFactory.getCurrentSession().get(SettingEntity.class, name);
		return setting;
	}

	@Override
	public void updateSetting(SettingEntity setting) {
		this.sessionFactory.getCurrentSession().update(setting);

	}
}
