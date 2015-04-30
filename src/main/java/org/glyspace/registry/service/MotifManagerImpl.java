package org.glyspace.registry.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngine;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glyspace.registry.dao.GlycanDAO;
import org.glyspace.registry.dao.MotifDAO;
import org.glyspace.registry.dao.exceptions.MotifNotFoundException;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.MotifTag;
import org.glyspace.registry.view.MotifInput;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

@Service
public class MotifManagerImpl implements MotifManager {
	
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.service.MotifManagerImpl");
	
	@Autowired
	MotifDAO motifDAO;
	
	@Autowired
	GlycanDAO glycanDAO;

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional
	public void createMotif(MotifInput motif) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		//check if it already exists, if so do nothing
		try {
			MotifEntity m = motifDAO.getMotifByName(motif.getName());
			if (m != null) 
				return;  // already exists
		} catch (MotifNotFoundException e) {
			// ignore and continue to add
			logger.debug("Motif {} already exists!", motif.getName());
		}
		MotifEntity motifEntity = new MotifEntity();
		motifEntity.setName(motif.getName());
		Set<MotifTag> tags = new HashSet<>();
		for (int i=0; i < motif.getTags().length; i++) {
			MotifTag tag;
			String tagString = motif.getTags()[i];
			if ((tag = motifDAO.getTag(tagString)) == null) {
				// add the tag
				tag = new MotifTag();
				tag.setTag(tagString);
				motifDAO.addTag(tag);
				
			}
			tags.add(tag);
		}
		
		motifEntity.setTags(tags);
		
		List<MotifSequence> sequences = motif.getSequences();
		for (Iterator iterator = sequences.iterator(); iterator.hasNext();) {
			MotifSequence motifSequence = (MotifSequence) iterator.next();
			motifSequence.setMotif(motifEntity);
		}
		motifEntity.setSequences(new HashSet<>(sequences));
		motifDAO.addMotif(motifEntity);
		
		// go through the existing glycans and match with this motif
		modifyGlycans(motifEntity);
	}

	@SuppressWarnings("rawtypes")
	private void modifyGlycans(MotifEntity motif) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		List <GlycanEntity> list = glycanDAO.getAllGlycansWithMotifs();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			Set<MotifEntity> existingMotifs = glycanEntity.getMotifs();
			SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
	        Sugar sugarStructure = null;
			// parse the sequence
	        sugarStructure = importer.parse(glycanEntity.getStructure());
	        
	        SearchEngine search = new SearchEngine ();
	        search.setQueriedStructure(sugarStructure);
	        
	        Set<MotifSequence> sequences = motif.getSequences();
			for (Iterator iterator2 = sequences.iterator(); iterator2.hasNext();) {
				MotifSequence motifSequence = (MotifSequence) iterator2.next();
				Sugar motifStructure = importer.parse(motifSequence.getSequence());
				search.setQueryStructure(motifStructure);
				if (motifSequence.getReducing() != null && motifSequence.getReducing()) {
					search.restrictToReducingEnds();
				}
				else {
					search.setOnlyReducingEnd(false);
				}
				search.match();
	            if (search.isExactMatch())
	            {
	            	logger.debug("Found a match. Modifying glycan {}", glycanEntity.getAccessionNumber());
	            	existingMotifs.add(motif);
	            	glycanEntity.setMotifs(existingMotifs);
	            	glycanDAO.updateGlycan(glycanEntity);
	            }
				
			}
		}
		
	}

	@Override
	@Transactional
	public MotifEntity getMotif(String motifName) {
		return motifDAO.getMotifByName(motifName);
	}

	@Override
	@Transactional
	public List<MotifEntity> getAll() {
		return motifDAO.getAllMotifs();
	}

	@Override
	@Transactional
	public List<MotifEntity> getMotifsByTags(List<String> tags) {
		return motifDAO.getMotifsByTags(tags, true);
	}
	
	@Override
	@Transactional
	public List<MotifEntity> getMotifsByAnyTags(List<String> tags) {
		return motifDAO.getMotifsByTags(tags, false);
	}

	@Override
	@Transactional
	public void addSequenceToMotif(String motifName, MotifSequence sequence) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		MotifEntity motif = motifDAO.getMotifByName(motifName);
		if (motif != null) {
			// check if the sequence already exists
			boolean exists=false;
			Set<MotifSequence> sequences = motif.getSequences();
			for (Iterator iterator = sequences.iterator(); iterator.hasNext();) {
				MotifSequence motifSequence = (MotifSequence) iterator.next();
				if (motifSequence.getSequence().equalsIgnoreCase(sequence.getSequence())) {
					exists = true;
					break;
				}
				
			}
			if (!exists) {
				sequence.setMotif(motif);
				motifDAO.updateMotifSequence(sequence);
				motif.getSequences().add(sequence);
				motifDAO.updateMotif(motif);
				modifyGlycans(motif);
			}
		}
		else {
			throw new MotifNotFoundException("Motif with name " + motifName + " does not exist");
		}
		
	}
	
	@Override
	@Transactional
	public void deleteSequenceFromMotif (Integer sequenceId) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		MotifEntity motif = motifDAO.getMotifBySequence(sequenceId);
		if (motif != null) {
			MotifSequence sequence = motifDAO.getSequenceById(sequenceId);
			if (sequence == null ) // should never happen
				throw new MotifNotFoundException("Motif sequence with id " + sequenceId + " does not exist");
			motif.getSequences().remove(sequence);
			if (motif.getSequences().size() == 0) {
				// remove the motif as well
				motifDAO.deleteMotif(motif.getMotifId());
			}
			else {
				sequence.setMotif(null);
				motifDAO.updateMotif(motif);
				modifyGlycansRemoveSequence(motif); 
			}
		}
		else {
			throw new MotifNotFoundException("Motif having sequence with id " + sequenceId + " does not exist");
		}
	}
	
	private void modifyGlycansRemoveSequence(MotifEntity motif) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		List<GlycanEntity> glycans = glycanDAO.getGlycansByMotif(motif);
		for (Iterator iterator = glycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			// remove this motif from the glycan and try to match with the motif again with the updated sequences
			glycanEntity.getMotifs().remove(motif);
			SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
	        Sugar sugarStructure = null;
			// parse the sequence
	        sugarStructure = importer.parse(glycanEntity.getStructure());
	        
	        SearchEngine search = new SearchEngine ();
	        search.setQueriedStructure(sugarStructure);
	        
	        Set<MotifSequence> sequences = motif.getSequences();
			for (Iterator iterator2 = sequences.iterator(); iterator2.hasNext();) {
				MotifSequence motifSequence = (MotifSequence) iterator2.next();
				Sugar motifStructure = importer.parse(motifSequence.getSequence());
				search.setQueryStructure(motifStructure);
				if (motifSequence.getReducing() != null && motifSequence.getReducing()) {
					search.restrictToReducingEnds();
				}
				else {
					search.setOnlyReducingEnd(false);
				}
				search.match();
	            if (search.isExactMatch())
	            {
	            	logger.debug("Found a match. Modifying glycan {}", glycanEntity.getAccessionNumber());
	            	glycanEntity.getMotifs().add(motif);
	            	glycanDAO.updateGlycan(glycanEntity);
	            }
			}
		}	
	}

	@Override
	@Transactional
	public void addTagToMotif(String motifName, String newTag) {
		MotifEntity motif = motifDAO.getMotifByName(motifName);
		if (motif != null) {
			Set<MotifTag> tags = motif.getTags();
			// check if newTag already exists in this motif
			for (Iterator iterator = tags.iterator(); iterator.hasNext();) {
				MotifTag motifTag = (MotifTag) iterator.next();
				if (motifTag.getTag().equalsIgnoreCase(newTag)) {
					return;
				}
			}
			// it does not exist
			// check whether the new tag exists in motif_tags table
			MotifTag tag = motifDAO.getTag(newTag);
			if (tag == null) {
				// add it to the database first
				tag = new MotifTag();
				tag.setTag(newTag);
				motifDAO.addTag(tag);
			} 
			tags.add(tag);
			motif.setTags(tags);
			motifDAO.updateMotif(motif);
		} else {
			throw new MotifNotFoundException("Motif with name " + motifName + " does not exist");
		}
	}

	@Override
	@Transactional
	public MotifSequence getMotifSequence(Integer sequenceId) {
		return motifDAO.getSequenceById(sequenceId);
	}


	@Override
	@Transactional
	public void motifSequenceUpdateReducing(Integer sequenceId, Boolean reducing) {
		MotifSequence seq = getMotifSequence(sequenceId);
		if (seq != null) {
			seq.setReducing(reducing);
			motifDAO.updateMotifSequence (seq);
		}
	}

	@Override
	@Transactional
	public void deleteMotif(Integer motifId) {
		MotifEntity motif = motifDAO.getMotif(motifId);
		if (motif == null) {
			throw new MotifNotFoundException("Motif with id " + motifId + " does not exist");
		}
		List<GlycanEntity> glycans = glycanDAO.getGlycansByMotif(motif);
		// remove this motif from each glycan
		for (Iterator iterator = glycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			glycanEntity.getMotifs().remove(motif);
			glycanDAO.updateGlycan(glycanEntity);
		}
		motifDAO.deleteMotif(motifId);
	}
}
