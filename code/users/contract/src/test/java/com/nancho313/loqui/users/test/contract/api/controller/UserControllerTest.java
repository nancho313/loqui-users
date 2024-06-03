package com.nancho313.loqui.users.test.contract.api.controller;

import com.nancho313.loqui.users.application.query.user.dto.UserResultDto;
import com.nancho313.loqui.users.application.query.user.query.SearchUserQuery;
import com.nancho313.loqui.users.application.query.user.response.SearchUserQueryResponse;
import com.nancho313.loqui.users.contract.api.controller.UserController;
import com.nancho313.loqui.users.test.contract.util.QueryHandlerTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {

  private QueryHandlerTestUtil<SearchUserQuery, SearchUserQueryResponse> queryHandlerSpy;

  private UserController sut;

  @BeforeEach
  void setup() {

    queryHandlerSpy = new QueryHandlerTestUtil<>();
    sut = new UserController(queryHandlerSpy);
  }

  @Test
  void searchUsersOk() {

    // Arrange
    var username = "foo";
    var email = "foo@foo.com";
    Map<String, String> params = Map.of("username", username, "email", email);

    var user = new UserResultDto(UUID.randomUUID().toString(), username, email);
    var users = List.of(user);
    var response = new SearchUserQueryResponse(users);
    queryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.searchUsers(params);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().users()).isNotNull().hasSize(1);

    var userToValidate = result.getBody().users().getFirst();
    assertThat(userToValidate.username()).isEqualTo(user.username());
    assertThat(userToValidate.id()).isEqualTo(user.id());
    assertThat(userToValidate.email()).isEqualTo(user.email());

    var queryToExecute = queryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.email()).isEqualTo(email);
    assertThat(queryToExecute.username()).isEqualTo(username);
  }

  @Test
  void searchUsersSendingOnlyUsernameOk() {

    // Arrange
    var username = "foo";
    Map<String, String> params = Map.of("username", username);

    var user = new UserResultDto(UUID.randomUUID().toString(), username, "foo@foo.com");
    var users = List.of(user);
    var response = new SearchUserQueryResponse(users);
    queryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.searchUsers(params);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().users()).isNotNull().hasSize(1);

    var userToValidate = result.getBody().users().getFirst();
    assertThat(userToValidate.username()).isEqualTo(user.username());
    assertThat(userToValidate.id()).isEqualTo(user.id());
    assertThat(userToValidate.email()).isEqualTo(user.email());

    var queryToExecute = queryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.email()).isNull();
    assertThat(queryToExecute.username()).isEqualTo(username);
  }

  @Test
  void searchUsersSendingOnlyEmailOk() {

    // Arrange
    var email = "foo@foo.com";
    Map<String, String> params = Map.of("email", email);

    var user = new UserResultDto(UUID.randomUUID().toString(), "foo", email);
    var users = List.of(user);
    var response = new SearchUserQueryResponse(users);
    queryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.searchUsers(params);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().users()).isNotNull().hasSize(1);

    var userToValidate = result.getBody().users().getFirst();
    assertThat(userToValidate.username()).isEqualTo(user.username());
    assertThat(userToValidate.id()).isEqualTo(user.id());
    assertThat(userToValidate.email()).isEqualTo(user.email());

    var queryToExecute = queryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.email()).isEqualTo(email);
    assertThat(queryToExecute.username()).isNull();
  }

  @Test
  void searchUsersReturnsEmptyData() {

    // Arrange
    var username = "foo";
    var email = "foo@foo.com";
    Map<String, String> params = Map.of("username", username, "email", email);

    var response = new SearchUserQueryResponse(List.of());
    queryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.searchUsers(params);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().users()).isNotNull().isEmpty();
  }
}