package org.glyspace.registry.service.search;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.service.GlycanManager;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement(name="motif-search")
public class MotifSearchType extends SingleSearch{

	String input;
	
	@Override
	public List<String> search(GlycanManager glycanManager) throws Exception {
		return glycanManager.motifSearch(input);
	}

	/**
	 * @return the input
	 */
	@XmlElement(name="motif-name")
	@NotNull
	@NotEmpty
	@NotBlank
	public String getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}

}
