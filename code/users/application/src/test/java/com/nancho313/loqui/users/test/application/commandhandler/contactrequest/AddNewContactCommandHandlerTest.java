package com.nancho313.loqui.users.test.application.commandhandler.contactrequest;

import com.nancho313.loqui.users.application.command.contactrequest.command.AddNewContactCommand;
import com.nancho313.loqui.users.application.command.contactrequest.handler.AddNewContactCommandHandler;
import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.test.application.util.ContactRequestRepositorySpy;
import com.nancho313.loqui.users.test.application.util.EventResolverFactorySpy;
import com.nancho313.loqui.users.test.application.util.UserRepositorySpy;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddNewContactCommandHandlerTest {

  private EventResolverFactorySpy eventResolverFactorySpy;
  
  private UserRepositorySpy userRepositorySpy;
  
  private ContactRequestRepositorySpy contactRequestRepositorySpy;

  private AddNewContactCommandHandler sut;
  
  @BeforeEach
  void setup() {
    
    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    eventResolverFactorySpy = new EventResolverFactorySpy();
    userRepositorySpy = new UserRepositorySpy();
    contactRequestRepositorySpy = new ContactRequestRepositorySpy();
    sut = new AddNewContactCommandHandler(validator, eventResolverFactorySpy, userRepositorySpy,
            contactRequestRepositorySpy, () -> UUID.randomUUID().toString());
  }
  
  @Test
  void handleOk() {
    
    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();
    var initialMessage = "This is the initial message";
    
    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user1);
    userRepositorySpy.save(user2);
    
    var command = new AddNewContactCommand(userId, contactId, initialMessage);
    
    // Act
    sut.handle(command);
    
    // Assert
    var allContactRequests = contactRequestRepositorySpy.findAll();
    assertThat(allContactRequests).hasSize(1)
            .allMatch(contactRequest -> contactRequest.getRequesterUser().equals(user1.getId())
                    && contactRequest.getRequestedUser().equals(user2.getId())
                    && contactRequest.getStatus().equals(ContactRequestStatus.PENDING));
  }

  @Test
  void handleThrowsExceptionDueContactIdDoesNotExist() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();
    var initialMessage = "This is the initial message";

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");

    userRepositorySpy.save(user1);

    var command = new AddNewContactCommand(userId, contactId, initialMessage);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The user with the id "+contactId+" does not exist.");
  }

  @Test
  void handleThrowsExceptionDueTheresIsAPendingContactRequest() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();
    var initialMessage = "This is the initial message";

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user1);
    userRepositorySpy.save(user2);

    var contactRequest = new ContactRequest(ContactRequestId.of(UUID.randomUUID().toString()),
            UserId.of(userId), UserId.of(contactId), CurrentDate.now(),
            ContactRequestStatus.PENDING, "This is the initial message.");

    contactRequestRepositorySpy.save(contactRequest);

    var command = new AddNewContactCommand(userId, contactId, initialMessage);

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, ()-> sut.handle(command));
    assertThat(exception.getMessage()).contains("There is already a pending contact request with the given users");
  }

  @Test
  void handleThrowsExceptionDueUserIdDoesNotExist() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();
    var initialMessage = "This is the initial message";
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");

    userRepositorySpy.save(user2);

    var command = new AddNewContactCommand(userId, contactId, initialMessage);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The user with the id "+userId+" does not exist.");
  }

  @Test
  void handleThrowsExceptionWhenProcessingNullCommand() {

    // Arrange
    AddNewContactCommand command = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The command to process cannot be null.");
  }

  @MethodSource("getInvalidData")
  @ParameterizedTest
  void handleInvalidData(String userId, String contactId, String initialMessage, String expectedErrorMessage) {

    // Arrange
    var command = new AddNewContactCommand(userId, contactId, initialMessage);

    // Act & Assert
    var exception = assertThrows(InvalidCommandDataException.class, ()-> sut.handle(command));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }

  private static Stream<Arguments> getInvalidData() {

    String userId = "123";
    String contactId = "321";
    String initialMessage = "This is the initial message";

    String expectedErrorMessage1 = "The user id cannot be empty.";
    String expectedErrorMessage2 = "The contact id cannot be empty.";
    String expectedErrorMessage3 = "The initial message cannot be empty.";

    return Stream.of(
            Arguments.of(null, contactId, initialMessage, expectedErrorMessage1),
            Arguments.of("", contactId, initialMessage, expectedErrorMessage1),
            Arguments.of("  ", contactId, initialMessage, expectedErrorMessage1),
            Arguments.of(userId, null, initialMessage, expectedErrorMessage2),
            Arguments.of(userId, "", initialMessage, expectedErrorMessage2),
            Arguments.of(userId, "  ", initialMessage, expectedErrorMessage2),
            Arguments.of(userId, contactId, null, expectedErrorMessage3),
            Arguments.of(userId, contactId, "", expectedErrorMessage3),
            Arguments.of(userId, contactId, "  ", expectedErrorMessage3)
    );
  }
}
