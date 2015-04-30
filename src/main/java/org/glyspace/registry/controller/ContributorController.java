package org.glyspace.registry.controller;

import org.glyspace.registry.dao.exceptions.UserNotFoundException;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.service.GlycanManager;
import org.glyspace.registry.service.UserManager;
import org.glyspace.registry.view.Confirmation;
import org.glyspace.registry.view.Contributor;
import org.glyspace.registry.view.GlycanList;
import org.glyspace.registry.view.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value="/contributor", description="Contributor Management")
@RequestMapping ("/contributor")
@Controller
public class ContributorController {
	
	@Autowired
	UserManager userManager;
	
	@Autowired
	GlycanManager glycanManager;

	@RequestMapping(value = "/{username}", method = RequestMethod.GET, produces={"application/xml", "application/json"})
    @ApiOperation(value="Retrieves the contributor information", 
    			response=Contributor.class)
    @ApiResponses(value ={@ApiResponse(code=200, message="Success"),
    		@ApiResponse(code=400, message="Illegal argument - username should be valid"),
    		@ApiResponse(code=404, message="Contributor not found"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Contributor getContributorInfo (
			@ApiParam(name="username", required=true)
			@PathVariable("username") String username) {
		UserEntity user = userManager.getUserByLoginId(username, true, false);
		if (user == null) {
			throw new UserNotFoundException(username + " is not found");
		}
		
		Contributor contributor = new Contributor();
		contributor.setAffiliation(user.getAffiliation());
		contributor.setDateRegistered(user.getDateRegistered());
		contributor.setFullName(user.getUserName());
		contributor.setLastLoggedIn(user.getLastLoggedIn());
		contributor.setLoginId(user.getLoginId());
		
		User searchUser = new User();
		searchUser.setLoginId(username);
		GlycanList glycanList = new GlycanList();
		glycanList.setGlycans(glycanManager.getGlycansByContributor(searchUser).toArray());
		contributor.setGlycans(glycanList);
		return contributor;
	}
}
