package org.glyspace.registry.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="settings")
public class SettingEntity {

	String name;
	String value;
	
	/**
	 * @return the name
	 */
	@Column(nullable=false, unique=true, length=255)
	@NotEmpty
	@Id
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
	 * @return the value
	 */
	@Column(nullable=false, length=255)
	@NotEmpty
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
