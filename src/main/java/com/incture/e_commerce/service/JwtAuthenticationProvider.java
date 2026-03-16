package com.incture.e_commerce.service;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.model.JwtAuthenticationToken;
import com.incture.e_commerce.utils.JwtUtil;


public class JwtAuthenticationProvider implements AuthenticationProvider {

	private JwtUtil jwtUtil;
	private UserService userService;
	
	public JwtAuthenticationProvider(JwtUtil jwtUtil, UserDetailsService userService) {
		super();
		this.jwtUtil = jwtUtil;
		this.userService = (UserService) userService;
	}
	

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String token = ((JwtAuthenticationToken) authentication).getToken();
		String username = jwtUtil.validateAndExtractUsername(token);
		if(username==null) {
			throw new BadCredentialsException("Invalid JWT Token");
		}
		
		User userDetails = userService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
