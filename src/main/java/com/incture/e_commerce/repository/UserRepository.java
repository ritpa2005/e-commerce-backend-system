package com.incture.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.e_commerce.entity.User;

/*
 * Repository interface for performing CRUD operations on User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	// Used during login authentication
	Optional<User> findByEmail(String email);
}
