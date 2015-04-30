package org.glyspace.registry.service;

import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.view.GlycanResponse;
import org.glyspace.registry.view.User;
import org.glyspace.registry.view.search.CompositionSearchInput;

public interface GlycanManager {
	
	public GlycanEntity getGlycanById (int glycanId);
	public GlycanEntity getGlycanByAccessionNumber (String accession);
	public GlycanEntity getGlycanByStructure (String structure);
	
	public void deleteGlycanByAccessionNumber (String accession);
	
	public List<String> getGlycansByContributor (User user);
	public GlycanResponse addStructure (String structure, String userName, Double mass) throws Exception;
	public List<GlycanEntity> getGlycans();
	
	public List<GlycanEntity> getGlycansByAccessionNumbers(List<String> accessionNumbers);
	
	public List<String> getGlycanIds();
	public List<String> subStructureSearch(String structure) throws Exception;
	public List<String> motifSearch (String motifName) throws Exception;
	GlycanResponse assignNewAccessionNumber(String structure, String userName);
	List<String> compositionSearch(CompositionSearchInput input)
			throws SugarImporterException, GlycoVisitorException,
			SearchEngineException;
	List<String> getAllPendingGlycansByContributor(User user);
	void deleteGlycansByAccessionNumber (List<String> accessionNumbers);
	
	public boolean isPending(GlycanEntity glycan);
}
