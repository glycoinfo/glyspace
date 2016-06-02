package org.glyspace.registry.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.converterGlycoCT.GlycoCTParser;
import org.eurocarbdb.application.glycanbuilder.converterWURCS2.WURCS2Parser;
import org.eurocarbdb.application.glycanbuilder.linkage.Union;
import org.eurocarbdb.application.glycanbuilder.massutil.MassOptions;
import org.eurocarbdb.application.glycanbuilder.renderutil.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.renderutil.SVGUtils;
//import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
//import org.eurocarbdb.application.glycanbuilder.GraphicOptions;
//import org.eurocarbdb.application.glycanbuilder.GlycoCTParser;
//import org.eurocarbdb.application.glycanbuilder.MassOptions;
//import org.eurocarbdb.application.glycanbuilder.SVGUtils;
//import org.eurocarbdb.application.glycanbuilder.Union;
import org.eurocarbdb.application.glycanbuilder.util.GraphicOptions;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.springframework.beans.factory.annotation.Autowired;

public class ImageGenerator {

	@Autowired
	BuilderWorkspace glycanWorkspace;
	
	@Autowired
	MonosaccharideConversion monosaccharideConverter;
	
	public byte[] getImage (String structure, String format, String notation, String style) throws Exception {
    Glycan t_glycan;
		// parse sequence and create Sugar object
//        SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
//        Sugar t_sugar;
//		t_sugar = t_importer.parse(structure);
//		
//        // get a GWB Glycan object from it
//        GlycoVisitorFromGlycoCT t_visFromGlycoCT = new GlycoVisitorFromGlycoCT( monosaccharideConverter );
//        t_visFromGlycoCT.setNameScheme(GlycanNamescheme.GWB);
//		t_glycan = GlycoCTParser.fromSugar(t_sugar,monosaccharideConverter,t_visFromGlycoCT,new MassOptions(),true);
		
    // overwite it with wurcs converter
    WURCS2Parser t_wurcsparser = new WURCS2Parser();
    t_glycan = t_wurcsparser.readGlycan(structure, new MassOptions());
  		
        // configure the image settings
		if (notation == null || notation.equalsIgnoreCase("cfg")) 
			glycanWorkspace.setNotation(GraphicOptions.NOTATION_CFG);
		else if (notation.equalsIgnoreCase("cfgbw"))
			glycanWorkspace.setNotation(GraphicOptions.NOTATION_CFGBW);
		else if (notation.equalsIgnoreCase("cfg-uoxf"))
			glycanWorkspace.setNotation(GraphicOptions.NOTATION_CFGLINK);
		else if (notation.equalsIgnoreCase("uoxf"))
			glycanWorkspace.setNotation(GraphicOptions.NOTATION_UOXF);
		else if (notation.equalsIgnoreCase("uoxf-color"))
			glycanWorkspace.setNotation(GraphicOptions.NOTATION_UOXFCOL);
		else if (notation.equalsIgnoreCase("iupac"))
			glycanWorkspace.setNotation(GraphicOptions.NOTATION_TEXT);
		else 
			throw new IllegalArgumentException("Notation " + notation + " is not supported");
		
		if (style == null || style.equalsIgnoreCase("compact"))
			glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_COMPACT);
		else if (style.equalsIgnoreCase("normal")) 
			glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_NORMAL);
		else if (style.equalsIgnoreCase("extended")) 
			glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_NORMALINFO);
		else 
			throw new IllegalArgumentException("Notation style " + style + " is not supported");
        
        if (format == null) 
        	format = "png";
        if (format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
        	// create a buffered image of scale 1
	        BufferedImage img = glycanWorkspace.getGlycanRenderer().getImage(t_glycan, true, false, true, 1);
	        
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        if (format.equalsIgnoreCase("png"))
	        	ImageIO.write( img  , "png", byteArrayOutputStream);
	        else
	        	ImageIO.write (img, "jpg", byteArrayOutputStream);
	        return byteArrayOutputStream.toByteArray();
        }
        else if (format.equalsIgnoreCase("svg")) {
        	// create the SVG
            String t_svg = SVGUtils.getVectorGraphics((GlycanRendererAWT)glycanWorkspace.getGlycanRenderer(),new Union<Glycan>(t_glycan));
            return t_svg.getBytes();
        } 
        else {
        	throw new IllegalArgumentException("Image format " + format + " is not supported");
        }
		
	}
	
	 public byte[] getGlycoCTImage (String structure, String format, String notation, String style) throws Exception {
	    Glycan t_glycan;
	    // parse sequence and create Sugar object
	        SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
	        Sugar t_sugar;
	    t_sugar = t_importer.parse(structure);
	    
	        // get a GWB Glycan object from it
	        GlycoVisitorFromGlycoCT t_visFromGlycoCT = new GlycoVisitorFromGlycoCT( monosaccharideConverter );
	        t_visFromGlycoCT.setNameScheme(GlycanNamescheme.GWB);
	    t_glycan = GlycoCTParser.fromSugar(t_sugar,monosaccharideConverter,t_visFromGlycoCT,new MassOptions(),true);
	    
	        // configure the image settings
	    if (notation == null || notation.equalsIgnoreCase("cfg")) 
	      glycanWorkspace.setNotation(GraphicOptions.NOTATION_CFG);
	    else if (notation.equalsIgnoreCase("cfgbw"))
	      glycanWorkspace.setNotation(GraphicOptions.NOTATION_CFGBW);
	    else if (notation.equalsIgnoreCase("cfg-uoxf"))
	      glycanWorkspace.setNotation(GraphicOptions.NOTATION_CFGLINK);
	    else if (notation.equalsIgnoreCase("uoxf"))
	      glycanWorkspace.setNotation(GraphicOptions.NOTATION_UOXF);
	    else if (notation.equalsIgnoreCase("uoxf-color"))
	      glycanWorkspace.setNotation(GraphicOptions.NOTATION_UOXFCOL);
	    else if (notation.equalsIgnoreCase("iupac"))
	      glycanWorkspace.setNotation(GraphicOptions.NOTATION_TEXT);
	    else 
	      throw new IllegalArgumentException("Notation " + notation + " is not supported");
	    
	    if (style == null || style.equalsIgnoreCase("compact"))
	      glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_COMPACT);
	    else if (style.equalsIgnoreCase("normal")) 
	      glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_NORMAL);
	    else if (style.equalsIgnoreCase("extended")) 
	      glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_NORMALINFO);
	    else 
	      throw new IllegalArgumentException("Notation style " + style + " is not supported");
	        
	        if (format == null) 
	          format = "png";
	        if (format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
	          // create a buffered image of scale 1
	          BufferedImage img = glycanWorkspace.getGlycanRenderer().getImage(t_glycan, true, false, true, 1);
	          
	          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	          if (format.equalsIgnoreCase("png"))
	            ImageIO.write( img  , "png", byteArrayOutputStream);
	          else
	            ImageIO.write (img, "jpg", byteArrayOutputStream);
	          return byteArrayOutputStream.toByteArray();
	        }
	        else if (format.equalsIgnoreCase("svg")) {
	          // create the SVG
	            String t_svg = SVGUtils.getVectorGraphics((GlycanRendererAWT)glycanWorkspace.getGlycanRenderer(),new Union<Glycan>(t_glycan));
	            return t_svg.getBytes();
	        } 
	        else {
	          throw new IllegalArgumentException("Image format " + format + " is not supported");
	        }
	    
	  }
	
}
