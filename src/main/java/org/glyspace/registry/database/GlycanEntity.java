package org.glyspace.registry.database;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.view.conversion.DateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Class to hold information about Glycans
 * 
 * @author sena
 *
 */

@Entity
@Table(name="glycans")
@XmlRootElement (name="glycan")
@JsonSerialize
public class GlycanEntity {
	Integer glycanId;
	String accessionNumber;
    Date dateEntered;
	String structure;
	Integer structureLength;
	UserEntity contributor;
	Double mass;
	Set<MotifEntity> motifs;
	Set<GlycanComposition> compositions;
	
	/**
	 * @return the motifs
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "glycan_motifs", joinColumns = { 
			@JoinColumn(name = "glycanId", nullable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "motifId", 
					nullable = false) })
	@XmlElementWrapper (name="motifs")
	public Set<MotifEntity> getMotifs() {
		return motifs;
	}

	/**
	 * @param motifs the motifs to set
	 */
	public void setMotifs(Set<MotifEntity> motifs) {
		this.motifs = motifs;
	}

	@Id
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="glycan_seq")
	@SequenceGenerator(name="glycan_seq", sequenceName="GLYCAN_SEQ", schema="glyspace")
	@Column (name="glycanid")
	@XmlAttribute
	public Integer getGlycanId() {
		return glycanId;
	}

	public void setGlycanId(Integer glycanId) {
		this.glycanId = glycanId;
	}

	@Column(name="accession_number", unique = true, length = 50)
	@XmlAttribute
	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	@JsonSerialize(using = DateSerializer.class)
	@Column(name="date_entered", nullable = false)
	@XmlAttribute
	public Date getDateEntered() {
		return dateEntered;
	}

	public void setDateEntered(Date dateEntered) {
		this.dateEntered = dateEntered;
	}

	@Column(name="structure", unique=true, nullable=false, length=15000)
	@XmlElement(name="structure")
	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	@Column(name="structure_length", nullable=false)
	@XmlAttribute
	public Integer getStructureLength() {
		return structureLength;
	}

	public void setStructureLength(Integer structureLength) {
		this.structureLength = structureLength;
	}

	@ManyToOne
	public UserEntity getContributor() {
		return contributor;
	}

	public void setContributor(UserEntity contributor) {
		this.contributor = contributor;
	}

	/**
	 * @return the mass
	 */
	@Column(name="mass", nullable=true)
	@XmlAttribute
	public Double getMass() {
		return mass;
	}

	/**
	 * @param mass the mass to set
	 */
	public void setMass(Double mass) {
		this.mass = mass;
	}

	/**
	 * @return the compositions
	 */
	@XmlElementWrapper(name="glycan-compositions")
	@XmlElement(name="glycan-composition")
	@OneToMany (fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="glycan", orphanRemoval=true)
	public Set<GlycanComposition> getCompositions() {
		return compositions;
	}

	/**
	 * @param compositions the compositions to set
	 */
	public void setCompositions(Set<GlycanComposition> compositions) {
		this.compositions = compositions;
	}	
}
