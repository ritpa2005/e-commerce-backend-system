package com.incture.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * This exception is thrown when a user already exists in the system.
 * May occur during user registration and user update, when user provides an email that already exists.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyPresentException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UserAlreadyPresentException(String message){
		super(message);
	}
	
}
