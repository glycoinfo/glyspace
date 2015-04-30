package org.glyspace.registry.service;

import java.util.List;

import org.glyspace.registry.database.UserEntity;

public interface EmailManager {
	void sendPasswordReminder (UserEntity user);
	void sendUserQuotaAlert (List<UserEntity> moderators, String userName);
	void sendUserRegistered (List<UserEntity> moderators, String userName);
}