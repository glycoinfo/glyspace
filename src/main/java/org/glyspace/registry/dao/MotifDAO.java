package org.glyspace.registry.dao;

import java.util.List;

import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.MotifTag;

public interface MotifDAO {

	void deleteMotif (Integer motifId);
	void addMotif (MotifEntity motif);
	void updateMotif (MotifEntity motif);
	
	void addTag (MotifTag tag);
	void deleteTag (Integer tagId);
	MotifTag getTag (String tag);
	
	MotifSequence getSequence (String sequence);
	MotifSequence getSequenceById(Integer sequenceId);
	
	MotifEntity getMotif (Integer motifId);
	MotifEntity getMotifByName (String motifName);
	
	List<MotifSequence> getMotifSequences (String motifName);
	List<MotifTag> getMotifTags (String motifName);
	
	List<MotifEntity> getAllMotifs();
	List<MotifEntity> getMotifsByTags(List<String> tags, boolean conjunction);
	void updateMotifSequence(MotifSequence seq);
	MotifEntity getMotifBySequence(Integer sequenceId);
}
	
