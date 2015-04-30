package org.glyspace.registry.service.search;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.glyspace.registry.service.GlycanManager;

@XmlTransient
public abstract class SingleSearch extends SearchType {

	Object input;
	
	@Override
	public abstract List<String> search(GlycanManager glycanManager) throws Exception;

	/**
	 * @return the input
	 */
	public Object getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(Object input) {
		this.input = input;
	}

}
