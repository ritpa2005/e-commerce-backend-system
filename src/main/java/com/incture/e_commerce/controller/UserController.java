package com.incture.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.incture.e_commerce.dto.UserDto;
import com.incture.e_commerce.dto.UserRequestDto;
import com.incture.e_commerce.dto.UserResponseDto;
import com.incture.e_commerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for User related APIs
 * Provides endpoints for user registration, retrieval, update and deletion.
 */
@RestController
@RequestMapping("/api/users/")
public class UserController {

	// Logger instance for logging API activity
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
    @Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
     * API to register a new user.
     * Password is encoded before storing in database.
     *
     * @param userDto - user data received from request body
     * @return UserResponseDto containing saved user details
     */
	@PostMapping("/register")
	public ResponseEntity<UserResponseDto> register(@RequestBody UserDto userDto) {
		
		// Encoding the password before saving
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		logger.debug("Password encoded successfully for user: {}", userDto.getEmail());
		
		// Saving user in database
		UserResponseDto userResponseDto = userService.addUser(userDto);
		
		logger.info("User registered successfully with ID: {}", userResponseDto.getId());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
	}
	
	
	/**
     * API to fetch user details by user ID
     *
     * @param id - user ID
     * @return UserResponseDto containing user details
     */
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> getUser(@PathVariable long id){
		
		// Fetching user from the database
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(id);
		
		UserResponseDto userResponseDto = userService.getUserById(userRequestDto);
		
		logger.info("User fetched successfully for ID: {}", id);
		
		return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
	}
	
	
	/**
     * API to update user details
     *
     * @param id - user ID
     * @param userDto - updated user details
     * @return updated UserResponseDto
     */
	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable long id, @RequestBody UserDto userDto){
		
		// Updating user in the database
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(id);
		
		UserResponseDto userResponseDto = userService.updateUser(userRequestDto, userDto);
		
		logger.info("User updated successfully with ID: {}", id);
		
		return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
	}
	
	
	/**
     * API to delete a user by ID
     *
     * @param id - user ID
     * @return success message
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable long id){
		
		// Fetching user from the database
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(id);
		
		userService.deleteUser(userRequestDto);
		
		logger.warn("User deleted successfully with ID: {}", id);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully.");
	}
	
}
