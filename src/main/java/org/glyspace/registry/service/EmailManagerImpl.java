package org.glyspace.registry.service;

import java.util.Iterator;
import java.util.List;

import org.glyspace.registry.dao.SettingsDAO;
import org.glyspace.registry.dao.UserDAO;
import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.utils.RandomPasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailManagerImpl implements EmailManager {
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	SettingsDAO settingsDAO;
	
	private MailSender mailSender;
    private SimpleMailMessage templateMessage;
    private String username;
    private String password;
    
    public void init () {
    	if (username == null && password == null) { // load them from db the first time
    	
    		SettingEntity userNameSetting = settingsDAO.getSetting("server.email");
    		SettingEntity passwordSetting = settingsDAO.getSetting("server.email.password");
    		if (userNameSetting != null && passwordSetting != null) {
    			username = userNameSetting.getValue();
    			password = passwordSetting.getValue();
    		
    			((JavaMailSenderImpl)this.mailSender).setPassword(password);
    			((JavaMailSenderImpl)this.mailSender).setUsername(username);
    			this.templateMessage.setFrom(username);
    		} else {
    			throw new RuntimeException("Internal Server Error: email server settings are not in the database");
    		}
    	}
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    @Override
    @Transactional
    public void sendPasswordReminder(UserEntity user) {
    	init(); // if username/password have not been initialized, this will get them from DB
        
    	// Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(user.getEmail());
        
        // this criteria should match with the PasswordValidator's criteria
        // password should have minimum of 5, max of 12 characters, 1 numeric, 1 special character, 1 capital letter and 1 lowercase letter at least
        char[] pswd = RandomPasswordGenerator.generatePswd(5, 12, 1, 1, 1);
        String newPassword = new String(pswd);
        // encrypt the password
 		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
 		String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userDAO.updateUser(user);
        msg.setText(
            "Dear " + user.getUserName()
                + ", \n\nYour Glycan Registry password is reset. This is your temporary password: \n\n" + new String(pswd) 
            	+ "\n\nPlease change it as soon as possible. \n\nThe Glycan Registry");
        this.mailSender.send(msg);
    }

	@Override
	@Transactional
	public void sendUserQuotaAlert(List<UserEntity> moderators, String user) {
		init();

		// Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setSubject("User Quota Alert");
        
        for (Iterator iterator = moderators.iterator(); iterator.hasNext();) {
			UserEntity userEntity = (UserEntity) iterator.next();
			msg.setTo(userEntity.getEmail());
	        
	        msg.setText(
	                "Dear Moderator,\n  " + user + "'s quota has exceeded. Please check the submitted structures.");
	        this.mailSender.send(msg);
		}  
		
	}

	@Override
    public void sendUserRegistered(List<UserEntity> moderators, String user) {
		init();
		
		// Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setSubject("New User");
        
        for (Iterator iterator = moderators.iterator(); iterator.hasNext();) {
			UserEntity userEntity = (UserEntity) iterator.next();
			msg.setTo(userEntity.getEmail());
	        
	        msg.setText(
	                "Dear Moderator,\n  A New user: " + user + " has just registered, please validate! http://glytoucan.org/");
	        this.mailSender.send(msg);
		}  
    }

}