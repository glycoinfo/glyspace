package org.glyspace.registry.view;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonSerialize
@XmlRootElement(name="glycan-structures")
public class GlycanInputList {

	List<Glycan> glycans;

	/**
	 * @return the glycans
	 */
	
	@XmlElement(name="glycan-structure")
	@JsonProperty(value="glycan-structure")
	public List<Glycan> getGlycans() {
		return glycans;
	}

	/**
	 * @param glycans the glycans to set
	 */
	public void setGlycans(List<Glycan> glycans) {
		this.glycans = glycans;
	}
	
	
}
