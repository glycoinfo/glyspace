package org.glyspace.registry.service.search;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.glyspace.registry.service.GlycanManager;
import org.glyspace.registry.view.Glycan;
import org.glyspace.registry.view.User;
import org.glyspace.registry.view.search.CompositionSearchInput;
import org.glyspace.registry.view.search.Range;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;

@XmlRootElement(name="combination-search")
@XmlType ( propOrder = {"search1", "search2"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
public class CombinationSearch extends SearchType{

	SearchType search1;
	SearchType search2;
	Operation operation;
	
	@Override
	public List<String> search(GlycanManager glycanManager) throws Exception {
		Set<String> finalResult = new HashSet<>();
		List<String> result1 = search1.search(glycanManager);
		List<String> result2 = search2.search(glycanManager);
		
		switch (operation) {
			case UNION :  
				finalResult.addAll(result1);
				finalResult.addAll(result2);
				break;
			case INTERSECTION: 
				finalResult.addAll(result1);
				Set<String> tempSet = new HashSet<>(result2);
				finalResult.retainAll(tempSet);
				break;
			case DIFFERENCE:
				finalResult.addAll(result1);
				finalResult.removeAll(result2);
				break;
		}	
		return new ArrayList<String>(finalResult);
	}

	/**
	 * @return the search1
	 */
	@XmlElements({
        @XmlElement(name="combination-search1",type=CombinationSearch.class),
        @XmlElement(name="substructure1",type=SubstructureSearchType.class),
        @XmlElement(name="composition1",type=CompositionSearchType.class),
        @XmlElement(name="id-search1",type=IdSearchType.class),
        @XmlElement(name="exact-search1",type=ExactSearchType.class),
        @XmlElement(name="contributor-search1",type=ContributorSearchType.class),
        @XmlElement(name="motif-search1",type=MotifSearchType.class),
    })
	public SearchType getSearch1() {
		return search1;
	}

	/**
	 * @param search1 the search1 to set
	 */
	public void setSearch1(SearchType query1) {
		this.search1 = query1;
	}

	/**
	 * @return the search2
	 */
	@XmlElements({
        @XmlElement(name="combination-search2",type=CombinationSearch.class),
        @XmlElement(name="substructure2",type=SubstructureSearchType.class),
        @XmlElement(name="composition2",type=CompositionSearchType.class),
        @XmlElement(name="id-search2",type=IdSearchType.class),
        @XmlElement(name="exact-search2",type=ExactSearchType.class),
        @XmlElement(name="contributor-search2",type=ContributorSearchType.class),
        @XmlElement(name="motif-search2",type=MotifSearchType.class),
    })
	public SearchType getSearch2() {
		return search2;
	}

	/**
	 * @param search2 the search2 to set
	 */
	public void setSearch2(SearchType query2) {
		this.search2 = query2;
	}

	/**
	 * @return the operation
	 */
	@XmlAttribute
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	public static void main(String[] args) throws Exception
	  {
	    ObjectMapper mapper = new ObjectMapper();
	    CombinationSearch search = new CombinationSearch();
	    SubstructureSearchType subs = new SubstructureSearchType();
	    Glycan glycan = new Glycan();
	    glycan.setStructure("RES\n1b:b-dglc-HEX-1:5\n2s:n-acetyl\n3b:b-dgal-HEX-1:5\nLIN\n1:1d(2+1)2n\n2:1o(4+1)3d");
	    glycan.setEncoding("");
	    subs.setInput(glycan);
	    
	    ExactSearchType subs2 = new ExactSearchType();
	    Glycan glycan2 = new Glycan();
	    glycan2.setStructure("RES\n1b:b-dglc-HEX-1:5\n2s:n-acetyl\n3b:b-dgal-HEX-1:5\nLIN\n1:1d(2+1)2n\n2:1o(4+1)3d");
	    glycan2.setEncoding("glycoct_condensed");
	    subs2.setInput(glycan2);
	    
	    MotifSearchType msearch = new MotifSearchType();
	    msearch.setInput("Lactosamine motif");
	    
	    ContributorSearchType cSearch = new ContributorSearchType();
	    User user = new User();
	    user.setLoginId("test");
	    cSearch.setInput(user);
	    
	    CombinationSearch search2 = new CombinationSearch();
	    search2.setSearch1(msearch);
	    
	    CompositionSearchType compS = new CompositionSearchType();
	    CompositionSearchInput input = new CompositionSearchInput();
		input.setNeuAc(new Range(2));
		input.setHexNac(new Range(7));
		input.setdHex(new Range(1));
		input.setNeuGc(new Range(1));
		input.setHexose(new Range(7));
		input.setOther(new Range(5));
		compS.setInput(input);
		
		search2.setSearch2(cSearch);
		search2.setOperation(Operation.DIFFERENCE);
		
	    search.setSearch1(subs);
	    search.setSearch2(search2);
	    search.setOperation(Operation.INTERSECTION);
	    System.out.println(mapper.writeValueAsString(search));
	  
	   
	  	  try {
	  		JAXBContext jaxbContext = JAXBContext.newInstance(CombinationSearch.class);
	  		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	   
	  		// output pretty printed
	  		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	   
	  		jaxbMarshaller.marshal(search, System.out);
	   
	  	      } catch (JAXBException e) {
	  		e.printStackTrace();
	  	      }
	   
	
	String test = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
"	<combination-search operation=\"INTERSECTION\">\n" +
"	    <substructure1>\n" +
"	        <glycan-structure encoding=\"\">\n" +
"	            <structure>&lt;![CDATA[RES\n" +
"	1b:b-dglc-HEX-1:5\n" +
"	2s:n-acetyl\n" +
"	3b:b-dgal-HEX-1:5\n" +
"	LIN\n" +
"	1:1d(2+1)2n\n" +
"	2:1o(4+1)3d]]&gt;</structure>\n" +
"	        </glycan-structure>\n" +
"	    </substructure1>\n" +
"	    <combination-search2 operation=\"UNION\">\n" +
"	        <exact-search1>\n" +
"	            <glycan-structure encoding=\"glycoct_condensed\">\n" +
"	                <structure>&lt;![CDATA[RES\n" +
"	1b:b-dglc-HEX-1:5\n" +
"	2s:n-acetyl\n" +
"	3b:b-dgal-HEX-1:5\n" +
"	LIN\n" +
"	1:1d(2+1)2n\n" +
"	2:1o(4+1)3d]]&gt;</structure>\n" +
"	            </glycan-structure>\n" +
"	        </exact-search1>\n" +
"	        <composition2>\n" +
"	            <composition-search>\n" +
"	                <hexNac>\n" +
"	                    <max>7</max>\n" +
"	                    <min>7</min>\n" +
"	                </hexNac>\n" +
"	                <hexose>\n" +
"	                    <max>7</max>\n" +
"	                    <min>7</min>\n" +
"	                </hexose>\n" +
"	                <neuAc>\n" +
"	                    <max>2</max>\n" +
"	                    <min>2</min>\n" +
"	                </neuAc>\n" +
"	                <neuGc>\n" +
"	                    <max>1</max>\n" +
"	                    <min>1</min>\n" +
"	                </neuGc>\n" +
"	                <other>\n" +
"	                    <max>5</max>\n" +
"	                    <min>5</min>\n" +
"	                </other>\n" +
"	                <dHex>\n" +
"	                    <max>1</max>\n" +
"	                    <min>1</min>\n" +
"	                </dHex>\n" +
"	            </composition-search>\n" +
"	        </composition2>\n" +
"	    </combination-search2>\n" +
"	</combination-search>\n";
	
	String test2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"	<combination-search operation=\"INTERSECTION\">\n" +
			"	    <substructure1>\n" +
			"	        <glycan-structure encoding=\"\">\n" +
			"	            <structure>&lt;![CDATA[RES\n" +
			"	1b:b-dglc-HEX-1:5\n" +
			"	2s:n-acetyl\n" +
			"	3b:b-dgal-HEX-1:5\n" +
			"	LIN\n" +
			"	1:1d(2+1)2n\n" +
			"	2:1o(4+1)3d]]&gt;</structure>\n" +
			"	        </glycan-structure>\n" +
			"	    </substructure1>\n" +
			"	        <exact-search2>\n" +
			"	            <glycan-structure encoding=\"glycoct_condensed\">\n" +
			"	                <structure>&lt;![CDATA[RES\n" +
			"	1b:b-dglc-HEX-1:5\n" +
			"	2s:n-acetyl\n" +
			"	3b:b-dgal-HEX-1:5\n" +
			"	LIN\n" +
			"	1:1d(2+1)2n\n" +
			"	2:1o(4+1)3d]]&gt;</structure>\n" +
			"	            </glycan-structure>\n" +
			"	        </exact-search2>\n" +
			"	</combination-search>\n";
	
	try {
  		JAXBContext jaxbContext = JAXBContext.newInstance(CombinationSearch.class);
  		Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
   
  		CombinationSearch s = (CombinationSearch)jaxbUnMarshaller.unmarshal(new StringReader(test));
        System.out.println ("search1" + s.getSearch1());
        System.out.println ("search2" + s.getSearch2());
        
  	      } catch (JAXBException e) {
  		e.printStackTrace();
  	      }
	  }
}
