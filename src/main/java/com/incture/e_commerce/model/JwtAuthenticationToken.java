package com.incture.e_commerce.model;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/*
 * Custom authentication token used for JWT-based authentication.
 * Initiated with unauthenticated token. After validation, it becomes authenticated.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;
	
	// JWT token received from request header
	private final String token;

	// Creates unauthenticated token
	public JwtAuthenticationToken(String token) {
		super(Collections.emptyList());
		this.token = token;
		setAuthenticated(false);
	}
	
	// Returns JWT token
	public String getToken() {
		return token;
	}

	// Returns credentials, i.e. the JWT token in this case
	@Override
	public Object getCredentials() {
		return token;
	}

	// Returns the authenticated principal. For unauthenticated tokens this is null.
	@Override
	public Object getPrincipal() {
		
		return null;
	}
	
}
