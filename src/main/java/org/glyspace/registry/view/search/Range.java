package org.glyspace.registry.view.search;

import javax.xml.bind.annotation.XmlElement;

public class Range {

	Integer min;
	Integer max;

	public Range() {
	}
	
	public Range(Integer min) {
		this.min = min;
		this.max = min;
	}
	
	public Range (Integer min, Integer max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean between (int num) {
		if (this.min == null && this.max != null)
			this.min=0;
		if (this.max == null && this.min != null) 
			this.max = this.min;
		else if (this.min == null && this.max == null)
			return false;
		if (num >= this.min && num <= this.max)
			return true;
		return false;
	}
	
	/**
	 * @return the min
	 */
	@XmlElement
	public Integer getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Integer min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	@XmlElement
	public Integer getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Integer max) {
		this.max = max;
	}

}
