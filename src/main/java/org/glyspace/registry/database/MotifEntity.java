package org.glyspace.registry.database;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="motif")
@XmlRootElement(name="motif")
public class MotifEntity {

	Integer motifId;
	String name;
	Set<MotifTag> tags;
	Set<MotifSequence> sequences;
	
	Set<GlycanEntity> glycans;
	
	/**
	 * @return the motifId
	 */
	@Id
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="motif_seq")
	@SequenceGenerator(name="motif_seq", sequenceName="MOTIF_SEQ", schema="glyspace", initialValue=200)
	@Column (name="motifId")
	@XmlAttribute
	public Integer getMotifId() {
		return motifId;
	}
	/**
	 * @param motifId the motifId to set
	 */
	public void setMotifId(Integer motifId) {
		this.motifId = motifId;
	}
	/**
	 * @return the name
	 */
	@Column(unique=true, nullable=false, length=255)
	@XmlAttribute
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
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "motif_tags", joinColumns = { 
			@JoinColumn(name = "motifId", nullable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "tagId", 
					nullable = false) })
	@XmlElementWrapper
	@XmlElement(name="tag")
	public Set<MotifTag> getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<MotifTag> tags) {
		this.tags = tags;
	}
	/**
	 * @return the sequences
	 */
	@XmlElementWrapper(name="motif-sequences")
	@XmlElement(name="motif-sequence")
	@OneToMany (fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="motif", orphanRemoval=true)
	public Set<MotifSequence> getSequences() {
		return sequences;
	}
	/**
	 * @param sequences the sequences to set
	 */
	public void setSequences(Set<MotifSequence> sequences) {
		this.sequences = sequences;
	}
	/**
	 * @return the glycans
	 */
	@ManyToMany(fetch=FetchType.LAZY, mappedBy = "motifs")
	@XmlTransient  // so that from the motif we should not go back to glycans - prevent cycles
	@JsonIgnore
	public Set<GlycanEntity> getGlycans() {
		return glycans;
	}
	/**
	 * @param glycans the glycans to set
	 */
	public void setGlycans(Set<GlycanEntity> glycans) {
		this.glycans = glycans;
	}
	
}
