package com.incture.e_commerce.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/* 
 * Utility class responsible for generating and validating JSON Web Tokens (JWT).
 */
@Component
public class JwtUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
	
	// Secret key used for signing JWT tokens.
	private static final String SECRET_KEY = "secure-key-super-secret-12345678-hmac-super-super";
	private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	
	/**
     * Generates a signed JWT token for a given username.
     *
     * @param username is the username(email) of the authenticated user
     * @param expiryMinutes is the token expiration time in minutes
     * @return JWT token
     */
	public String generateToken(String username, long expiryMinutes) {
		
		logger.info("Generating JWT token for user: {}", username);
		
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiryMinutes*60*1000))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
		
	}
	
	/**
     * Validates a JWT token and extracts the username(email).
     * 
     * @param token receives the JWT token to validate
     * @return username if token is valid, otherwise null
     */
	public String validateAndExtractUsername(String token) {
		try {
			logger.info("Validating JWT token");
			
			String username = Jwts.parser()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
			
			logger.debug("JWT token validated successfully for user: {}", username);
			
			return username;
		}
		catch(JwtException e) {
			logger.error("JWT validation failed: {}", e.getMessage());
			
			return null;
		}
	}
}





