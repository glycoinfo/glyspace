package org.glyspace.registry.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngine;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glyspace.registry.dao.CompositionDAO;
import org.glyspace.registry.dao.GlycanDAO;
import org.glyspace.registry.dao.MotifDAO;
import org.glyspace.registry.dao.UserDAO;
import org.glyspace.registry.dao.exceptions.GlycanNotFoundException;
import org.glyspace.registry.database.CompositionEntity;
import org.glyspace.registry.database.GlycanComposition;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.view.GlycanResponse;
import org.glyspace.registry.view.User;
import org.glyspace.registry.view.search.CompositionSearchInput;
import org.glyspace.registry.view.search.Range;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

@Service
public class GlycanManagerImpl implements GlycanManager {

	@Autowired
	GlycanDAO glycanDAO;
	
	@Autowired 
	UserDAO userDAO;
	
	@Autowired
	MotifDAO motifDAO;
	
	@Autowired
	CompositionDAO compositionDAO;
	
	List<CompositionEntity> compositionList=null;
	
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.service.GlycanManagerImpl");
	static Map<String, String> queryMap;
	static
    {
		queryMap = new HashMap<String, String>();
		
		queryMap.put("hexNac", "RES\n"
				+ "1b:x-HEX-1:5\n"
				+ "2s:n-acetyl\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n");
		queryMap.put("hexN", "RES\n"
				+ "1b:x-HEX-x:x\n"
				+ "2s:amino\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n");
		queryMap.put("NeuAC", "RES\n"
				+ "1b:x-dgro-dgal-NON-x:x|1:a|2:keto|3:d\n"
				+ "2s:n-acetyl\n"
				+ "LIN\n"
				+ "1:1d(5+1)2n");
		queryMap.put("NeuGC", "RES\n"
				+ "1b:x-dgro-dgal-NON-x:x|1:a|2:keto|3:d\n"
				+ "2s:n-glycolyl\n"
				+ "LIN\n"
				+ "1:1d(5+1)2n");
		queryMap.put("hexA", "RES\n"
				+ "1b:x-HEX-x:x|6:a");
		queryMap.put("dHex", "RES\n"
				+ "1b:x-HEX-x:x|6:d");
		queryMap.put("KDO", "RES\n"
				+ "1b:x-dman-OCT-x:x|1:a|2:keto|3:d");
		queryMap.put("KDN", "RES\n"
				+ "1b:x-dgro-dgal-NON-x:x|1:a|2:keto|3:d");
		queryMap.put("hexose", "RES\n"
				+ "1b:x-HEX-x:x");
		queryMap.put("pentose", "RES\n"
				+ "1b:x-PEN-x:x");
		
		queryMap.put("methyl", "RES\n1s:methyl");
		queryMap.put("acetyl", "RES\n1s:acetyl");
		queryMap.put("sulfate", "RES\n1s:sulfate");
		queryMap.put("phosphate", "RES\n1s:phosphate");
    }
	
	public boolean rangeProvided (Range range) {
		if (range != null && (range.getMin() !=null || range.getMax() != null)) {
			if (range.getMin() == null && range.getMax() != null && range.getMax() > 0) {
				return true;
			} else if (range.getMax() == null && range.getMin() != null && range.getMin() > 0) {
				return true;
			} else { // both not null
				if (range.getMin() > 0 || range.getMax() > 0) 
					return true;
			}
		}
		return false;
	}
	
	public void setGlycanDAO(GlycanDAO glycanDAO) {
		this.glycanDAO = glycanDAO;
	}

	@Override
	@Transactional
	public GlycanEntity getGlycanById(int glycanId) {
		GlycanEntity glycan = glycanDAO.getGlycanById(glycanId);
		if (glycan == null) 
			throw new GlycanNotFoundException("Glycan: " + glycanId +" does not exist");
		return glycan;
	}

	@Override
	@Transactional
	public GlycanEntity getGlycanByAccessionNumber(String accession) {
		GlycanEntity glycan = glycanDAO.getGlycanByAccession(accession);
		if (glycan == null) 
			throw new GlycanNotFoundException("Glycan with accession: " + accession +" does not exist");
		return glycan;
	}

	/**
	 * Stores the given structure into the DB
	 * Checks whether the structure is already in DB, if so returns the existing accessionNumber 
	 * dateEntered and accessionNumber are set in the DAO
	 * @return the unique accession number assigned to the glycan together with pending and existing flags
	 */
	@Override
	@Transactional
	public GlycanResponse addStructure(String structure, String userName, Double mass) throws Exception{
		String accessionNumber=null;
		GlycanResponse resp = new GlycanResponse();
		try {
			logger.debug("begin getGlycanByStructure");
			GlycanEntity glycan = glycanDAO.getGlycanByStructure(structure);
			logger.debug("end getGlycanByStructure");
			accessionNumber = glycan.getAccessionNumber();
			resp.setAccessionNumber (accessionNumber);
			resp.setExisting(true);
			resp.setQuotaExceeded(false);
			// check if it is pending
			if (glycanDAO.isPending (glycan))
				resp.setPending(true);
			else
				resp.setPending(false);
			return resp;
		} catch (GlycanNotFoundException ge) {
			//nothing to do, add the structure
		}
		resp.setExisting(false);
		GlycanEntity glycan = new GlycanEntity();
		glycan.setStructure(structure);
		glycan.setStructureLength(structure.length());
		UserEntity user = userDAO.getUserByLoginName(userName);
		
		// check if the user's quota is exceeded.
//		if (userQuotaExceeded (user)) {
//			resp.setQuotaExceeded(true);
//			return resp;
//		}
		glycan.setContributor(user);
		glycan.setMass(mass);
		// find matched motifs
//		Set<MotifEntity> motifs = motifMatch (glycan.getStructure());
//		glycan.setMotifs(motifs);
		
//		Set<GlycanComposition> compositions = calculateCompositions(glycan);
//		glycan.setCompositions(compositions);
		logger.debug("begin addGlycan");
		accessionNumber = glycanDAO.addGlycan(glycan);
		logger.debug("end addGlycan");
		resp.setAccessionNumber(accessionNumber);
		resp.setQuotaExceeded(false);
		resp.setPending(true);
		
		return resp;
	}
	
	public boolean userQuotaExceeded(UserEntity user) {
		long quotaPeriod = userDAO.getQuotaPeriod();
		
		Integer quota = user.getQuota();
		if (quota == null) {
			quota = UserEntity.DEFAULT_QUOTA;
		}
		
		logger.debug("begin getNumberGlycansByUserDate:>" + user.getUserId());
		int glycansSubmitted = glycanDAO.getNumberGlycansByUserDate (quotaPeriod, user.getUserId());
		logger.debug("end getNumberGlycansByUserDate:>" + user.getUserId());
		
		if (glycansSubmitted+1 > quota) {
			return true;
		}
		
		return false;	
	}
	
	@SuppressWarnings("rawtypes")
	private Set<GlycanComposition> calculateCompositions (GlycanEntity glycan) throws SugarImporterException, SearchEngineException, GlycoVisitorException {
		if (this.compositionList == null) {
			logger.debug("Getting composition list from the database");
			this.compositionList = compositionDAO.getAllCompositions();
		}
		else {
			logger.debug("Already have the composition list");
		}
		Set<GlycanComposition> setForCompEntity = new HashSet<GlycanComposition> ();
		Set<GlycanComposition> compositions = new HashSet<GlycanComposition> ();
		SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
        Sugar sugarStructure = null;
		// parse the sequence
        sugarStructure = importer.parse(glycan.getStructure());
        CompositionSearchEngine search = new CompositionSearchEngine ();
        search.setQueriedStructure(sugarStructure);
        for (Iterator iterator = this.compositionList.iterator(); iterator.hasNext();) {
			CompositionEntity compEntity = (CompositionEntity) iterator.next();
			String compStructure = compEntity.getStructure();
			try {
				Sugar comp = importer.parse(compStructure);
				search.setQueryStructure(comp);
				boolean stop = false;
				int matchCount = 0;
				while (!stop) {
					if (search.isExactMatch()) {
						matchCount++;
						search.removeMatchedFromQueried();
					}
					else {
						stop = true;
					}
				}
				GlycanComposition glycanComposition = new GlycanComposition();
				glycanComposition.setComposition(compEntity);
				glycanComposition.setGlycan(glycan);
				glycanComposition.setCount(matchCount);
				setForCompEntity.add(glycanComposition);
			//	compEntity.setGlycans (setForCompEntity);
			//	compositionDAO.saveOrUpdate(compEntity);
				compositions.add(glycanComposition);
			} catch (SugarImporterException s) {
	        	logger.warn ("exception in calculateComposition for composition {} .Error {}", compEntity.getCompositionId(), s.getErrorText());
	        }
		}
		return compositions;
	}

	private Set<MotifEntity> motifMatch(String structure) throws SugarImporterException, GlycoVisitorException, SearchEngineException, GlycoconjugateException {
		SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
        Sugar sugarStructure = null;
		// parse the sequence
        sugarStructure = importer.parse(structure);
        Set<MotifEntity> matchedMotifs = new HashSet<>();
        
        SearchEngine search = new SearchEngine ();
        search.setQueriedStructure(sugarStructure);
		
		List<MotifEntity> allMotifs = motifDAO.getAllMotifs();
		for (Iterator iterator = allMotifs.iterator(); iterator.hasNext();) {
			MotifEntity motifEntity = (MotifEntity) iterator.next();
			Set<MotifSequence> sequences = motifEntity.getSequences();
			for (Iterator iterator2 = sequences.iterator(); iterator2.hasNext();) {
				MotifSequence motifSequence = (MotifSequence) iterator2.next();
				try {
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
		            	// found a match, return
		            	logger.debug("Found a match: {}", motifEntity.getName());
		            	matchedMotifs.add(motifEntity);
		            }
				} catch (SugarImporterException s) {
		        	logger.warn ("exception in motifMatch for motif {} .Error {}", motifSequence.getSequenceId(), s.getErrorText());
		        }
				
			}
		} 
        return matchedMotifs;
	}

	@Override
	@Transactional
	public List<GlycanEntity> getGlycans() {
		return glycanDAO.getAllGlycansWithMotifs();
	}

	@Override
	@Transactional
	public List<String> getGlycanIds() {
		return glycanDAO.getAllGlycanIds();
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public List<String> subStructureSearch(String structure)
			throws GlycanNotFoundException, SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
        Sugar t_sugarStructure = null;
        List<String> matches = new ArrayList<String>();
	
        SearchEngine search = new SearchEngine ();
        // parse the sequence
        t_sugarStructure = t_importer.parse(structure);
        search.setQueryStructure(t_sugarStructure);
        // gets only visible glycans (not waiting for the delay period)
        List <GlycanEntity> allGlycans = glycanDAO.getAllGlycans();
        // test for each structure
        for (Iterator iterator = allGlycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			Sugar existingStructure = t_importer.parse(glycanEntity.getStructure());
			search.setQueriedStructure(existingStructure);
            search.match();
            if (search.isExactMatch())
            {
            	// found a match, return
            	logger.debug("Found a match: {}", glycanEntity.getAccessionNumber());
            	matches.add(glycanEntity.getAccessionNumber());
            }
		}
        
		if (matches.isEmpty()) {
			throw new GlycanNotFoundException ("No glycan found matching the given structure");
		}
		
		return matches;
	}
	
	@Override
	@Transactional
	public List<String> motifSearch(String motifName) throws Exception {
		List<String> accessionNumberList = new ArrayList<>();
		MotifEntity motif = motifDAO.getMotifByName(motifName);
		List<GlycanEntity> glycans = glycanDAO.getGlycansByMotif(motif);
		for (Iterator iterator = glycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			accessionNumberList.add(glycanEntity.getAccessionNumber());
		}
		return accessionNumberList;
	}

	@Override
	@Transactional
	public List<String> getGlycansByContributor(User user) {
		UserEntity userEntity = userDAO.getUserByCriteria (user, true);
		return glycanDAO.getGlycansByContributor(userEntity);
	}

	@Override
	@Transactional
	public GlycanEntity getGlycanByStructure(String structure) {
		return glycanDAO.getGlycanByStructure(structure);
	}

	@Override
	@Transactional
	public void deleteGlycanByAccessionNumber(String accession) {
		glycanDAO.deleteGlycanByAccession(accession);
		
	}

	@Override
	@Transactional
	public GlycanResponse assignNewAccessionNumber(String structure, String userName) {
		GlycanEntity glycan = new GlycanEntity();
		glycan.setStructure(structure);
		glycan.setStructureLength(structure.length());
		UserEntity user = userDAO.getUserByLoginName(userName);
		glycan.setContributor(user);
		String accessionNumber = glycanDAO.addGlycan(glycan);
		GlycanResponse response = new GlycanResponse();
		response.setAccessionNumber(accessionNumber);
		response.setExisting(false);
		response.setPending(true);
		return response;
	}
	
	@Transactional
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> compositionSearch(CompositionSearchInput input) throws SugarImporterException, GlycoVisitorException, SearchEngineException {
	
		List<GlycanEntity> resultList = new ArrayList<GlycanEntity>();
		SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
		
		Map<Sugar, Range> queryStructures = new LinkedHashMap<Sugar, Range>();
		// prepare query structures, the order is important
		Range range;
		if (rangeProvided(range=input.getHexNac())) {
			String structure = queryMap.get("hexNac");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getHexN()) ) {
			String structure = queryMap.get("hexN");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getNeuAc()) ) {
			String structure = queryMap.get("NeuAC");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getNeuGc()) ) {
			String structure = queryMap.get("NeuGC");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getHexA()) ) {
			String structure = queryMap.get("hexA");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getdHex())) {
			String structure = queryMap.get("dHex");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getKdo())) {
			String structure = queryMap.get("KDO");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getKdn()) ) {
			String structure = queryMap.get("KDN");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getHexose()) ) {
			String structure = queryMap.get("hexose");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getPentose()) ) {
			String structure = queryMap.get("pentose");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		//query the substituents last
		if (rangeProvided(range=input.getSulfate()) ) {
			String structure = queryMap.get("sulfate");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getPhosphate()) ) {
			String structure = queryMap.get("phosphate");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getMethyl()) ) {
			String structure = queryMap.get("methyl");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		if (rangeProvided(range=input.getAcetyl()) ) {
			String structure = queryMap.get("acetyl");
			Sugar queryStr = importer.parse(structure);
			queryStructures.put(queryStr, range);
		}
		
		if (queryStructures.isEmpty() && !rangeProvided(input.getOther())) {
			// nothing to search
			throw new IllegalArgumentException("Invalid Input: No search criteria");
		}
		
		// TODO: don't get all at once
		// get all structures from DB and start searching
		List<GlycanEntity> glycans = glycanDAO.getAllGlycans();
		CompositionSearchEngine searchEngine = new CompositionSearchEngine();
		
		for (Iterator iterator = glycans.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity = (GlycanEntity) iterator.next();
			
	        Sugar sugarStructure = null;
	        // parse the sequences
	        int count=0;
	        int allowed=0; // this many extra matches are allowed
			sugarStructure = importer.parse(glycanEntity.getStructure());
			// search through this structure for each query parameter and try to match the counts
			searchEngine.setQueriedStructure(sugarStructure);
			
			for(Iterator itr2 = queryStructures.entrySet().iterator(); itr2.hasNext();) {
				Entry<Sugar, Range> entry = (Entry<Sugar,Range>)itr2.next();
				Range r = entry.getValue();
				int min =0, max=0;
				if (r.getMin() != null) 
					min = r.getMin();
				if (r.getMax() != null)
					max = r.getMax();
				if (min > 0 && max < min) {
					max = min;
				}
				int matchCount = 0;
				searchEngine.setQueryStructure(entry.getKey());
				for (int i=0; i < max; i++) {
					if (searchEngine.isExactMatch()) {
						matchCount++;
						if (matchCount <= min) 
							searchEngine.removeMatchedFromQueried();
					}
				}
				if (matchCount >= min) { // we were able to satisfy this query parameter
					count++;
				}
				if (matchCount == max) {
					// we have found the maximum number of matches but didn't removed them from the queried
					// so we don't want these count towards others
					allowed += max-min;
				}
			}
			 
			if (count == queryStructures.size()) { // we were able to satisfy all the query parameters
				// need to check other count
				if ((range=input.getOther()) != null && (range.getMin() != null || range.getMax() != null)) {
					int others = searchEngine.getNotMatchedCount();
					logger.debug("Other left:{}", searchEngine.getNotMatchedCount());
					if (range.between(others-allowed)) {    // we need to allow the maximum of previous queries, then count the others
						// add the glycan to the result list
						resultList.add(glycanEntity);
					}
				} 
				else { // no others to check
					// add the glycan to the result list
					resultList.add(glycanEntity);
				}
			}	
		}
		
		if (resultList.isEmpty()) {
			throw new GlycanNotFoundException("No matching structure is found");
		}
		
		List<String> accessionNumberList = new ArrayList<>();
		for (Iterator iterator = resultList.iterator(); iterator.hasNext();) {
			GlycanEntity glycanEntity2 = (GlycanEntity) iterator.next();
			accessionNumberList.add(glycanEntity2.getAccessionNumber());
		}
		
		return accessionNumberList;
	}
	
	@Override
	@Transactional
	public List<String> getAllPendingGlycansByContributor (User user) {
		UserEntity userEntity = userDAO.getUserByCriteria (user, true);
		return glycanDAO.getGlycansByContributor(userEntity, true, true);
	}

	@Override
	@Transactional
	public void deleteGlycansByAccessionNumber(List<String> accessionNumbers) {
		glycanDAO.deleteGlycansByAccessionNumber(accessionNumbers);
		
	}

	@Override
	@Transactional
	public boolean isPending(GlycanEntity glycan) {
		return glycanDAO.isPending(glycan);
	}

	@Override
	@Transactional
	public List<GlycanEntity> getGlycansByAccessionNumbers(
			List<String> accessionNumbers) {
		List<GlycanEntity> glycans = new ArrayList<GlycanEntity>();
		for (Iterator iterator = accessionNumbers.iterator(); iterator.hasNext();) {
			String accessionNumber = (String) iterator.next();
			glycans.add(glycanDAO.getGlycanByAccession(accessionNumber));
		}
		return glycans;
	}

	
}
