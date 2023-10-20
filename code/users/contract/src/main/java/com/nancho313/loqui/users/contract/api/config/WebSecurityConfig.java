package com.nancho313.loqui.users.contract.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, @Value("${loqui.auth.jwt.key}") String jwtKey) throws Exception {
    
    http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .addFilterBefore(new JwtFilter(jwtKey), AuthorizationFilter.class)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }
}
