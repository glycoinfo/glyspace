package org.glyspace.registry.service.search;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.service.GlycanManager;
import org.glyspace.registry.view.search.CompositionSearchInput;

@XmlRootElement(name="composition")
public class CompositionSearchType extends SingleSearch {
	
	CompositionSearchInput input;
	
	@Override
	public List<String> search(GlycanManager glycanManager) throws Exception {
		if (input == null) {
			throw new IllegalArgumentException ("Invalid Input: search criteria (composition) should not be empty");
		}
		return glycanManager.compositionSearch(input);
	}

	/**
	 * @return the input
	 */
	@XmlElement(name="composition-search")
	public CompositionSearchInput getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(CompositionSearchInput input) {
		this.input = input;
	}
}
