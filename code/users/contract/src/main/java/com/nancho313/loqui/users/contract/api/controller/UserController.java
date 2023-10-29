package com.nancho313.loqui.users.contract.api.controller;

import com.nancho313.loqui.users.application.query.QueryHandler;
import com.nancho313.loqui.users.application.query.user.query.SearchUserQuery;
import com.nancho313.loqui.users.application.query.user.response.SearchUserQueryResponse;
import com.nancho313.loqui.users.contract.api.dto.AddNewContactApiRequest;
import com.nancho313.loqui.users.contract.api.dto.SearchUserApiResponse;
import com.nancho313.loqui.users.contract.api.dto.UserApiDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
  
  private static final String USERNAME_QUERY_PARAM = "username";
  
  private static final String EMAIL_QUERY_PARAM = "email";
  
  private final QueryHandler<SearchUserQuery, SearchUserQueryResponse> queryHandler;
  
  @GetMapping
  public ResponseEntity<SearchUserApiResponse> searchUsers(@RequestParam Map<String, String> queryParams) {
    
    var query = new SearchUserQuery(queryParams.get(USERNAME_QUERY_PARAM), queryParams.get(EMAIL_QUERY_PARAM));
    var result = queryHandler.execute(query);
    return ResponseEntity.ok(toApiResponse(result));
  }
  
  private SearchUserApiResponse toApiResponse(SearchUserQueryResponse result) {
    
    var users = result.users().stream().map(user -> new UserApiDto(user.id(), user.username(), user.email())).toList();
    return new SearchUserApiResponse(users);
  }
}