package org.glyspace.registry.dao;

import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;

public interface GlycanDAO {

	String addGlycan (GlycanEntity glycan);
	void deleteGlycanById (Integer glycanId);
	void deleteGlycanByAccession (String accessionNumber);
	GlycanEntity getGlycanById (Integer glycanId);
	GlycanEntity getGlycanByAccession (String accessionNumber);
	void updateGlycan (GlycanEntity glycan);
	List<GlycanEntity> getAllGlycans();
	List<String> getAllGlycanIds();
	GlycanEntity getGlycanByStructure(String structure);
	boolean isPending(GlycanEntity glycan);
	long getControlDelay();
	void updateControlDelay(SettingEntity delaySetting);
	List<String> getGlycansByContributor(UserEntity user);
	List<GlycanEntity> getGlycansByMotif(MotifEntity motif);
	List<GlycanEntity> getAllGlycansWithMotifs();
	List<String> getGlycansByContributor(UserEntity user, boolean checkPending,
			boolean pending);
	void deleteGlycansByAccessionNumber(List<String> accessionNumbers);
	int getNumberGlycansByUserDate(long quotaPeriod, Integer userId);
}
