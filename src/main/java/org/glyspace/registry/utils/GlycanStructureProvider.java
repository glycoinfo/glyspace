package org.glyspace.registry.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.glycomedb.database.data.Structure;
import org.glycomedb.rdf.glycordf.util.StructureProvider;
import org.glyspace.registry.database.GlycanEntity;

public class GlycanStructureProvider implements StructureProvider {

	GlycanEntity glycan;
	
	public void setGlycan(GlycanEntity glycan) {
		this.glycan = glycan;
	}
	
	@Override
	public List<Integer> getAllStructureId() throws IOException {
		// TODO Auto-generated method stub
		List<Integer> glycanIds = new ArrayList<Integer>(1);
		glycanIds.add(glycan.getGlycanId());
		return glycanIds;
	}

	@Override
	public Structure getStructure(Integer arg0) throws IOException {
		Structure str = new Structure();
		str.setId(glycan.getGlycanId());
		str.setSequence(glycan.getStructure());
		str.setAccessionNumber(glycan.getAccessionNumber());
		str.setHasCfgImage(true);
		str.setHasIupacImage(true);
		str.setHasOxImag(true);
		return str;
	}
}
