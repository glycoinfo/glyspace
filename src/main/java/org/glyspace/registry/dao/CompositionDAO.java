package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.database.CompositionEntity;

public interface CompositionDAO {

	public List<CompositionEntity> getAllCompositions();
	public void saveOrUpdate(CompositionEntity compEntity);
}
