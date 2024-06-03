package com.nancho313.loqui.users.test.application.commandhandler.user;

import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.CreateUserCommand;
import com.nancho313.loqui.users.application.command.user.handler.CreateUserCommandHandler;
import com.nancho313.loqui.users.application.exception.InvalidInputDataException;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.test.application.util.EventResolverFactorySpy;
import com.nancho313.loqui.users.test.application.util.UserRepositorySpy;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateUserCommandHandlerTest {

  private EventResolverFactorySpy eventResolverFactorySpy;

  private UserRepositorySpy userRepositorySpy;

  private CreateUserCommandHandler sut;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    eventResolverFactorySpy = new EventResolverFactorySpy();
    userRepositorySpy = new UserRepositorySpy();
    sut = new CreateUserCommandHandler(validator, eventResolverFactorySpy, userRepositorySpy);
  }

  @Test
  void handleOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var email = "foo@foo.com";
    var command = new CreateUserCommand(userId, username, email);

    // Act
    var result = sut.handle(command);

    // Assert
    assertThat(result).isNotNull().isEqualTo(EmptyCommandResponse.VALUE);

    var optionalUser = userRepositorySpy.findById(UserId.of(userId));
    assertThat(optionalUser).isPresent();
    var user = optionalUser.get();
    assertThat(user.getId().id()).isEqualTo(userId);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getContacts()).isEmpty();
    assertThat(user.getCurrentDate().creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
    assertThat(user.getCurrentDate().lastUpdatedDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));

    var sentEvents = eventResolverFactorySpy.getProcessedEvents();
    assertThat(sentEvents).isNotNull().hasSize(1).allMatch(event -> event instanceof User.CreatedUserEvent);
    var sentEvent = (User.CreatedUserEvent) sentEvents.getFirst();
    assertThat(sentEvent.getId().id()).isEqualTo(userId);
    assertThat(sentEvent.getEmail()).isEqualTo(email);
    assertThat(sentEvent.getUsername()).isEqualTo(username);
    assertThat(sentEvent.getEventDate()).isCloseTo(LocalDateTime.now(), within(200, ChronoUnit.MILLIS));
  }

  @Test
  void handleThrowsExceptionDueUserAlreadyExists() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var email = "foo@foo.com";
    var command = new CreateUserCommand(userId, username, email);
    sut.handle(command);

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The user with id " + userId + " already exists.");
  }

  @Test
  void handleThrowsExceptionWhenProcessingNullCommand() {

    // Arrange
    CreateUserCommand command = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The command to process cannot be null.");
  }

  @MethodSource("getInvalidData")
  @ParameterizedTest
  void handleInvalidData(String userId, String username, String email, String expectedErrorMessage) {

    // Arrange
    var command = new CreateUserCommand(userId, username, email);

    // Act & Assert
    var exception = assertThrows(InvalidInputDataException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }

  private static Stream<Arguments> getInvalidData() {

    String userId = "123";
    String username = "foo";
    String email = "foo@foo.com";

    String expectedErrorMessage1 = "The user id cannot be empty.";
    String expectedErrorMessage2 = "The username cannot be empty.";
    String expectedErrorMessage3 = "The email cannot be empty.";

    return Stream.of(
        Arguments.of(null, username, email, expectedErrorMessage1),
        Arguments.of("", username, email, expectedErrorMessage1),
        Arguments.of("  ", username, email, expectedErrorMessage1),
        Arguments.of(userId, null, email, expectedErrorMessage2),
        Arguments.of(userId, "", email, expectedErrorMessage2),
        Arguments.of(userId, "  ", email, expectedErrorMessage2),
        Arguments.of(userId, username, null, expectedErrorMessage3),
        Arguments.of(userId, username, "", expectedErrorMessage3),
        Arguments.of(userId, username, "  ", expectedErrorMessage3)
    );
  }
}
