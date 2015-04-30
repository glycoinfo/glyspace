package org.glyspace.registry.view;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.database.MotifEntity;

import com.wordnik.swagger.annotations.ApiModel;

@XmlRootElement (name="motifs")
@ApiModel (value="MotifList", description="List of Motifs")
public class MotifEntityList {

	List<MotifEntity> motifs;

	@XmlElement(name="motif")
	public List<MotifEntity> getMotifs() {
		return motifs;
	}

	public void setMotifs(List<MotifEntity> motifs) {
		this.motifs = motifs;
	}
	
}
