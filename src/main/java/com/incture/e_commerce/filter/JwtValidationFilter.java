package com.incture.e_commerce.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.incture.e_commerce.model.JwtAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends OncePerRequestFilter{

	private final AuthenticationManager authenticationManager;
	
	public JwtValidationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = extractJwtTokenFromRequest(request);

		if (token != null) {
			
			JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
			
			Authentication authResult = authenticationManager.authenticate(authenticationToken);

			if(authResult != null && authResult.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authResult);
			}
		}
		filterChain.doFilter(request, response);
	}
	
	private String extractJwtTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
