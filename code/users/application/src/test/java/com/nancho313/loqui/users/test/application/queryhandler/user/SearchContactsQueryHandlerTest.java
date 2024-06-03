package com.nancho313.loqui.users.test.application.queryhandler.user;

import com.nancho313.loqui.users.application.exception.InvalidInputDataException;
import com.nancho313.loqui.users.application.query.user.handler.SearchContactsQueryHandler;
import com.nancho313.loqui.users.application.query.user.query.SearchContactsQuery;
import com.nancho313.loqui.users.projection.model.ContactModel;
import com.nancho313.loqui.users.test.application.util.UserDataSourceSpy;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchContactsQueryHandlerTest {

  private UserDataSourceSpy userDataSourceSpy;

  private SearchContactsQueryHandler sut;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    userDataSourceSpy = new UserDataSourceSpy();
    sut = new SearchContactsQueryHandler(validator, userDataSourceSpy);
  }

  @Test
  void executeOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var query = new SearchContactsQuery(userId);
    var contact = new ContactModel(UUID.randomUUID().toString(), "foo", "foo@foo.com", "ACCEPTED");
    Map<String, List<ContactModel>> contactData = new HashMap<>();
    contactData.put(userId, List.of(contact));
    userDataSourceSpy.initDataSource(null, contactData);

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.contacts()).isNotNull().hasSize(1);
    var contactToValidate = result.contacts().getFirst();
    assertThat(contactToValidate.status()).isEqualTo(contact.status());
    assertThat(contactToValidate.user().id()).isEqualTo(contact.id());
    assertThat(contactToValidate.user().email()).isEqualTo(contact.email());
    assertThat(contactToValidate.user().username()).isEqualTo(contact.username());
  }

  @Test
  void executeReturnsEmptyData() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var query = new SearchContactsQuery(userId);

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.contacts()).isNotNull().isEmpty();
  }

  @Test
  void executeThrowsExceptionWhenUsingNullQuery() {

    // Arrange
    SearchContactsQuery query = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.execute(query));
    assertThat(exception.getMessage()).isEqualTo("The query to execute cannot be null.");
  }

  @MethodSource("getInvalidData")
  @ParameterizedTest
  void executeWithInvalidData(String userId, String expectedErrorMessage) {

    // Arrange
    var query = new SearchContactsQuery(userId);

    // Act & Assert
    var exception = assertThrows(InvalidInputDataException.class, () -> sut.execute(query));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }

  private static Stream<Arguments> getInvalidData() {

    String nullString = null;
    String expectedErrorMessage1 = "The user id cannot be empty.";

    return Stream.of(
        Arguments.of(nullString, expectedErrorMessage1),
        Arguments.of("", expectedErrorMessage1),
        Arguments.of("  ", expectedErrorMessage1)
    );
  }
}
