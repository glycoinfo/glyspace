package org.glyspace.registry.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.glyspace.registry.view.validation.Structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Entity
@Table(name="motif_sequence")
@XmlRootElement(name="motif-sequence")
@JsonIgnoreProperties(value={"motif"})
@ApiModel(value="MotifSequence", description="sequence of the motif")
public class MotifSequence {

	Integer sequenceId;
	MotifEntity motif;
	Boolean reducing=null;
	String sequence;
	
	/**
	 * @return the sequenceId
	 */
	@Id
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="motif_seq")
	@SequenceGenerator(name="motif_seq", sequenceName="MOTIF_SEQ", schema="glyspace")
	@Column (name="sequenceId")
	@XmlAttribute
	public Integer getSequenceId() {
		return sequenceId;
	}
	/**
	 * @param sequenceId the sequenceId to set
	 */
	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}
	/**
	 * @return the motif
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	@XmlTransient
	public MotifEntity getMotif() {
		return motif;
	}
	/**
	 * @param motif the motif to set
	 */
	public void setMotif(MotifEntity motif) {
		this.motif = motif;
	}
	/**
	 * @return the reducing
	 */
	@Column(name="reducing", nullable=true)
	@XmlAttribute
	@ApiModelProperty(required=false)
	public Boolean getReducing() {
		return reducing;
	}
	/**
	 * @param reducing the reducing to set
	 */
	public void setReducing(Boolean reducing) {
		this.reducing = reducing;
	}
	/**
	 * @return the sequence
	 */
	@Column(name="sequence", unique=true, length=15000)
	@XmlElement
	@ApiModelProperty(required=true)
	@Structure
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
}
