package org.glyspace.registry.view;

import org.eurocarbdb.MolecularFramework.io.CarbohydrateSequenceEncoding;
import org.eurocarbdb.MolecularFramework.io.SugarExporterFactory;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.validation.GlycoVisitorValidation;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glyspace.registry.view.validation.GlycanValidator;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class StructureParserValidator {
	
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.view.StructureParserValidator");
	
	public static boolean isValid (Sugar sugarStructure) throws GlycoVisitorException {
		//=========================
        // Validation
        //=========================
        // validation of the structure based on GlycomeDB validation code
        GlycoVisitorValidation validation = new GlycoVisitorValidation();
        try
        {
        	validation.start(sugarStructure);
            if ( validation.getErrors().size() != 0 )
            {
            	String errorMessage="Validation Error: ";
                // there was an error in the sequence
                for (String t_string : validation.getErrors())
                {
                    errorMessage += " " + t_string;
                }
                
                throw new GlycoVisitorException (errorMessage);
            }
        } 
        catch (GlycoVisitorException e)
        {
            // something went totally wrong
            logger.error("Error validating the structure - " + e.getErrorMessage());
            throw e;
        }
        
		return true;
	}
	
	public static Sugar parse (String structure) throws SugarImporterException {
		//=========================
        // Parsing
        //=========================
		SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
        Sugar sugarStructure = null;
        
        // parse the sequencees
        sugarStructure = importer.parse(structure);
        return sugarStructure;
	}
	
	public static String exportStructure (Sugar sugarStructure) throws Exception {
		return SugarExporterFactory.exportSugar(sugarStructure, CarbohydrateSequenceEncoding.glycoct_condensed);
	}

}
