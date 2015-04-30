package org.glyspace.registry.controller;

import java.util.List;

import javax.validation.Valid;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.service.MotifManager;
import org.glyspace.registry.utils.ImageGenerator;
import org.glyspace.registry.view.Confirmation;
import org.glyspace.registry.view.MotifEntityList;
import org.glyspace.registry.view.MotifInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Controller
@Api(value="/motifs", description="Motif Management")
@RequestMapping ("/motifs")
public class MotifController {
	
	@Autowired
	MotifManager motifManager;
	
	@Autowired
	ImageGenerator imageGenerator;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes={"application/xml", "application/json"})
    @ApiOperation(value="Adds a new motif to the system", 
    			response=Confirmation.class, notes="Administrator use only")
    @ApiResponses(value ={@ApiResponse(code=201, message="Motif added successfully"),
    		@ApiResponse(code=400, message="Illegal argument - Motif should be valid"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Confirmation addMotif (
			@RequestBody 
			@Valid 
			@ApiParam(required=true, value="Motif")
			MotifInput motif) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		motifManager.createMotif(motif);
		return new Confirmation ("Motif added successfully", HttpStatus.CREATED.value());	
	}
	
	@RequestMapping(value = "/add/{motifname}/sequence", method = RequestMethod.POST, consumes={"application/xml", "application/json"})
    @ApiOperation(value="Adds a new sequence to a given motif", 
    			response=Confirmation.class, notes="Administrator use only")
    @ApiResponses(value ={@ApiResponse(code=201, message="Motif sequence added successfully"),
    		@ApiResponse(code=400, message="Illegal argument - Motif sequence should be valid"),
    		@ApiResponse(code=404, message="Motif does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Confirmation addMotifSequence (
			@RequestBody 
			@Valid 
			@ApiParam(required=true, value="motif sequence")
			MotifSequence sequence, 
			@ApiParam(required=true, value="motif name")
			@PathVariable ("motifname") String motifName) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		motifManager.addSequenceToMotif (motifName, sequence);
		return new Confirmation ("Motif sequence added successfully", HttpStatus.CREATED.value());	
	}
	
	@RequestMapping(value = "/add/{motifname}/tag", method = RequestMethod.POST)
    @ApiOperation(value="Adds a new tag to a given motif", 
    			response=Confirmation.class, notes="Administrator use only")
    @ApiResponses(value ={@ApiResponse(code=201, message="Motif modified successfully"),
    		@ApiResponse(code=404, message="Motif does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Confirmation addMotifTag (
			@ApiParam(required=true, value="new tag")
			@RequestParam
			String tag,
			@ApiParam(required=true, value="motif name")
			@PathVariable ("motifname") String motifName) {
		motifManager.addTagToMotif (motifName, tag);
		return new Confirmation ("Motif modified successfully", HttpStatus.OK.value());	
	}
	
	@RequestMapping(value = "/delete/{motifId}", method = RequestMethod.DELETE)
	@ApiOperation(value="deletes the motif with the given id", 
			response=Confirmation.class, notes="Administrator use only")
		@ApiResponses(value ={@ApiResponse(code=201, message="Motif deleted successfully"),
		@ApiResponse(code=404, message="Motif does not exist"),
		@ApiResponse(code=401, message="Unauthorized"),
		@ApiResponse(code=403, message="Not enough privileges"),
		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Confirmation deleteMotif (
			@ApiParam(required=true, value="motif id")
			@PathVariable("motifId")
			Integer motifId) {
		motifManager.deleteMotif(motifId);
		return new Confirmation ("Motif deleted successfully", HttpStatus.OK.value());	
	}
	
	@RequestMapping(value = "/delete/sequence/{sequenceId}", method = RequestMethod.DELETE)
	@ApiOperation(value="deletes the motif sequence with the given id", 
			response=Confirmation.class, notes="Administrator use only")
		@ApiResponses(value ={@ApiResponse(code=201, message="Motif sequence deleted successfully"),
		@ApiResponse(code=404, message="Motif/Motif Sequence does not exist"),
		@ApiResponse(code=401, message="Unauthorized"),
		@ApiResponse(code=403, message="Not enough privileges"),
		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Confirmation deleteMotifSequence (
			@ApiParam(required=true, value="sequence id")
			@PathVariable("sequenceId")
			Integer sequenceId) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException {
		motifManager.deleteSequenceFromMotif(sequenceId);
		return new Confirmation ("Motif sequence deleted successfully", HttpStatus.OK.value());	
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET, produces={"application/xml", "application/json"})
    @ApiOperation(value="Retrieves the motif with given name", 
    			response=MotifEntity.class)
    @ApiResponses(value ={@ApiResponse(code=200, message="Motif returned successfully"),
    		@ApiResponse(code=404, message="Motif does not exist"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody MotifEntity getMotif (
			@ApiParam(value="motif", required=true) 
			@RequestParam("motifName") String motifName) {
		return motifManager.getMotif(motifName);
	}
	
	
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces={"application/xml", "application/json"})
    @ApiOperation(value="Retrieves all the motifs in the system", 
    			response=MotifEntityList.class)
    @ApiResponses(value ={@ApiResponse(code=200, message="Motifs returned successfully"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody MotifEntityList getAllMotifs(){
		MotifEntityList list = new MotifEntityList();
		list.setMotifs(motifManager.getAll());
		return list;
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET, produces={"application/xml", "application/json"})
    @ApiOperation(value="Retrieves all the motifs having only the given classifications", 
			response=MotifEntityList.class, notes="Specify a list of tags (e.g. tag1, tag2) for the query parameter tag. "
					+ "queryType can be omitted to get all motifs containing all the tags listed, "
					+ "or set to OR to get all motifs containining any of the tags listed")
			@ApiResponses(value ={@ApiResponse(code=200, message="Motifs returned successfully"),
			@ApiResponse(code=415, message="Media type is not supported"),
			@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody MotifEntityList getMotifsByTags (
			@ApiParam(name="tag", value="list of tags") 
			@RequestParam("tag") List<String> tags,
			@ApiParam(name="queryType", required=false, defaultValue="AND", value="and/or")
			@RequestParam(value="queryType", defaultValue="AND", required=false)
			String queryType) {
		MotifEntityList list = new MotifEntityList();
		if (queryType == null || queryType.equalsIgnoreCase("AND")) {
			list.setMotifs(motifManager.getMotifsByTags(tags));
		} else {
			// OR
			// return all motifs containing any of the tags
			list.setMotifs(motifManager.getMotifsByAnyTags (tags));
		}
		return list;
	}
	
	@RequestMapping(value = "/image/{sequenceid}", method = RequestMethod.GET, produces={MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @ApiOperation(value="Retrieves the image of a motif given its sequence id", 
    			response=Byte[].class)
    @ApiResponses(value ={@ApiResponse(code=200, message="Motif sequence image returned successfully"),
    		@ApiResponse(code=400, message="Illegal argument"),
    		@ApiResponse(code=404, message="Motif sequence does not exist"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody ResponseEntity<byte[]> getMotifImage (
			@ApiParam(value="sequenceid", required=true) 
			@PathVariable("sequenceid") Integer sequenceId,
			@ApiParam(required=false, value="format of the the glycan image", defaultValue="png") 
    		@RequestParam("format") 
    		String format,
    		@ApiParam(required=false, value="notation to use to generate the image", defaultValue="cfg") 
    		@RequestParam("notation") 
    		String notation,
    		@ApiParam(required=false, value="style of the image", defaultValue="compact") 
    		@RequestParam("style") 
    		String style) throws Exception {
		
		MotifSequence seq = motifManager.getMotifSequence(sequenceId);
		byte[] bytes = imageGenerator.getImage(seq.getSequence(), format, notation, style);
		
		HttpHeaders headers = new HttpHeaders();
    	if (format == null || format.equalsIgnoreCase("png")) {    
		    headers.setContentType(MediaType.IMAGE_PNG);
    	} else if (format.equalsIgnoreCase("svg")) {
		    headers.setContentType(MediaType.APPLICATION_XML);
    	} else if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
    		headers.setContentType(MediaType.IMAGE_JPEG);
    	}
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value="/update/{sequenceid}/reducing", method = RequestMethod.PUT, produces={"application/xml", "application/json"})
	@ApiOperation(value="Changes the reducing flag of a given motif sequence", 
		response=Confirmation.class, notes="if value parameter is omitted (N/A), it means that motif sequence can match any part of the glycan")
	@ApiResponses(value ={@ApiResponse(code=200, message="Motif sequence updated successfully"),
		@ApiResponse(code=400, message="Illegal argument"),
		@ApiResponse(code=401, message="Unauthorized"),
		@ApiResponse(code=404, message="Motif sequence does not exist"),
		@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody Confirmation updateMotifSequenceSetReducing (
			@ApiParam (name="sequenceid", value="sequence id to modify", required=true)
			@PathVariable("sequenceid") Integer sequenceId,
			@ApiParam (value="true, false or omit the value to set it to N/A", required=false, name ="value")
			@RequestParam("value") String reducing) {
		if (reducing == null || reducing.isEmpty()) {
			// Not Applicable (n/a)
			motifManager.motifSequenceUpdateReducing(sequenceId, null);
		} else if (!reducing.isEmpty()) {
			if (reducing.equalsIgnoreCase("true"))
				motifManager.motifSequenceUpdateReducing(sequenceId, true);
			else if (reducing.equalsIgnoreCase("false"))
				motifManager.motifSequenceUpdateReducing(sequenceId, false);
			else 
				throw new IllegalArgumentException("Invalid Input: Reducing value is not valid");
		}
		return new Confirmation ("Sequence updated successfully", HttpStatus.OK.value());
	}
}
