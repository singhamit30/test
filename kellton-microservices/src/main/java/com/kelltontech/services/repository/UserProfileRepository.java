package com.kelltontech.services.repository;

import com.kelltontech.services.domain.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the UserProfile entity.
 */

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	
	List<UserProfile> findByuserNameContaining(String userName);
}
