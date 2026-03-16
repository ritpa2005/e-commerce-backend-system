package com.incture.e_commerce.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.incture.e_commerce.filter.JwtAuthenticationFilter;
import com.incture.e_commerce.filter.JwtValidationFilter;
import com.incture.e_commerce.service.JwtAuthenticationProvider;
import com.incture.e_commerce.utils.JwtUtil;

/**
 * Security configuration class for the application.
 * Configures authentication providers, JWT filters, password encoding, and authorization rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserDetailsService userDetailsService;

	/**
     * Authentication provider for validating JWT tokens and extracting user details.
     */
	@Bean
	public JwtAuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider(jwtUtil, userDetailsService);
	}
	
	/*
	 * Authentication provider used for login using username and password.
	 */
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	/**
     * Password encoder bean to be used by DaoAuthenticationProvider to hash user passwords before storing them in the database.
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
     * Configures the Security filter chain.
     *
     * Disables CSRF
     * Defines authorization rules for endpoints
     * Adds the JWT authentication and validation filters
     */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, JwtUtil jwtUtil) throws Exception {
		
		JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil);
		JwtValidationFilter jwtValidationFilter = new JwtValidationFilter(authenticationManager);
		
		http
		.csrf(csrf -> csrf.disable())
		
		.authorizeHttpRequests(auth -> auth
				// public endpoints
				.requestMatchers("/api/users/register", "/api/users/login", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
				// admin-only endpoints
				.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/products/").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("ADMIN")
                // all other endpoints requiring authentication
				.anyRequest().authenticated())
		
		// JWT filters added to security filter chain
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterAfter(jwtValidationFilter, JwtAuthenticationFilter.class)
		
		// configure stateless session
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); 
		
		return http.build(); 
	}
	
	/**
     * Authentication manager responsible for delegating authentication requests to configured providers.
     * Includes:
     * 1. DAO authentication provider (login and token generation)
     * 2. JWT authentication provider (token validation)
     */
	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Arrays.asList(
				daoAuthenticationProvider(), 
				jwtAuthenticationProvider())
				);
	}

}
