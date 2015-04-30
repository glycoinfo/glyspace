package org.glyspace.registry.service.search;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.glyspace.registry.service.GlycanManager;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
	@JsonSubTypes.Type(value=CombinationSearch.class, name="combination-search"),
	@JsonSubTypes.Type(value=SubstructureSearchType.class, name="substructure"),
	@JsonSubTypes.Type(name="composition",value=CompositionSearchType.class),
	@JsonSubTypes.Type(name="id-search",value=IdSearchType.class),
	@JsonSubTypes.Type(name="exact-search",value=ExactSearchType.class),
	@JsonSubTypes.Type(name="contributor-search",value=ContributorSearchType.class),
	@JsonSubTypes.Type(name="motif-search",value=MotifSearchType.class),
}) 
@XmlTransient
public abstract class SearchType {

	abstract List<String> search (GlycanManager glycanManager) throws Exception;
}
