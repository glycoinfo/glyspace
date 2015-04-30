package org.glyspace.registry.controller;

import org.glyspace.registry.service.SettingsManager;
import org.glyspace.registry.view.Confirmation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Controller
@Api(value="/settings", description="Settings Management")
@RequestMapping ("/settings")
public class SettingsController {
	
	@Autowired
	SettingsManager settingsManager;
	
	@RequestMapping(value="/delay", method=RequestMethod.PUT, consumes="application/json")
	@ApiOperation(value="Updates the delay period for newly submitted glycans", notes="Only the administrator can change this setting")
	@ApiResponses(value ={@ApiResponse(code=200, message="Updated successfully"), 
			        @ApiResponse(code=400, message="Illegal argument - Delay cannot be left empty"),
    				@ApiResponse(code=401, message="Unauthorized"),
    				@ApiResponse(code=403, message="User is not allowed to change this setting"),
    				@ApiResponse(code=500, message="Internal Server Error")})
	public ResponseEntity<Confirmation> setDelay (
			@RequestBody (required=true)
			@ApiParam(required=true, value="New delay duration") 
			Long newDelay) {
		
		settingsManager.setDelay(newDelay);
		return new ResponseEntity<Confirmation> (new Confirmation("Delay period updated successfully", HttpStatus.OK.value()), HttpStatus.OK);
	}
	
	@RequestMapping(value="/delay", method=RequestMethod.GET)
	@ApiOperation(value="Gets the current value for the delay period for newly submitted glycans", notes="Only the administrator can change this setting")
	@ApiResponses(value ={@ApiResponse(code=200, message="Retrieved successfully"), 
    				@ApiResponse(code=401, message="Unauthorized"),
    				@ApiResponse(code=403, message="User is not allowed to see this setting"),
    				@ApiResponse(code=500, message="Internal Server Error")})
	public ResponseEntity<Long> getDelay () {
		return new ResponseEntity<Long> (settingsManager.getDelay(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/quotaPeriod", method=RequestMethod.PUT, consumes="application/json")
	@ApiOperation(value="Updates the quota period", notes="Only the administrator can change this setting")
	@ApiResponses(value ={@ApiResponse(code=200, message="Updated successfully"), 
			        @ApiResponse(code=400, message="Illegal argument - Quota period cannot be left empty"),
    				@ApiResponse(code=401, message="Unauthorized"),
    				@ApiResponse(code=403, message="User is not allowed to change this setting"),
    				@ApiResponse(code=500, message="Internal Server Error")})
	public ResponseEntity<Confirmation> setQuotaPeriod (
			@RequestBody (required=true)
			@ApiParam(required=true, value="New quota duration") 
			Long newQuotaPeriod) {
		
		settingsManager.setQuotaPeriod(newQuotaPeriod);
		return new ResponseEntity<Confirmation> (new Confirmation("Quota period updated successfully", HttpStatus.OK.value()), HttpStatus.OK);
	}
	
	@RequestMapping(value="/quotaPeriod", method=RequestMethod.GET)
	@ApiOperation(value="Gets the current value for the quota period for the users", notes="Only the administrator can change this setting")
	@ApiResponses(value ={@ApiResponse(code=200, message="Retrieved successfully"), 
    				@ApiResponse(code=401, message="Unauthorized"),
    				@ApiResponse(code=403, message="User is not allowed to see this setting"),
    				@ApiResponse(code=500, message="Internal Server Error")})
	public ResponseEntity<Long> getQuotaPeriod () {
		return new ResponseEntity<Long> (settingsManager.getQuotaPeriod(), HttpStatus.OK);
	}
}
