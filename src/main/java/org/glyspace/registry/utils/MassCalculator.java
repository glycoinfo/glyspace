package org.glyspace.registry.utils;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.massutil.IonCloud;
import org.eurocarbdb.application.glycanbuilder.massutil.MassOptions;
//import org.eurocarbdb.application.glycanbuilder.GlycoCTParser;
//import org.eurocarbdb.application.glycanbuilder.IonCloud;
//import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.glyspace.registry.view.StructureParserValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.qos.logback.classic.Logger;

public class MassCalculator {
	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.utils.MassCalculator");
	
	@Autowired
	MonosaccharideConversion monosaccharideConverter;
	
	public Double calculateMass (Sugar sugar) throws Exception {
		GlycoVisitorFromGlycoCT t_visFromGlycoCT = new GlycoVisitorFromGlycoCT( monosaccharideConverter );
        t_visFromGlycoCT.setNameScheme(GlycanNamescheme.GWB);
        Glycan t_glycan;
		
		//t_glycan = GlycoCTParser.fromSugar(sugar,monosaccharideConverter,t_visFromGlycoCT,new MassOptions(),true);
		
		SugarExporterGlycoCT t_exCT = new SugarExporterGlycoCT();
		t_exCT.start(sugar);
		t_glycan = Glycan.fromGlycoCT(t_exCT.getXMLCode(), new MassOptions());
		this.checkGWBTranslation(sugar, t_glycan);
		// configure the mass options
        MassOptions t_options = new MassOptions();
        t_options.setDerivatization(MassOptions.NO_DERIVATIZATION);
        t_options.ION_CLOUD = new IonCloud();
        t_options.NEUTRAL_EXCHANGES = new IonCloud();
        // set the mass options to the glycan and calculate mass
        t_glycan.setMassOptions(t_options);
        double t_mass = t_glycan.computeMass();
        return t_mass;
	}
	
	private  void checkGWBTranslation(Sugar a_sugar, Glycan a_glycan) throws Exception
    {
        try
        {
            SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
            SugarExporterGlycoCTCondensed t_exporter = new SugarExporterGlycoCTCondensed();
            t_exporter.start(a_sugar);
            String t_original = t_exporter.getHashCode();
            String t_newCT = a_glycan.toGlycoCTCondensed();
            Sugar t_newSugar = t_importer.parse(t_newCT);
            t_exporter.start(t_newSugar);
            if ( !t_original.equals(t_exporter.getHashCode()) )
            {
                throw new Exception("GWB does not read the GlycoCT correctly");
            }
        }
        catch ( Exception e)
        {
            throw new Exception("GWB does not read the GlycoCT correctly",e);
        }
    }
	
	public static void main(String[] args) throws Exception
	{
		String structure = "RES\n"
				+ "1b:b-drib-HEX-1:5|2:d|6:d\n"
				+ "2b:b-drib-HEX-1:5|2:d|6:d\n"
				+ "3b:b-drib-HEX-1:5|2:d|6:d\n"
				+ "4s:acetyl\n"
				+ "LIN\n"
				+ "1:1o(4+1)2d\n"
				+ "2:2o(4+1)3d\n"
				+ "3:3o(4+1)4n";
		Sugar sugarStructure = StructureParserValidator.parse(structure);
		MassCalculator massCalculator = new MassCalculator();
		double mass = new MassCalculator().calculateMass (sugarStructure);
		System.out.println ("Mass: " + mass);
	}
}
