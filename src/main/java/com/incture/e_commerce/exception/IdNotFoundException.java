package com.incture.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * This exception is thrown when a requested resource cannot be found.
 * Occurs when user is not found, product is not found, cart is not found or cart item is not found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class IdNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IdNotFoundException(String message){
		super(message);
	}
}
