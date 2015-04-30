package org.glyspace.registry.database;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="motif_tag")
@XmlRootElement(name="tag")
@JsonIgnoreProperties(value={"tagId"})
public class MotifTag {

	Integer tagId;
	String tag;
	Set<MotifEntity> motifs;
	/**
	 * @return the tagId
	 */
	@Id
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="motif_seq")
	@SequenceGenerator(name="motif_seq", sequenceName="MOTIF_SEQ", schema="glyspace")
	@Column (name="tagId")
	@XmlTransient
	public Integer getTagId() {
		return tagId;
	}
	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	/**
	 * @return the tag
	 */
	@XmlAttribute
	@Column(name="tag", unique=true, nullable=false, length=100)
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * @return the motifs
	 */
	@ManyToMany(fetch=FetchType.EAGER, mappedBy = "tags")
	@XmlTransient  // so that from the tag we should not go back to motifs - prevent cycles
	@JsonIgnore
	public Set<MotifEntity> getMotifs() {
		return motifs;
	}
	/**
	 * @param motifs the motifs to set
	 */
	public void setMotifs(Set<MotifEntity> motifs) {
		this.motifs = motifs;
	}
}
