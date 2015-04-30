package org.glyspace.registry.service;

import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.view.MotifInput;

public interface MotifManager {

	void createMotif(MotifInput motif) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException;

	void deleteMotif (Integer motifId);
	
	MotifEntity getMotif(String motifName);

	List<MotifEntity> getAll();

	List<MotifEntity> getMotifsByTags(List<String> tags);

	void addSequenceToMotif(String motifName, MotifSequence sequence) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException;

	void addTagToMotif(String motifName, String newTag);

	MotifSequence getMotifSequence(Integer sequenceId);

	List<MotifEntity> getMotifsByAnyTags(List<String> tags);
	
	void motifSequenceUpdateReducing (Integer sequenceId, Boolean reducing);

	void deleteSequenceFromMotif(Integer sequenceId) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException;
}
