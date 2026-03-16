package com.incture.e_commerce.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuration class for application service utilities.
 * Defines a ModelMapper used for mapping between entities and DTO.
 * Defines a JavaMailSender used for notifying user on checkout.
 */
@Configuration
public class ServiceConfig {
	
	/**
     * Creates and configures a ModelMapper bean.
     * The mapper should skip null values during mapping, so existing fields are not overwritten.
     * @return configured ModelMapper instance
     */
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
	    modelMapper.getConfiguration()
	            .setSkipNullEnabled(true);

	    return modelMapper;
	}
	
	@Bean
	public JavaMailSender javaMailSender() {
		return new JavaMailSenderImpl();
	}
	
}
