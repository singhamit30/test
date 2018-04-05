package com.kelltontech.services.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kelltontech.services.domain.UserProfile;
import com.kelltontech.services.repository.UserProfileRepository;


@Service("userProfileServices")
@Transactional
public class UserProfileServicesImpl implements UserProfileServices {
	
	private final UserProfileRepository userProfileRepository;
	
	public UserProfileServicesImpl(UserProfileRepository userProfileRepository) {
		this.userProfileRepository = userProfileRepository;
	}

	@Override
	public UserProfile findById(Long id) {
		return userProfileRepository.findOne(id);
	}

	@Override
	public List<UserProfile> findByUserName(String userName) {
		return userProfileRepository.findByuserNameContaining(userName);
	}

	@Override
	public void saveUser(UserProfile userProfile) {
		userProfileRepository.save(userProfile);
	}

	@Override
	public void updateUser(UserProfile userProfile) {
		userProfileRepository.save(userProfile);
	}

	@Override
	public void deleteUserById(Long id) {
		userProfileRepository.delete(id);	
	}

	@Override
	public void deleteAllUsers() {
		userProfileRepository.deleteAll();
	}

	@Override
	public List<UserProfile> findAllUsers() {
		return userProfileRepository.findAll();
	}

	@Override
	public Boolean isUserExist(UserProfile userProfile) {
		return findByUserName(userProfile.getUserName()) != null;
	}

	@Override
	public UserProfile save(UserProfile userProfile) {
		return userProfileRepository.save(userProfile);
	}

	@Override
	public void saveAndFlush(UserProfile userProfile) {
		userProfileRepository.saveAndFlush(userProfile);	
	}

}