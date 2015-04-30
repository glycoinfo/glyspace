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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="glycan_composition")
@XmlRootElement(name="glycan-composition")
public class GlycanComposition {

	Integer glyCompId;
	GlycanEntity glycan;
	CompositionEntity composition;
	int count;
	
	/**
	 * @return the glyCompId
	 */
	@Id
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="composition_seq")
	@SequenceGenerator(name="composition_seq", sequenceName="COMP_SEQ", schema="glyspace", initialValue=50)
	public Integer getGlyCompId() {
		return glyCompId;
	}
	/**
	 * @param glyCompId the glyCompId to set
	 */
	public void setGlyCompId(Integer glyCompId) {
		this.glyCompId = glyCompId;
	}
	/**
	 * @return the glycan
	 */
	@ManyToOne (fetch = FetchType.LAZY)
	@XmlTransient
	@JsonIgnore
	public GlycanEntity getGlycan() {
		return glycan;
	}
	/**
	 * @param glycan the glycan to set
	 */
	public void setGlycan(GlycanEntity glycan) {
		this.glycan = glycan;
	}
	/**
	 * @return the composition
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	public CompositionEntity getComposition() {
		return composition;
	}
	/**
	 * @param composition the composition to set
	 */
	public void setComposition(CompositionEntity composition) {
		this.composition = composition;
	}
	/**
	 * @return the count
	 */
	@Column(name="count", nullable=false)
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
}
