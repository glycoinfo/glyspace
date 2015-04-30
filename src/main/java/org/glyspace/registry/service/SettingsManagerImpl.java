package org.glyspace.registry.service;

import org.glyspace.registry.dao.GlycanDAO;
import org.glyspace.registry.dao.GlycanDAOImpl;
import org.glyspace.registry.dao.UserDAO;
import org.glyspace.registry.dao.UserDAOImpl;
import org.glyspace.registry.database.SettingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsManagerImpl implements SettingsManager {
	
	@Autowired
	GlycanDAO glycanDAO;
	
	@Autowired
	UserDAO userDAO;

	@Override
	@Transactional
	public void setDelay(long newDelay) {
		SettingEntity delaySetting = new SettingEntity();
		delaySetting.setName(GlycanDAOImpl.DELAY);
		delaySetting.setValue(String.valueOf(newDelay));
		glycanDAO.updateControlDelay(delaySetting);
	}

	@Override
	@Transactional
	public long getDelay() {
		return glycanDAO.getControlDelay();
	}

	@Override
	@Transactional
	public void setQuotaPeriod(long newPeriod) {
		SettingEntity quotaSetting = new SettingEntity();
		quotaSetting.setName(UserDAOImpl.PERIOD);
		quotaSetting.setValue(String.valueOf(newPeriod));
		userDAO.updateQuotaPeriod(quotaSetting);
		
	}

	@Override
	@Transactional
	public long getQuotaPeriod() {
		return userDAO.getQuotaPeriod();
	}

}
