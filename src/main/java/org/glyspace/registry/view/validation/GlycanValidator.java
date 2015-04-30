package org.glyspace.registry.view.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.validation.GlycoVisitorValidation;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class GlycanValidator implements ConstraintValidator<Structure, String>{
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.view.validation.GlycanValidator");
	
	public static final void recreateConstraintViolation(ConstraintValidatorContext constraintValidatorContext, String errorCode, String fieldName) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(errorCode).
        addPropertyNode(fieldName).addConstraintViolation();
	}

	@Override
	public void initialize(Structure arg0) {
	}

	@Override
	public boolean isValid(String glycanStructure, ConstraintValidatorContext context) {
		
		// Should not happen since we already have @NotEmpty
		if (glycanStructure == null) { 
			return false;
		}
		
		
		//=========================
        // Parsing
        //=========================
		SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
        Sugar t_sugarStructure = null;
        
        try 
        {
            // parse the sequencees
            t_sugarStructure = t_importer.parse(glycanStructure);
            
        }
        catch (SugarImporterException e) 
        {
            // parsing the sequence failed, printing out error position, error code and error message
            logger.error( "Parse Error" + e.getPosition() + " - "+ e.getErrorCode() + " - " + e.getErrorText() );
            GlycanValidator.recreateConstraintViolation(context, e.getErrorCode() + " - " + e.getErrorText() + String.valueOf(e.getPosition()), "");
            return false;
        }
        
        //=========================
        // Validation
        //=========================
        // validation of the structure based on GlycomeDB validation code
        GlycoVisitorValidation t_validation = new GlycoVisitorValidation();
        try
        {
            t_validation.start(t_sugarStructure);
            if ( t_validation.getErrors().size() != 0 )
            {
            	String errorMessage="Validation Error: ";
                // there was an error in the sequence
                for (String t_string : t_validation.getErrors())
                {
                    errorMessage += " " + t_string;
                }
                
                GlycanValidator.recreateConstraintViolation(context, errorMessage, "");
                return false;
            }
        } 
        catch (GlycoVisitorException e)
        {
            // something went totally wrong
            logger.error("Error validating the structure - " + e.getErrorMessage());
            GlycanValidator.recreateConstraintViolation(context, e.getErrorMessage(), "");
            return false;
        }
        
		return true;
	}

}
