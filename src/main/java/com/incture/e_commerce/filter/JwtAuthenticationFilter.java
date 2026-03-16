package com.incture.e_commerce.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incture.e_commerce.model.LoginRequest;
import com.incture.e_commerce.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if(!request.getServletPath().equals("/api/users/login")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
		
		Authentication authResult = authenticationManager.authenticate(authToken);
		if(authResult.isAuthenticated()) {
			String token = jwtUtil.generateToken(authResult.getName(), 15);
			
			response.setHeader("Authorization", "Bearer "+token);
		}
	}

}





