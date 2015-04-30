package org.glyspace.registry.view;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;
import org.glyspace.registry.view.validation.Structure;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * This class is used to collect information about a glycan from the user
 * View of GlycanEntity
 * @author sena
 *
 */
@XmlRootElement (name="glycan-structure")
@ApiModel (value="Glycan", description="Glycan representation")
public class Glycan {

	String structure;
	String encoding;

	/**
	 * @return the structure
	 */
	@XmlJavaTypeAdapter(value=CDATAAdapter.class)
	@NotEmpty
	//@Structure
	@ApiModelProperty (value="Glycan structure", required=true)
	public String getStructure() {
		return structure;
	}

	/**
	 * @param structure the structure to set
	 */
	public void setStructure(String structure) {
		this.structure = structure;
	}

	/**
	 * @return the format
	 */
	@XmlAttribute
	@ApiModelProperty (value="Encoding", required=false)
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the format to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
