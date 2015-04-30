package org.glyspace.registry.service.search;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.service.GlycanManager;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement(name="id-search")
public class IdSearchType extends SingleSearch{

	String input;
	
	@Override
	public List<String> search(GlycanManager glycanManager) throws Exception {
		List<String> list = new ArrayList<>();
		GlycanEntity glycan = glycanManager.getGlycanByAccessionNumber(input);
		if (glycan != null) {
			list.add(glycan.getAccessionNumber());
		}
		return list;
	}

	/**
	 * @return the input
	 */
	@XmlElement(name="id")
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
