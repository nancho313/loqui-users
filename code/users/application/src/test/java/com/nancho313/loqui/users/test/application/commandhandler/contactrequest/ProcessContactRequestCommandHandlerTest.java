package com.nancho313.loqui.users.test.application.commandhandler.contactrequest;

import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.contactrequest.command.ProcessContactRequestCommand;
import com.nancho313.loqui.users.application.command.contactrequest.handler.ProcessContactRequestCommandHandler;
import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessContactRequestCommandHandlerTest {

  private ProcessContactRequestCommandHandler sut;

  private UserRepositorySpy userRepositorySpy;

  private ContactRequestRepositorySpy contactRequestRepositorySpy;

  private EventResolverFactorySpy eventResolverFactorySpy;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    eventResolverFactorySpy = new EventResolverFactorySpy();
    userRepositorySpy = new UserRepositorySpy();
    contactRequestRepositorySpy = new ContactRequestRepositorySpy();
    sut = new ProcessContactRequestCommandHandler(validator, eventResolverFactorySpy, contactRequestRepositorySpy, userRepositorySpy);
  }

  @Test
  void handleCommandAsAcceptedOk() {

    // Arrange
    var requesterUserId = UUID.randomUUID().toString();
    var requestedUserId = UUID.randomUUID().toString();
    var accepted = Boolean.TRUE;

    userRepositorySpy.save(User.createUser(UserId.of(requesterUserId), "foo1", "foo1@gmail.com"));
    userRepositorySpy.save(User.createUser(UserId.of(requestedUserId), "foo2", "foo2@gmail.com"));

    var contactRequest = ContactRequest.create(() -> UUID.randomUUID().toString(), UserId.of(requesterUserId), UserId.of(requestedUserId), "Greetings.");
    contactRequestRepositorySpy.save(contactRequest);

    var command = new ProcessContactRequestCommand(contactRequest.getContactRequestId().id(), requestedUserId, accepted);

    // Act
    var result = sut.handle(command);

    // Assert
    assertThat(result).isEqualTo(EmptyCommandResponse.VALUE);

    var acceptedContactRequest = contactRequestRepositorySpy.findById(contactRequest.getContactRequestId());
    assertThat(acceptedContactRequest).isNotNull().isPresent();
    assertThat(acceptedContactRequest.get().getStatus()).isEqualTo(ContactRequestStatus.ACCEPTED);

    var processedEvents = eventResolverFactorySpy.getProcessedEvents();
    assertThat(processedEvents)
            .isNotEmpty()
            .hasSize(1)
            .allMatch(value -> value instanceof ContactRequest.AcceptedContactRequestEvent);

    var acceptedEvent = (ContactRequest.AcceptedContactRequestEvent) processedEvents.getFirst();
    assertThat(acceptedEvent.contactRequestId()).isEqualTo(contactRequest.getContactRequestId());
    assertThat(acceptedEvent.requestedUser()).isEqualTo(contactRequest.getRequestedUser());
    assertThat(acceptedEvent.requesterUser()).isEqualTo(contactRequest.getRequesterUser());
    assertThat(acceptedEvent.creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
  }

  @Test
  void handleCommandAsRejectedOk() {

    // Arrange
    var requesterUserId = UUID.randomUUID().toString();
    var requestedUserId = UUID.randomUUID().toString();
    var accepted = Boolean.FALSE;

    userRepositorySpy.save(User.createUser(UserId.of(requesterUserId), "foo1", "foo1@gmail.com"));
    userRepositorySpy.save(User.createUser(UserId.of(requestedUserId), "foo2", "foo2@gmail.com"));

    var contactRequest = ContactRequest.create(() -> UUID.randomUUID().toString(), UserId.of(requesterUserId), UserId.of(requestedUserId), "Greetings.");
    contactRequestRepositorySpy.save(contactRequest);

    var command = new ProcessContactRequestCommand(contactRequest.getContactRequestId().id(), requestedUserId, accepted);

    // Act
    var result = sut.handle(command);

    // Assert
    assertThat(result).isEqualTo(EmptyCommandResponse.VALUE);

    var rejectedContactRequest = contactRequestRepositorySpy.findById(contactRequest.getContactRequestId());
    assertThat(rejectedContactRequest).isNotNull().isPresent();
    assertThat(rejectedContactRequest.get().getStatus()).isEqualTo(ContactRequestStatus.REJECTED);

    var processedEvents = eventResolverFactorySpy.getProcessedEvents();
    assertThat(processedEvents)
            .isNotEmpty()
            .hasSize(1)
            .allMatch(value -> value instanceof ContactRequest.RejectedContactRequestEvent);

    var acceptedEvent = (ContactRequest.RejectedContactRequestEvent) processedEvents.getFirst();
    assertThat(acceptedEvent.contactRequestId()).isEqualTo(contactRequest.getContactRequestId());
    assertThat(acceptedEvent.requesterUser()).isEqualTo(contactRequest.getRequesterUser());
    assertThat(acceptedEvent.creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
  }

  @Test
  void handleCommandThrowsExceptionDueContactRequestDoesNotExist() {

    // Arrange
    var requesterUserId = UUID.randomUUID().toString();
    var requestedUserId = UUID.randomUUID().toString();
    var accepted = Boolean.TRUE;

    userRepositorySpy.save(User.createUser(UserId.of(requesterUserId), "foo1", "foo1@gmail.com"));
    userRepositorySpy.save(User.createUser(UserId.of(requestedUserId), "foo2", "foo2@gmail.com"));

    var contactRequest = ContactRequest.create(() -> UUID.randomUUID().toString(), UserId.of(requesterUserId), UserId.of(requestedUserId), "Greetings.");
    var command = new ProcessContactRequestCommand(contactRequest.getContactRequestId().id(), requestedUserId, accepted);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("The contact request with the id " + contactRequest.getContactRequestId().id() + " does not exist.");
  }

  @Test
  void handleCommandThrowsExceptionDueRequestedUserDoesNotExist() {

    // Arrange
    var requesterUserId = UUID.randomUUID().toString();
    var requestedUserId = UUID.randomUUID().toString();
    var accepted = Boolean.TRUE;

    userRepositorySpy.save(User.createUser(UserId.of(requesterUserId), "foo1", "foo1@gmail.com"));

    var contactRequest = ContactRequest.create(() -> UUID.randomUUID().toString(), UserId.of(requesterUserId), UserId.of(requestedUserId), "Greetings.");
    contactRequestRepositorySpy.save(contactRequest);

    var command = new ProcessContactRequestCommand(contactRequest.getContactRequestId().id(), requestedUserId, accepted);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("The user with the id " + requestedUserId + " does not exist.");
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void handleCommandThrowsExceptionDueContactRequestWasAccepted(boolean accepted) {

    // Arrange
    var requesterUserId = UUID.randomUUID().toString();
    var requestedUserId = UUID.randomUUID().toString();

    userRepositorySpy.save(User.createUser(UserId.of(requesterUserId), "foo1", "foo1@gmail.com"));
    userRepositorySpy.save(User.createUser(UserId.of(requestedUserId), "foo2", "foo2@gmail.com"));

    var contactRequest = ContactRequest.create(() -> UUID.randomUUID().toString(), UserId.of(requesterUserId), UserId.of(requestedUserId), "Greetings.");
    contactRequestRepositorySpy.save(contactRequest.processRequest(Boolean.TRUE, UserId.of(requestedUserId)));

    var command = new ProcessContactRequestCommand(contactRequest.getContactRequestId().id(), requestedUserId, accepted);

    // Act & Assert
    var exception = assertThrows(IllegalStateException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("Invalid status permutation.");
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void handleCommandThrowsExceptionDueContactRequestWasRejected(boolean accepted) {

    // Arrange
    var requesterUserId = UUID.randomUUID().toString();
    var requestedUserId = UUID.randomUUID().toString();

    userRepositorySpy.save(User.createUser(UserId.of(requesterUserId), "foo1", "foo1@gmail.com"));
    userRepositorySpy.save(User.createUser(UserId.of(requestedUserId), "foo2", "foo2@gmail.com"));

    var contactRequest = ContactRequest.create(() -> UUID.randomUUID().toString(), UserId.of(requesterUserId), UserId.of(requestedUserId), "Greetings.");
    contactRequestRepositorySpy.save(contactRequest.processRequest(Boolean.FALSE, UserId.of(requestedUserId)));

    var command = new ProcessContactRequestCommand(contactRequest.getContactRequestId().id(), requestedUserId, accepted);

    // Act & Assert
    var exception = assertThrows(IllegalStateException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains("Invalid status permutation.");
  }

  @Test
  void handleThrowsExceptionWhenProcessingNullCommand() {

    // Arrange
    ProcessContactRequestCommand command = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The command to process cannot be null.");
  }

  @MethodSource("getInvalidData")
  @ParameterizedTest
  void handleInvalidData(String contactRequestId, String requestedUser, Boolean accepted, String expectedErrorMessage) {

    // Arrange
    var command = new ProcessContactRequestCommand(contactRequestId, requestedUser, accepted);

    // Act & Assert
    var exception = assertThrows(InvalidCommandDataException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }

  private static Stream<Arguments> getInvalidData() {

    String contactRequestId = UUID.randomUUID().toString();
    String requestedUser = UUID.randomUUID().toString();
    Boolean accepted = Boolean.TRUE;

    String expectedErrorMessage1 = "The contact request id cannot be empty.";
    String expectedErrorMessage2 = "The requested user id cannot be empty.";
    String expectedErrorMessage3 = "The accepted flag cannot be null.";

    return Stream.of(
            Arguments.of(null, requestedUser, accepted, expectedErrorMessage1),
            Arguments.of("", requestedUser, accepted, expectedErrorMessage1),
            Arguments.of("  ", requestedUser, accepted, expectedErrorMessage1),
            Arguments.of(contactRequestId, null, accepted, expectedErrorMessage2),
            Arguments.of(contactRequestId, "", accepted, expectedErrorMessage2),
            Arguments.of(contactRequestId, "  ", accepted, expectedErrorMessage2),
            Arguments.of(contactRequestId, requestedUser, null, expectedErrorMessage3)
    );
  }
}
