package com.nancho313.loqui.users.test.application.commandhandler.user;

import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.AddContactToUserCommand;
import com.nancho313.loqui.users.application.command.user.handler.AddContactToUserCommandHandler;
import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.ContactStatus;
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
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddContactToUserCommandHandlerTest {

  private EventResolverFactorySpy eventResolverFactorySpy;

  private UserRepositorySpy userRepositorySpy;

  private AddContactToUserCommandHandler sut;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    eventResolverFactorySpy = new EventResolverFactorySpy();
    userRepositorySpy = new UserRepositorySpy();
    sut = new AddContactToUserCommandHandler(validator, eventResolverFactorySpy, userRepositorySpy);
  }

  @Test
  void handleOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user1);
    userRepositorySpy.save(user2);

    var command = new AddContactToUserCommand(userId, contactId);

    // Act
    var result = sut.handle(command);

    // Assert
    assertThat(result).isNotNull().isEqualTo(EmptyCommandResponse.VALUE);
    var actualUser = userRepositorySpy.findById(UserId.of(userId));

    assertThat(actualUser).isPresent();
    assertThat(actualUser.get().getContacts()).isNotNull().hasSize(1);

    var addedContact = actualUser.get().getContacts().getFirst();
    assertThat(addedContact.id().id()).isEqualTo(contactId);
    assertThat(addedContact.status()).isEqualTo(ContactStatus.AVAILABLE);

    var sentEvents = eventResolverFactorySpy.getProcessedEvents();
    assertThat(sentEvents).isNotNull().hasSize(1).allMatch(event -> event instanceof User.AddedContactEvent);
    var sentEvent = (User.AddedContactEvent) sentEvents.getFirst();
    assertThat(sentEvent.getUserId().id()).isEqualTo(userId);
    assertThat(sentEvent.getContactId().id()).isEqualTo(contactId);
    assertThat(sentEvent.getEventDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
  }

  @Test
  void handleThrowsExceptionDueUserDoesNotExist() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();

    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user2);

    var command = new AddContactToUserCommand(userId, contactId);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("The user with id " + userId + " does not exist.");
  }

  @Test
  void handleThrowsExceptionDueContactDoesNotExist() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    userRepositorySpy.save(user1);

    var command = new AddContactToUserCommand(userId, contactId);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("The user with id " + contactId + " does not exist.");
  }

  @Test
  void handleThrowsExceptionDueUserHasAlreadyAddedTheContact() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user1);
    userRepositorySpy.save(user2);

    var command = new AddContactToUserCommand(userId, contactId);
    sut.handle(command);

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("The user with id " + contactId + " is already a contact of " + userId);
  }

  @Test
  void handleThrowsExceptionDueIsUsedTheSameUserForAddingTheContact() {

    // Arrange
    var userId = UUID.randomUUID().toString();

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    userRepositorySpy.save(user1);

    var command = new AddContactToUserCommand(userId, userId);

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("The new contact has the same id as the current user. Id -> " + userId);
  }

  @Test
  void handleThrowsExceptionWhenProcessingNullCommand() {

    // Arrange
    AddContactToUserCommand command = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The command to process cannot be null.");
  }

  @MethodSource("getInvalidData")
  @ParameterizedTest
  void handleInvalidData(String userId, String contactId, String expectedErrorMessage) {

    // Arrange
    var command = new AddContactToUserCommand(userId, contactId);

    // Act & Assert
    var exception = assertThrows(InvalidCommandDataException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }

  private static Stream<Arguments> getInvalidData() {

    String userId = "123";
    String contactId = "321";

    String expectedErrorMessage1 = "The user id cannot be empty.";
    String expectedErrorMessage2 = "The contact id cannot be empty.";

    return Stream.of(
        Arguments.of(null, contactId, expectedErrorMessage1),
        Arguments.of("", contactId, expectedErrorMessage1),
        Arguments.of("  ", contactId, expectedErrorMessage1),
        Arguments.of(userId, null, expectedErrorMessage2),
        Arguments.of(userId, "", expectedErrorMessage2),
        Arguments.of(userId, "  ", expectedErrorMessage2)
    );
  }
}
