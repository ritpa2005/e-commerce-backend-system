package com.incture.e_commerce.model;

/*
 * Request model used for user authentication.
 */
public class LoginRequest {
	
	// Represents the information required to authenticate a user
	private String username;
	private String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
