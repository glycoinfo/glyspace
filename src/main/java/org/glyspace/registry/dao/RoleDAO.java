package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.database.RoleEntity;

public interface RoleDAO {
	RoleEntity getRole (String name);
	List<String> listRoleNames ();
	
}
