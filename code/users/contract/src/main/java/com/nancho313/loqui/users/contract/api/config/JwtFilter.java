package com.nancho313.loqui.users.contract.api.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {
  
  private final SecretKey jwtKey;
  
  public JwtFilter(String jwtKey) {
    this.jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKey));
  }
  
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    
    try {
      
      var authorizationValue = Optional.ofNullable(request.getHeader("Authorization")).orElseThrow(() -> new AccessDeniedException("Access denied."));
      var token = getTokenFromHeader(authorizationValue);
      var claims = Jwts.parser().verifyWith(jwtKey).build().parseSignedClaims(token);
      var authUser = new AuthUser(claims.getPayload().getSubject(), claims.getPayload().get("lqu", String.class));
      request.setAttribute("authUser", authUser);
      filterChain.doFilter(request, response);
      
    } catch (JwtException e) {
      
      throw new AccessDeniedException("Access denied");
    }
  }
  
  private String getTokenFromHeader(String authorization) {
    return authorization.replace("Bearer ", "");
  }
}
