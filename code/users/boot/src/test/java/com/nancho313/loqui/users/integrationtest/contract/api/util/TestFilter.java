package com.nancho313.loqui.users.integrationtest.contract.api.util;

import com.nancho313.loqui.users.contract.api.config.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TestFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    var userId = request.getHeader("test_user_id");
    var username = request.getHeader("test_username");
    var authUser = new AuthUser(userId, username);
    request.setAttribute("authUser", authUser);
    filterChain.doFilter(request, response);
  }
}
