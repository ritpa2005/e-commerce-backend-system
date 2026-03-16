package com.incture.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * This exception is thrown when the requested cart has no cart items.
 * Occurs while attempting to checkout with empty cart.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyCartException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public EmptyCartException(String message) {
		super(message);
	}
}
