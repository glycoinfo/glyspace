package org.glyspace.registry.dao;

import org.glyspace.registry.database.SettingEntity;

public interface SettingsDAO {

	SettingEntity getSetting (String name);
	void updateSetting (SettingEntity setting);
}
