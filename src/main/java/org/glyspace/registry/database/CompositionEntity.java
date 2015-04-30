package org.glyspace.registry.database;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="composition")
@XmlRootElement(name="composition")
public class CompositionEntity {

	Integer compositionId;
	String name;
	String structure;
	Set<GlycanComposition> glycans;
	
	/**
	 * @return the compositionId
	 */
	@Id
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="composition_seq")
	@SequenceGenerator(name="composition_seq", sequenceName="COMP_SEQ", schema="glyspace", initialValue=50)
	@Column (name="compositionId")
	@XmlAttribute
	public Integer getCompositionId() {
		return compositionId;
	}
	/**
	 * @param compositionId the compositionId to set
	 */
	public void setCompositionId(Integer compositionId) {
		this.compositionId = compositionId;
	}
	/**
	 * @return the name
	 */
	@Column(nullable=false, unique=true, length=100)
	@NotEmpty
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
	 * @return the structure
	 */
	@Column(name="structure", unique=true, nullable=false, length=15000)
	@XmlElement(name="structure")
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
	 * @return the glycans
	 */
	@OneToMany (fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="composition", orphanRemoval=true)
	@XmlTransient
	@JsonIgnore
	public Set<GlycanComposition> getGlycans() {
		return glycans;
	} 
	/**
	 * @param glycans the glycans to set
	 */
	public void setGlycans(Set<GlycanComposition> glycans) {
		this.glycans = glycans;
	} 
}
