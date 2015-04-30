package org.glyspace.registry.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.glyspace.registry.dao.exceptions.UserRoleViolationException;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.service.EmailManager;
import org.glyspace.registry.service.UserManager;
import org.glyspace.registry.view.Confirmation;
import org.glyspace.registry.view.RoleList;
import org.glyspace.registry.view.User;
import org.glyspace.registry.view.UserList;
import org.glyspace.registry.view.validation.PasswordValidator;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.qos.logback.classic.Logger;

import com.knappsack.swagger4springweb.annotation.ApiExclude;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;

/**
 * This is the REST API for the users of the system
 * Allows us to add/delete/list registered users
 * 
 * @author sena
 *
 */
@Controller
@Api(value="/users", description="User Management")
@RequestMapping ("/users")
public class UserController {

	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.dao.UserController");
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private EmailManager emailManager;
		
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Authorization (value="basicAuth", scopes={@AuthorizationScope (scope="glyspace", description="Access to GlySpace")})
	@RequestMapping(value="/signin", method=RequestMethod.GET)
	@ApiOperation(value="Check if the user's credentials are acceptable", response=Confirmation.class, notes="If the user is authorized, this does not necessarily mean that s/he is allowed to access all the resources")
	@ApiResponses (value={@ApiResponse(code=203, message="User is accepted"),
			@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=404, message="User with given login name does not exist"),
			@ApiResponse(code=500, message="Internal Server Error")})
	public @ResponseBody UserEntity signin() {
//		return new Confirmation("User is authorized", HttpStatus.ACCEPTED.value());
    	UserEntity user;
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) { 
   			user = userManager.getUserByLoginId(auth.getName(), true, true);
    	}
    	else { // should not reach here at all
    		throw new BadCredentialsException ("The user has not been authenticated");
    	}
    	return user;
	}
	
	@RequestMapping(value="/get/{userName}", method=RequestMethod.GET, produces={"application/xml", "application/json"})
    @ApiOperation(value="Retrieve the information for the given user", response=UserEntity.class)
    @ApiResponses (value ={@ApiResponse(code=200, message="User retrieved successfully"), 
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges"),
    		@ApiResponse(code=404, message="User with given login name does not exist"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody UserEntity getUser (
    		@ApiParam(required=true, value="login name of the user")
    		@PathVariable("userName")
    		String userName) {
    	UserEntity user;
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) { 
    		// username of the authenticated user should match the username parameter
    		// a user can only see his/her own user information
    		// but admin can access all the users' information
    		
    		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
    		{
    			user = userManager.getUserByLoginId(userName, false, false);
    		//	user.setPassword("");  // do not pass password information to the user
    		}
    		else if (auth.getName().equals(userName)) {
    			user = userManager.getUserByLoginId(userName, true, true);
    		//	user.setPassword("");
    		}
    		else {
    			logger.info("The user: " + auth.getName() + " is not authorized to access " + userName + "'s information");
    			throw new AccessDeniedException("The user: " + auth.getName() + " is not authorized to access " + userName + "'s information");
    		}
    	}
    	else { // should not reach here at all
    		throw new BadCredentialsException ("The user has not been authenticated");
    	}
    	return user;
    }
	

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces={"application/xml", "application/json"})
	@ResponseStatus(HttpStatus.OK)
	@ApiResponses (value ={@ApiResponse(code=200, message="Success"), 
			@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to list users"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	@ApiOperation (value="Lists all the users", response=UserList.class, notes="This can be accessed by the Administrator user only. "
			+ "If validated parameter is omitted it will list all the validated and not yet validated users. "
			+ "If active parameter is omitted it will list all the users regardless of whether they are active or not.")
    public @ResponseBody UserList listUsers(
    		@ApiParam(required=false, value="validated: true or false") 
			@RequestParam(required=false, value="validated")
			String validated, 
			@ApiParam(required=false, value="active: true or false") 
			@RequestParam(required=false, value="active")
			String active)
    {
		UserList list = new UserList();
		boolean validatedOmitted = false;
		boolean activeOmitted = false;
		if (validated == null || validated.isEmpty()) validatedOmitted = true;
		if (active == null || active.isEmpty()) activeOmitted = true;
		List<UserEntity> users;
		if (validatedOmitted && activeOmitted) {
			users = userManager.getAllUsers();
		} else if (validatedOmitted) {
			boolean a = active.equalsIgnoreCase("true") ? true : false;
			users = userManager.getUsersByActive(a);
		} else if (activeOmitted){
			boolean v = validated.equalsIgnoreCase("true") ? true : false;
			users = userManager.getUsersByValidated(v);
		} else {
			boolean a = active.equalsIgnoreCase("true") ? true : false;
			boolean v = validated.equalsIgnoreCase("true") ? true : false;
			users = userManager.getUsers(v,  a);
		}
	/*	List<UserEntity> usersWithNoPassword = new ArrayList<UserEntity>(users.size());
		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			UserEntity userEntity = (UserEntity) iterator.next();
			userEntity.setPassword("");
			usersWithNoPassword.add(userEntity);
		}
		list.setUsers(usersWithNoPassword);*/
		list.setUsers(users);
        return list;
    }
 
    @RequestMapping(value = "/add", method = RequestMethod.POST, 
    		consumes={"application/xml", "application/json"})
    @ApiOperation(value="Adds a user to the system and returns a message for successful addition", response=Confirmation.class)
    @ApiResponses (value ={@ApiResponse(code=201, message="User added successfully"),
    		@ApiResponse(code=400, message="Illegal arguments - input argument fails validation"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody Confirmation addUser( 
    		@ApiParam (required=true, value="User") 
    		@RequestBody(required=true)
    		@Valid User user)
    {
    	UserEntity newUser = new UserEntity();
    	newUser.setAffiliation(user.getAffiliation());
    	newUser.setLoginId(user.getLoginId());
    	newUser.setPassword(user.getPassword());
    	newUser.setEmail(user.getEmail());
    	newUser.setUserName(user.getFullName());
        userManager.addUser(newUser);

		Marker notifyAdmin = MarkerFactory.getMarker("NOTIFY_ADMIN");
		logger.info(notifyAdmin, "New user {} is added to the system", newUser.getLoginId());

        return new Confirmation("User added successfully", HttpStatus.CREATED.value());
    }
    
    @RequestMapping(value="/recover", method = RequestMethod.GET)
    @ApiOperation(value="Returns the user's login name when email is provided", response=String.class)
    @ApiResponses (value ={@ApiResponse(code=200, message="Username recovered successfully"), 
    		@ApiResponse(code=400, message="Illegal argument - valid email has to be provided"),
            @ApiResponse(code=404, message="User with given email does not exist"),
            @ApiResponse(code=500, message="Internal Server Error")})
    public ResponseEntity<String> recoverUsername (
    		@ApiParam(required=true, value="email of the user") 
    		@RequestParam(value="email", required=true) String email) {
    	
    	String loginId = userManager.recoverLogin(email);
		return new ResponseEntity<String>(loginId, HttpStatus.OK);
    }
    
    @RequestMapping(value="/{userName}/password", method = RequestMethod.GET)
    @ApiOperation(value="Recovers the user's password. Sends an email to the registered email of the user", response=Confirmation.class)
    @ApiResponses (value ={@ApiResponse(code=200, message="Password recovered successfully"), 
    		@ApiResponse(code=404, message="User with given login name does not exist"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody Confirmation recoverPassword (
    		@ApiParam(required=true, value="login name of the user") 
    		@PathVariable("userName") String loginId) {
    	
    	UserEntity user = userManager.getUserByLoginId(loginId, true, true);
    	emailManager.sendPasswordReminder (user);
    	logger.info("Password reminder email is sent to {}", loginId);
		return new Confirmation("Password reminder email is sent", HttpStatus.OK.value());
    	
    }
    
    @RequestMapping(value="/{userName}/password", method = RequestMethod.PUT)
    @ApiOperation(value="Changes the password for the given user", response=Confirmation.class, notes="Only authenticated user can change his/her password")
    @ApiResponses (value ={@ApiResponse(code=200, message="Password changed successfully"), 
    		@ApiResponse(code=400, message="Illegal argument - new password should be valid"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to update password"),
    		@ApiResponse(code=404, message="User with given login name does not exist"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody Confirmation changePassword (
    		Principal p,
    		@ApiParam (required=true, value="new password") 
    		@RequestBody(required=true) 
    		String newPassword, 
    		@ApiParam (required=true, value="login name for the user")
    		@PathVariable("userName") String userName) {
    	if (p == null) {
    		// not authenticated
    		throw new BadCredentialsException("Unauthorized to change the password");
    	}
    	if (!p.getName().equalsIgnoreCase(userName)) {
    		logger.warn("The user: " + p.getName() + " is not authorized to change " + userName + "'s password");
    		throw new AccessDeniedException("The user: " + p.getName() + " is not authorized to change " + userName + "'s password");
    	}
    	
    	// using @NotEmpty for newPassword didn't work, so have to handle it here
    	if (newPassword == null || newPassword.isEmpty()) {
    		throw new IllegalArgumentException("Invalid Input: new password cannot be empty");
    	}
    	logger.debug("new password is {}", newPassword);
    	Pattern pattern = Pattern.compile(PasswordValidator.PASSWORD_PATTERN);
    	if (!pattern.matcher(newPassword).matches()) {
    		throw new IllegalArgumentException("Invalid Input: The password length must be greater than or equal to 5, must contain one or more uppercase characters, "
    				+ "must contain one or more lowercase characters, must contain one or more numeric values and must contain one or more special characters");
    	}
    	userManager.changePassword(p.getName(), newPassword);
    	return new Confirmation("Password changed successfully", HttpStatus.OK.value());
    }
 
    @RequestMapping(value="/{userId}/delete", method = RequestMethod.DELETE)
    @ApiOperation(value="Deletes the user with provided id", response=Confirmation.class, notes="This can be accessed by the Administrator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="User deleted successfully"), 
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to delete users"),
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody Confirmation deleteUser(@ApiParam(required=true, value="id of the user to be deleted") @PathVariable("userId") Integer userId)
    {
   		userManager.deleteUser(userId);
   		return new Confirmation("User deleted successfully", HttpStatus.OK.value());
    }
    
    @RequestMapping(value="/{userId}/validate", method = RequestMethod.PUT)
    @ApiOperation(value="Validate a user account.", notes="This can be accessed by the Administrator or Moderator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="User updated successfully"), 
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to validate users"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public ResponseEntity<Confirmation> validateUser(@ApiParam(required=true, value="id of the user to be validated") @PathVariable("userId") Integer userId)
    {
   		userManager.validateUser(userId);
   		return new ResponseEntity<Confirmation>(new Confirmation("User validated successfully", HttpStatus.OK.value()), HttpStatus.OK);
    }
    
    @RequestMapping(value="/{userId}/activate", method = RequestMethod.PUT)
    @ApiOperation(value="Validate a user account.", response=Confirmation.class, notes="This can be accessed by the Administrator or Moderator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="User activated successfully"), 
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to activate users"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public ResponseEntity<Confirmation> activateUser(@ApiParam(required=true, value="id of the user to be activated") @PathVariable("userId") Integer userId)
    {
   		userManager.activateUser(userId);
   		return new ResponseEntity<Confirmation>(new Confirmation ("User activated successfully", HttpStatus.OK.value()), HttpStatus.OK);
    }
    
    @RequestMapping(value="/{userId}/deactivate", method = RequestMethod.PUT)
    @ApiOperation(value="Validate a user account.", response=Confirmation.class, notes="This can be accessed by the Administrator or Moderator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="User deactivated successfully"), 
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to deactivate users"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public ResponseEntity<Confirmation> deactivateUser(@ApiParam(required=true, value="id of the user to be deactivated") @PathVariable("userId") Integer userId)
    {
    	UserEntity userEntity= userManager.getUser (userId);
    	// administrator should not be allowed to deactivate himself
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) { 
    		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) &&
    				auth.getName().equals(userEntity.getLoginId())) {
    			throw new AccessDeniedException ("Administrator user cannot be deactivated");
    		}
    		else {
    			// the user can deactivate his/her own account
    			// admin can deactivate any account except his/her own
    			if (auth.getName().equals(userEntity.getLoginId()) || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
    				userManager.deactivateUser(userId);
    			}
    			else {
    				logger.warn("The user {} is not allowed to deactivate another user {}, only the Administrator is allowed to do that!", auth.getName(), userEntity.getLoginId());
    				throw new AccessDeniedException ("The user is not allowed to deactivate another user, only Administrator is allowed to do that!");
    			}
    		}
    	}
    	else { // should not reach here at all
    		throw new BadCredentialsException ("The user has not been authenticated");
    	}
   		return new ResponseEntity<Confirmation>(new Confirmation("User deactivated successfully", HttpStatus.OK.value()), HttpStatus.OK);
    }
    
    @RequestMapping(value="/{userId}/quota", method = RequestMethod.PUT)
    @ApiOperation(value="Update user's quota.", response=Confirmation.class, notes="This can be accessed by the Administrator or Moderator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="User quota updated successfully"), 
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to change user's quota"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public ResponseEntity<Confirmation> updateQuota(@ApiParam(required=true, value="id of the user whose quota to be changed") @PathVariable("userId") Integer userId,
    		@ApiParam(required=true, value="new quota") @RequestParam (value="quota", required=true) Integer newQuota)
    {
   		userManager.updateUserQuota(userId, newQuota);
   		return new ResponseEntity<Confirmation>(new Confirmation ("User's quota updated successfully", HttpStatus.OK.value()), HttpStatus.OK);
    }
    
    @RequestMapping(value="/{userId}/update", method = RequestMethod.POST, consumes={"application/xml", "application/json"}, produces={"application/xml", "application/json"})
    @ApiOperation(value="Update user account.", notes="This can be accessed by the Administrator or Moderator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="User updated successfully"), 
    		@ApiResponse(code=400, message="Illegal arguments - input argument fails validation"),
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to update users"),
    		@ApiResponse(code=415, message="Media type is not supported"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody UserEntity updateUser (
    		@ApiParam (required=true, value="User") 
    		@RequestBody (required=true)
    		User user,
    		@ApiParam(required=true, value="id of the user to be updated") 
    		@PathVariable("userId")
    		Integer userId) {
    	
    	UserEntity userEntity= userManager.getUser (userId);
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) { 
    		// a user can only update his/her own user information
    		// username of the authenticated user should match the loginId of the user retrieved from the db
    		
    		if (auth.getName().equals(userEntity.getLoginId())) {
    			if (user.getAffiliation() != null) userEntity.setAffiliation(user.getAffiliation());
    			if (user.getEmail() != null && !user.getEmail().isEmpty()) userEntity.setEmail(user.getEmail());
    			if (user.getPassword() != null && !user.getPassword().isEmpty()) userEntity.setPassword(user.getPassword());
    			if (user.getFullName() != null && !user.getFullName().isEmpty()) userEntity.setUserName(user.getFullName());
    			if (user.getLoginId() != null && !user.getLoginId().isEmpty()) userEntity.setLoginId(user.getLoginId());
    	    	userManager.modifyUser(userEntity);
    		}
    		else {
    			logger.info("The user: " + auth.getName() + " is not authorized to update user with id " + userId);
    			throw new AccessDeniedException("The user: " + auth.getName() + " is not authorized to update user with id " + userId);
    		}
    	}
    	else { // should not reach here at all
    		throw new BadCredentialsException ("The user has not been authenticated");
    	}
    	
    	return userEntity;
    }
    
    @RequestMapping(value="/{userId}/role", method=RequestMethod.POST)
    @ApiOperation(value="Promote a user by adding a new role", response=Confirmation.class, notes="This can be accessed by the Administrator user only")
    @ResponseStatus(value=HttpStatus.OK, reason="Added a new role to the user successfully")
    @ApiResponses (value ={@ApiResponse(code=200, message="Added a new role to the user successfully"), 
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to change user roles"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody Confirmation promoteUser (
    		@ApiParam(required=true, value="id of the user to be updated") 
    		@PathVariable("userId")
    		Integer userId, 
    		@ApiParam (required=true, value="role name") 
    		@RequestBody(required=true)
    		@NotEmpty String roleName) {
    	
    	userManager.addRole(userId, roleName);
    	return new Confirmation("Added a new role to the user successfully", HttpStatus.OK.value());
    }
    
    @RequestMapping(value="/{userId}/role/{roleName}", method=RequestMethod.DELETE)
    @ApiOperation(value="Revoke a user's certain role", response=Confirmation.class, notes="This can be accessed by the Administrator user only")
    @ApiResponses (value ={@ApiResponse(code=200, message="Removed the role from the user successfully"), 
    		@ApiResponse(code=400, message="Illegal Argument - "),
    		@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=404, message="User does not exist"),
    		@ApiResponse(code=403, message="Not enough privileges to change user roles"),
    		@ApiResponse(code=500, message="Internal Server Error")})
    public @ResponseBody Confirmation demoteUser (
    		@ApiParam(required=true, value="id of the user to be updated") 
    		@PathVariable("userId")
    		Integer userId, 
    		@ApiParam (required=true, value="role name to be removed from the user") 
    		@PathVariable
    		@NotEmpty String roleName) {
    	
    	UserEntity userEntity = userManager.getUser(userId);
    	// administrator should not be allowed to remove his own roles
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) { 
    		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) &&
    				auth.getName().equals(userEntity.getLoginId())) {
    			// the admin is trying to modify its own user info
    			throw new UserRoleViolationException("The Administrator is not allowed to remove his own roles");
    		}
    	}
    	else { // should not reach here at all
    		throw new BadCredentialsException ("The user has not been authenticated");
    	}
    	
    	userManager.removeRole(userId, roleName);
    	return new Confirmation("Removed the role from the user successfully", HttpStatus.OK.value());
    }
    
    @RequestMapping(value = "/roles/list", method = RequestMethod.GET, produces={"application/xml", "application/json"})
	@ApiResponses (value ={@ApiResponse(code=200, message="Success"), 
			@ApiResponse(code=401, message="Unauthorized"),
    		@ApiResponse(code=403, message="Not enough privileges to list roles"),
    		@ApiResponse(code=500, message="Internal Server Error")})
	@ApiOperation (value="Lists all the available roles", response=RoleList.class, notes="This can be accessed by the Administrator user only")
    public @ResponseBody RoleList listRoles () {
    	return new RoleList(userManager.getRoles());
    }
}
