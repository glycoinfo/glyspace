package org.glyspace.registry.service.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eurocarbdb.MolecularFramework.io.CarbohydrateSequenceEncoding;
import org.eurocarbdb.MolecularFramework.io.SugarImporterFactory;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.service.GlycanManager;
import org.glyspace.registry.view.Glycan;
import org.glyspace.registry.view.StructureParserValidator;

@XmlRootElement(name="exact-search")
public class ExactSearchType extends SingleSearch {

	Glycan input;
	
	@Override
	public List<String> search(GlycanManager glycanManager) throws Exception {
		List<String> list = new ArrayList<>();
		
		if (input == null) {
			throw new IllegalArgumentException ("Invalid Input: search criteria (structure) should not be empty");
		}
		
		String encoding = input.getEncoding();
		Sugar sugarStructure = null;
		if (encoding != null && !encoding.isEmpty()) {
			ArrayList<CarbohydrateSequenceEncoding> supported = SugarImporterFactory.getSupportedEncodings();
			for (Iterator iterator = supported.iterator(); iterator.hasNext();) {
				CarbohydrateSequenceEncoding carbohydrateSequenceEncoding = (CarbohydrateSequenceEncoding) iterator
						.next();
				if (encoding.equalsIgnoreCase(carbohydrateSequenceEncoding.getId())) {
					try {
						sugarStructure = SugarImporterFactory.importSugar(input.getStructure(), carbohydrateSequenceEncoding);
					} catch (Exception e) {
						// import failed
						throw new IllegalArgumentException("Structure cannot be imported: " + e.getMessage());
					}
					break;
				}
			}
			if (sugarStructure == null) {
				//encoding is not supported
				throw new IllegalArgumentException("Encoding " + encoding + " is not supported");
			}
		} else {
			// assume GlycoCT encoding
			sugarStructure = StructureParserValidator.parse(input.getStructure());
		}
		
		String exportedStructure;
		
		if (StructureParserValidator.isValid(sugarStructure)) {
			// export into GlycoCT to make sure we have a uniform structure content in the DB
			try {
				exportedStructure = StructureParserValidator.exportStructure(sugarStructure);
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot export into common encoding: " + e.getMessage());
			}
		}
		else {
			throw new IllegalArgumentException("Validation error, please submit a valid structure");
		}
		GlycanEntity glycan = glycanManager.getGlycanByStructure(exportedStructure);
		if (glycan != null) {
			list.add(glycan.getAccessionNumber());
		}
		return list;
	}

	/**
	 * @return the structure
	 */
	@XmlElement(name="glycan-structure")
	public Glycan getInput() {
		return input;
	}

	/**
	 * @param structure the structure to set
	 */
	public void setInput(Glycan structure) {
		this.input = structure;
	}

}
