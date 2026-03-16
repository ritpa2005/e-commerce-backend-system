package com.incture.e_commerce.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.incture.e_commerce.dto.UserDto;
import com.incture.e_commerce.dto.UserRequestDto;
import com.incture.e_commerce.dto.UserResponseDto;
import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.exception.UserAlreadyPresentException;
import com.incture.e_commerce.repository.UserRepository;

/**
 * Service layer responsible for handling user-related business logic.
 * Implements UserDetailsService to integrate with Spring Security authentication.
 */
@Service
public class UserService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
     * Registers a new user.
     * Checks if a user with the given email already exists.
     *
     * @param userDto user details from request
     * @return saved user response
     */
	public UserResponseDto addUser(UserDto userDto) {
		
		logger.info("Attempting to register user with email {}", userDto.getEmail());
		
		Optional<User> optional = userRepository.findByEmail(userDto.getEmail());
		if(optional.isPresent()) {
			logger.error("User registration failed. Email already exists: {}", userDto.getEmail());
			throw new UserAlreadyPresentException("Email already exists.");
		}
		
		User user = modelMapper.map(userDto, User.class);
		user.setRole("ROLE_CUSTOMER");
		
		logger.debug("Mapped UserDto to User entity for email: {}", userDto.getEmail());
		
		User savedUser = userRepository.save(user);
		
		logger.info("User successfully registered with ID: {}", savedUser.getId());
		
		UserResponseDto userResponseDto = modelMapper.map(savedUser, UserResponseDto.class);
		return userResponseDto;
		
	}
	
	
	/**
     * Fetch user details by ID.
     *
     * @param userRequestDto request containing user ID
     * @return user details
     */
	public UserResponseDto getUserById(UserRequestDto userRequestDto) {
		
		long id = userRequestDto.getUserId();
		
		logger.info("Fetching user with ID: {}", id);
		
		User user = userRepository.findById(id)
				.orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new IdNotFoundException("User with id = " + id + " not found.");
                });
		
		logger.debug("User entity retrieved for ID: {}", id);
		
		UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
		return userResponseDto;
		
	}
	
	
	/**
     * Updates an existing user's information.
     *
     * @param userRequestDto contains user ID
     * @param userDto updated user data
     * @return updated user response
     */
	public UserResponseDto updateUser(UserRequestDto userRequestDto, UserDto userDto) {
		
		long id = userRequestDto.getUserId();
		
		logger.info("Updating user with ID: {}", id);
		
		// Fetching user from database
		User user = userRepository.findById(id)
				.orElseThrow(() -> {
                    logger.error("Update failed. User not found with ID: {}", id);
                    return new IdNotFoundException("User with id = " + id + " not found.");
                });
		
		logger.debug("User retrieved from database for update. ID: {}", id);
		
		String newEmail = userDto.getEmail();
		
		// If email is provided in request and it is different from the user's email, checking if another user already owns this email.
		if(newEmail != null && !newEmail.equals(user.getEmail())) {
			
			logger.debug("Checking if email {} already exists in system", newEmail);
			
			Optional<User> optional = userRepository.findByEmail(newEmail);
			if(optional.isPresent()) {
				logger.error("User update failed. Email already exists: {}", newEmail);
				throw new UserAlreadyPresentException("Email already exists.");
			}
		}
		
		// Mapping updated fields from DTO to entity
		modelMapper.map(userDto, user);
		
		logger.debug("Mapped updated fields from UserDto to User entity for ID: {}", id);
		
		// Saving the updated information
		User savedUser = userRepository.save(user);
		
		logger.info("User successfully updated with ID: {}", id);
		
		UserResponseDto userResponseDto = modelMapper.map(savedUser, UserResponseDto.class);
		return userResponseDto;
		
	}
	
	
	/**
     * Deletes a user from the system.
     *
     * @param userRequestDto contains user ID
     */
	public void deleteUser(UserRequestDto userRequestDto) {
		long id = userRequestDto.getUserId();
		logger.warn("Deleting user with ID: {}", id);
		
		User user = userRepository.findById(id)
				.orElseThrow(() -> {
                    logger.error("Delete failed. User not found with ID: {}", id);
                    return new IdNotFoundException("User with id = " + id + " not found.");
                });
		
		userRepository.delete(user);
		
		logger.warn("User successfully deleted with ID: {}", id);
	
	}
	
	/**
     * Loads user by email for Spring Security authentication.
     *
     * @param email user's email
     * @return UserDetails object
     */
	@Override
	public User loadUserByUsername(String email) throws UsernameNotFoundException {
		
		logger.info("Authenticating user with email: {}", email);
		
		return userRepository.findByEmail(email)
				.orElseThrow(() -> {
                    logger.error("Authentication failed. User not found with email: {}", email);
                    return new UsernameNotFoundException("User Not Found.");
                });
		
	}
}




