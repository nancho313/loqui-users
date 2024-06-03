package com.nancho313.loqui.users.test.application.queryhandler.user;

import com.nancho313.loqui.users.application.query.user.handler.SearchUserQueryHandler;
import com.nancho313.loqui.users.application.query.user.query.SearchUserQuery;
import com.nancho313.loqui.users.projection.model.UserModel;
import com.nancho313.loqui.users.test.application.util.UserDataSourceSpy;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchUserQueryHandlerTest {

  private UserDataSourceSpy userDataSourceSpy;

  private SearchUserQueryHandler sut;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    userDataSourceSpy = new UserDataSourceSpy();
    sut = new SearchUserQueryHandler(validator, userDataSourceSpy);
  }

  @Test
  void executeFindByUsernameOk() {

    // Arrange
    var username = "foo";
    String email = null;
    var query = new SearchUserQuery(username, email);

    UserModel userToSearch = new UserModel(UUID.randomUUID().toString(), username, "fee@fee.com");
    List<UserModel> newData = List.of(userToSearch);
    userDataSourceSpy.initDataSource(newData, null);

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.users()).isNotNull().hasSize(1);
    var user = result.users().getFirst();
    assertThat(user.id()).isEqualTo(userToSearch.id());
    assertThat(user.username()).isEqualTo(userToSearch.username());
    assertThat(user.email()).isEqualTo(userToSearch.email());
  }

  @Test
  void executeFindByEmailOk() {

    // Arrange
    String username = null;
    var email = "foo@foo.com";
    var query = new SearchUserQuery(username, email);

    UserModel userToSearch = new UserModel(UUID.randomUUID().toString(), "fee", email);
    List<UserModel> newData = List.of(userToSearch);
    userDataSourceSpy.initDataSource(newData, null);

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.users()).isNotNull().hasSize(1);
    var user = result.users().getFirst();
    assertThat(user.id()).isEqualTo(userToSearch.id());
    assertThat(user.username()).isEqualTo(userToSearch.username());
    assertThat(user.email()).isEqualTo(userToSearch.email());
  }

  @Test
  void executeFindByEmailAndUsernameOk() {

    // Arrange
    var username = "foo";
    var email = "foo@foo.com";
    var query = new SearchUserQuery(username, email);

    UserModel userToSearch1 = new UserModel(UUID.randomUUID().toString(), username, "fee@fee.com");
    UserModel userToSearch2 = new UserModel(UUID.randomUUID().toString(), "fee", email);
    List<UserModel> newData = List.of(userToSearch1, userToSearch2);
    userDataSourceSpy.initDataSource(newData, null);

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.users())
        .isNotNull()
        .hasSize(2)
        .anyMatch(user -> user.email().equals(userToSearch1.email()) &&
            user.username().equals(userToSearch1.username()) &&
            user.id().equals(userToSearch1.id()))
        .anyMatch(user -> user.email().equals(userToSearch2.email()) &&
            user.username().equals(userToSearch2.username()) &&
            user.id().equals(userToSearch2.id()));
  }

  @Test
  void executeThrowsExceptionWhenUsingNullQuery() {

    // Arrange
    SearchUserQuery query = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.execute(query));
    assertThat(exception.getMessage()).isEqualTo("The query to execute cannot be null.");
  }
}
