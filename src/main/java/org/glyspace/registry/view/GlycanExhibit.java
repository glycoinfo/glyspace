package org.glyspace.registry.view;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.glyspace.registry.view.conversion.DateSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class GlycanExhibit {
	Integer glycanId;
	String type="Glycan";
	String accessionNumber;
    Date dateEntered;
	String structure;
	String contributor;
	Double mass;
	List<String> motifs;
	List<String> tags;
	String imageURL;
	
	int numberOfFuc= 0;
    int numberOfGal=2;
    int numberOfGalN=0;
    int numberOfGalA=0;
    int numberOfGalNAc=4;
    int numberOfGlc=0;
    int numberOfGlcN=0;
    int numberOfGlcA=0;
    int numberOfGlcNAc= 0;
    int numberOfIdoA=0;
    int numberOfKdn=0;
    int numberOfMan=3;
    int numberOfManN= 0;
    int numberOfManA= 0;
    int numberOfManNAc= 0;
    int numberOfNeuAc= 0;
    int numberOfNeuGc= 0;
    int numberOfXyl= 0;

	@XmlAttribute
	public Integer getGlycanId() {
		return glycanId;
	}
	public void setGlycanId(Integer glycanId) {
		this.glycanId = glycanId;
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	@XmlTransient
	public String getLabel() {
		return accessionNumber;
	}
	
	@XmlAttribute
	public String getAccessionNumber() {
		return accessionNumber;
	}
	
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	@JsonSerialize(using = DateSerializer.class)
	public Date getDateEntered() {
		return dateEntered;
	}
	public void setDateEntered(Date dateEntered) {
		this.dateEntered = dateEntered;
	}
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	
	@XmlAttribute
	public String getContributor() {
		return contributor;
	}
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	
	/**
	 * @return the numberOfFuc
	 */
	public int getNumberOfFuc() {
		return numberOfFuc;
	}
	/**
	 * @param numberOfFuc the numberOfFuc to set
	 */
	public void setNumberOfFuc(int numberOfFuc) {
		this.numberOfFuc = numberOfFuc;
	}
	/**
	 * @return the numberOfGal
	 */
	public int getNumberOfGal() {
		return numberOfGal;
	}
	/**
	 * @param numberOfGal the numberOfGal to set
	 */
	public void setNumberOfGal(int numberOfGal) {
		this.numberOfGal = numberOfGal;
	}
	/**
	 * @return the numberOfGalN
	 */
	public int getNumberOfGalN() {
		return numberOfGalN;
	}
	/**
	 * @param numberOfGalN the numberOfGalN to set
	 */
	public void setNumberOfGalN(int numberOfGalN) {
		this.numberOfGalN = numberOfGalN;
	}
	/**
	 * @return the numberOfGalA
	 */
	public int getNumberOfGalA() {
		return numberOfGalA;
	}
	/**
	 * @param numberOfGalA the numberOfGalA to set
	 */
	public void setNumberOfGalA(int numberOfGalA) {
		this.numberOfGalA = numberOfGalA;
	}
	/**
	 * @return the numberOfGalNAc
	 */
	public int getNumberOfGalNAc() {
		return numberOfGalNAc;
	}
	/**
	 * @param numberOfGalNAc the numberOfGalNAc to set
	 */
	public void setNumberOfGalNAc(int numberOfGalNAc) {
		this.numberOfGalNAc = numberOfGalNAc;
	}
	/**
	 * @return the numberOfGlc
	 */
	public int getNumberOfGlc() {
		return numberOfGlc;
	}
	/**
	 * @param numberOfGlc the numberOfGlc to set
	 */
	public void setNumberOfGlc(int numberOfGlc) {
		this.numberOfGlc = numberOfGlc;
	}
	/**
	 * @return the numberOfGlcN
	 */
	public int getNumberOfGlcN() {
		return numberOfGlcN;
	}
	/**
	 * @param numberOfGlcN the numberOfGlcN to set
	 */
	public void setNumberOfGlcN(int numberOfGlcN) {
		this.numberOfGlcN = numberOfGlcN;
	}
	/**
	 * @return the numberOfGlcA
	 */
	public int getNumberOfGlcA() {
		return numberOfGlcA;
	}
	/**
	 * @param numberOfGlcA the numberOfGlcA to set
	 */
	public void setNumberOfGlcA(int numberOfGlcA) {
		this.numberOfGlcA = numberOfGlcA;
	}
	/**
	 * @return the numberOfGlcNAc
	 */
	public int getNumberOfGlcNAc() {
		return numberOfGlcNAc;
	}
	/**
	 * @param numberOfGlcNAc the numberOfGlcNAc to set
	 */
	public void setNumberOfGlcNAc(int numberOfGlcNAc) {
		this.numberOfGlcNAc = numberOfGlcNAc;
	}
	/**
	 * @return the umberOfIdoA
	 */
	public int getNumberOfIdoA() {
		return numberOfIdoA;
	}
	/**
	 * @param umberOfIdoA the umberOfIdoA to set
	 */
	public void setNumberOfIdoA(int numberOfIdoA) {
		this.numberOfIdoA = numberOfIdoA;
	}
	/**
	 * @return the numberOfKdn
	 */
	public int getNumberOfKdn() {
		return numberOfKdn;
	}
	/**
	 * @param numberOfKdn the numberOfKdn to set
	 */
	public void setNumberOfKdn(int numberOfKdn) {
		this.numberOfKdn = numberOfKdn;
	}
	/**
	 * @return the numberOfMan
	 */
	public int getNumberOfMan() {
		return numberOfMan;
	}
	/**
	 * @param numberOfMan the numberOfMan to set
	 */
	public void setNumberOfMan(int numberOfMan) {
		this.numberOfMan = numberOfMan;
	}
	/**
	 * @return the numberOfManN
	 */
	public int getNumberOfManN() {
		return numberOfManN;
	}
	/**
	 * @param numberOfManN the numberOfManN to set
	 */
	public void setNumberOfManN(int numberOfManN) {
		this.numberOfManN = numberOfManN;
	}
	/**
	 * @return the numberOfManA
	 */
	public int getNumberOfManA() {
		return numberOfManA;
	}
	/**
	 * @param numberOfManA the numberOfManA to set
	 */
	public void setNumberOfManA(int numberOfManA) {
		this.numberOfManA = numberOfManA;
	}
	/**
	 * @return the numberOfManNAc
	 */
	public int getNumberOfManNAc() {
		return numberOfManNAc;
	}
	/**
	 * @param numberOfManNAc the numberOfManNAc to set
	 */
	public void setNumberOfManNAc(int numberOfManNAc) {
		this.numberOfManNAc = numberOfManNAc;
	}
	/**
	 * @return the numberOfNeuAc
	 */
	public int getNumberOfNeuAc() {
		return numberOfNeuAc;
	}
	/**
	 * @param numberOfNeuAc the numberOfNeuAc to set
	 */
	public void setNumberOfNeuAc(int numberOfNeuAc) {
		this.numberOfNeuAc = numberOfNeuAc;
	}
	/**
	 * @return the numberOfNeuGc
	 */
	public int getNumberOfNeuGc() {
		return numberOfNeuGc;
	}
	/**
	 * @param numberOfNeuGc the numberOfNeuGc to set
	 */
	public void setNumberOfNeuGc(int numberOfNeuGc) {
		this.numberOfNeuGc = numberOfNeuGc;
	}
	/**
	 * @return the numberOfXyl
	 */
	public int getNumberOfXyl() {
		return numberOfXyl;
	}
	/**
	 * @param numberOfXyl the numberOfXyl to set
	 */
	public void setNumberOfXyl(int numberOfXyl) {
		this.numberOfXyl = numberOfXyl;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	@XmlAttribute
	public Double getMass() {
		return mass;
	}
	public void setMass(Double mass) {
		this.mass = mass;
	}
	@JsonProperty(value="motif")
	public List<String> getMotifs() {
		return motifs;
	}
	public void setMotifs(List<String> motifs) {
		this.motifs = motifs;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	@JsonProperty(value="tag")
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
