package org.glyspace.registry.importers;

import org.eurocarbdb.application.glycanbuilder.Glycan;

public class GWSImporter {
	
	/**
	 * 
	 * @param structure in GlycoWorkbench (GWS) format
	 * 
	 * example: redEnd--??1HexA,p--??1D-GlcN,p--??1HexA,p--??1D-GlcNAc,p--??1HexA,p(--??1D-GlcN,p((--??1HexA,p--??1D-GlcNAc,p--6?1S)--N?1S)--6?1S)--2?1S$MONO,perMe,Na,0,redEnd
	 * @return structure in GlycoCT_condensed format
	 */
	public String parse (String structure) {
		Glycan t_glycan = Glycan.fromString(structure);
        // serialize to GlycoCT
        // should be correct but better parse and serialize it again
        String t_sequenceGlycoCT = t_glycan.toGlycoCTCondensed();
        return t_sequenceGlycoCT;
	}
}
