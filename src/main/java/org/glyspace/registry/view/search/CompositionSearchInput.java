package org.glyspace.registry.view.search;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;


@XmlRootElement(name="composition-search")
@ApiModel (value="CompositionSearchInput", description="List of Ranges")
public class CompositionSearchInput {

	Range hexose;
	Range hexA;
	Range hexN;
	Range pentose;
	Range neuAc;
	Range neuGc;
	Range kdn;
	Range kdo;
	Range dHex;
	Range hexNac;
	Range other;
	
	//substituent only
	Range sulfate;
	Range phosphate;
	Range methyl;
	Range acetyl;
	
	/**
	 * @return the hexose
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Hexose")
	public Range getHexose() {
		return hexose;
	}
	/**
	 * @param hexose the hexose to set
	 */
	public void setHexose(Range hexose) {
		this.hexose = hexose;
	}
	/**
	 * @return the hexA
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for HexA")
	public Range getHexA() {
		return hexA;
	}
	/**
	 * @param hexA the hexA to set
	 */
	public void setHexA(Range hexA) {
		this.hexA = hexA;
	}
	
	/**
	 * @return the hexN
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for HexN")
	public Range getHexN() {
		return hexN;
	}
	
	/**
	 * @param hexN the hexN to set
	 */
	public void setHexN(Range hexN) {
		this.hexN = hexN;
	}
	/**
	 * @return the pentose
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Pentose")
	public Range getPentose() {
		return pentose;
	}
	/**
	 * @param pentose the pentose to set
	 */
	public void setPentose(Range pentose) {
		this.pentose = pentose;
	}
	/**
	 * @return the neuAc
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for NeuAC")
	public Range getNeuAc() {
		return neuAc;
	}
	/**
	 * @param neuAc the neuAc to set
	 */
	public void setNeuAc(Range neuAc) {
		this.neuAc = neuAc;
	}
	/**
	 * @return the neuGc
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for NeuGC")
	public Range getNeuGc() {
		return neuGc;
	}
	/**
	 * @param neuGc the neuGc to set
	 */
	public void setNeuGc(Range neuGc) {
		this.neuGc = neuGc;
	}
	/**
	 * @return the kdn
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for KDN")
	public Range getKdn() {
		return kdn;
	}
	/**
	 * @param kdn the kdn to set
	 */
	public void setKdn(Range kdn) {
		this.kdn = kdn;
	}
	/**
	 * @return the kdo
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for KDO")
	public Range getKdo() {
		return kdo;
	}
	/**
	 * @param kdo the kdo to set
	 */
	public void setKdo(Range kdo) {
		this.kdo = kdo;
	}
	/**
	 * @return the dHex
	 */
	@XmlElement(name="dHex")
	@Valid
	@ApiModelProperty (value="Min/Max value for dHex")
	public Range getdHex() {
		return dHex;
	}
	/**
	 * @param dHex the dHex to set
	 */
	public void setdHex(Range dHex) {
		this.dHex = dHex;
	}
	/**
	 * @return the sulphate
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Sulfate")
	public Range getSulfate() {
		return sulfate;
	}
	/**
	 * @param sulphate the sulphate to set
	 */
	public void setSulfate(Range sulphate) {
		this.sulfate = sulphate;
	}
	/**
	 * @return the phosphate
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Phosphate")
	public Range getPhosphate() {
		return phosphate;
	}
	/**
	 * @param phosphate the phosphate to set
	 */
	public void setPhosphate(Range phosphate) {
		this.phosphate = phosphate;
	}
	/**
	 * @return the methyl
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Methyl")
	public Range getMethyl() {
		return methyl;
	}
	/**
	 * @param methyl the methyl to set
	 */
	public void setMethyl(Range methyl) {
		this.methyl = methyl;
	}
	/**
	 * @return the acetyl
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Acetyl")
	public Range getAcetyl() {
		return acetyl;
	}
	/**
	 * @param acetyl the acetyl to set
	 */
	public void setAcetyl(Range acetyl) {
		this.acetyl = acetyl;
	}
	/**
	 * @return the hexNac
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for HexNac")
	public Range getHexNac() {
		return hexNac;
	}
	/**
	 * @param hexNac the hexNac to set
	 */
	public void setHexNac(Range hexNac) {
		this.hexNac = hexNac;
	}
	/**
	 * @return the other
	 */
	@XmlElement
	@Valid
	@ApiModelProperty (value="Min/Max value for Others")
	public Range getOther() {
		return other;
	}
	/**
	 * @param other the other to set
	 */
	public void setOther(Range other) {
		this.other = other;
	}
	
}
