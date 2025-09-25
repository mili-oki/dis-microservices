package com.example.dis.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(ex -> ex
        // actuator samog gateway-a
        .pathMatchers("/actuator/**",   "/**/actuator/**").permitAll()
        .pathMatchers(
          "/auth-service/actuator/**",
          "/catalog-service/actuator/**",
          "/orders-service/actuator/**",
          "/payments-service/actuator/**",
          "/notifications-service/actuator/**",
          "/auth-service/api/auth/**"
        ).permitAll()
        // javni auth endpointi
        .pathMatchers("/auth-service/**", "/auth/**").permitAll()
        // ostalo zahteva auth
        .anyExchange().authenticated()
      )
      .build();
  }
}
