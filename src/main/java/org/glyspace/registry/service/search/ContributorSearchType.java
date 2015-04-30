package org.glyspace.registry.service.search;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glyspace.registry.service.GlycanManager;
import org.glyspace.registry.view.User;

@XmlRootElement(name="contributor-search")
public class ContributorSearchType extends SingleSearch {

	User input;
	
	@Override
	public List<String> search(GlycanManager glycanManager) throws Exception {
		if (input == null || 
			(input.getLoginId() == null && 
			input.getAffiliation() == null && 
			input.getLoginId() == null && 
			input.getEmail() == null)) {
				// invalid input
				throw new IllegalArgumentException("Invalid input: You should at least provide one field of the user");
			} else if ((input.getLoginId() == null || (input.getLoginId() != null && input.getLoginId().isEmpty())) && 
					(input.getAffiliation() == null || (input.getAffiliation() != null && input.getAffiliation().isEmpty())) && 
					(input.getEmail() == null || (input.getEmail() != null && input.getEmail().isEmpty())) &&
					(input.getFullName() == null || (input.getFullName() != null && input.getFullName().isEmpty()) ) ) {
				// invalid input
				throw new IllegalArgumentException("Invalid input: You should at least provide one field of the user");		
			}
		return glycanManager.getGlycansByContributor(input);
	}

	/**
	 * @return the input
	 */
	@XmlElement(name="user-input")
	public User getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(User input) {
		this.input = input;
	}
}
