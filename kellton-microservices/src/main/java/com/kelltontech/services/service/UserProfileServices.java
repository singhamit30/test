package com.kelltontech.services.service;

import java.util.List;

import com.kelltontech.services.domain.UserProfile;

public interface UserProfileServices {

	UserProfile findById(Long id);
    
	List<UserProfile> findByUserName(String userName);
 
    void saveUser(UserProfile userProfile);
 
    void updateUser(UserProfile userProfile);
 
    void deleteUserById(Long id);
 
    void deleteAllUsers();
 
    List<UserProfile> findAllUsers();
 
    Boolean isUserExist(UserProfile userProfile);

	UserProfile save(UserProfile userProfile);
	
	void saveAndFlush(UserProfile userProfile);
}
