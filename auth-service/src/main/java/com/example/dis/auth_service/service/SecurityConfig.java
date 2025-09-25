package com.example.dis.auth_service.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	  @Bean
	  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    return http
	      .csrf(csrf -> csrf.disable())
	      .httpBasic(b -> b.disable())
	      .formLogin(f -> f.disable())
	      .authorizeHttpRequests(a -> a
	    	.requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()       
	        .requestMatchers("/auth/**").permitAll()   // <-- dozvoli /auth/*
	        .anyRequest().authenticated()
	      )
	      .build();
	  }
}
