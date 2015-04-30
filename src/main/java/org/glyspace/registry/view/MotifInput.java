package org.glyspace.registry.view;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.database.MotifSequence;
import org.hibernate.validator.constraints.NotEmpty;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel (value="Motif", description="Motif representation")
@XmlRootElement(name="motif-input")
public class MotifInput {

	String name;
	String[] tags;
	List<MotifSequence> sequences;
	
	/**
	 * @return the name
	 */
	@NotEmpty
	@XmlAttribute
	@ApiModelProperty(required=true)
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the tags
	 */
	@NotEmpty
	@ApiModelProperty(required=true)
	@XmlElementWrapper
	@XmlElement(name="tag")
	public String[] getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	/**
	 * @return the sequences
	 */
	@NotEmpty
	@ApiModelProperty (dataType="java.util.List")
	@XmlElementWrapper(name="motif-sequences")
	@XmlElement(name="motif-sequence")
	public List<MotifSequence> getSequences() {
		return sequences;
	}
	/**
	 * @param sequences the sequences to set
	 */
	public void setSequences(List<MotifSequence> sequences) {
		this.sequences = sequences;
	}
	
	
}
