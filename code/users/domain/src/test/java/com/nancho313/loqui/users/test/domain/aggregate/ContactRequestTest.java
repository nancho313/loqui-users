package com.nancho313.loqui.users.test.domain.aggregate;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
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

class ContactRequestTest {
  
  @Test
  void buildObjectOk() {
    
    // Arrange
    var contactRequestId = ContactRequestId.of(UUID.randomUUID().toString());
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var currentDate = CurrentDate.now();
    var status = ContactRequestStatus.PENDING;
    var message = "foo message";
    
    // Act
    var result = new ContactRequest(contactRequestId, requesterUser, requestedUser, currentDate, status, message);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContactRequestId()).isEqualTo(contactRequestId);
    assertThat(result.getRequesterUser()).isEqualTo(requesterUser);
    assertThat(result.getRequestedUser()).isEqualTo(requestedUser);
    assertThat(result.getCurrentDate().creationDate()).isCloseTo(currentDate.creationDate(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getCurrentDate().lastUpdatedDate()).isCloseTo(currentDate.lastUpdatedDate(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getStatus()).isEqualTo(status);
    assertThat(result.getMessage()).isEqualTo(message);
    assertThat(result.getCurrentEvents()).isEmpty();
  }
  
  @Test
  void buildObjectWithFactoryMethodOk() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var message = "foo message";
    
    // Act
    var result = ContactRequest.create(() -> id, requesterUser, requestedUser, message);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContactRequestId().id()).isEqualTo(id);
    assertThat(result.getRequesterUser()).isEqualTo(requesterUser);
    assertThat(result.getRequestedUser()).isEqualTo(requestedUser);
    assertThat(result.getCurrentDate().creationDate()).isCloseTo(LocalDateTime.now(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getCurrentDate().lastUpdatedDate()).isCloseTo(LocalDateTime.now(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getStatus()).isEqualTo(ContactRequestStatus.PENDING);
    assertThat(result.getMessage()).isEqualTo(message);
    assertThat(result.getCurrentEvents()).isNotEmpty().hasSize(1).allMatch(event -> event instanceof ContactRequest.CreatedContactRequestEvent);
    
    var event = (ContactRequest.CreatedContactRequestEvent) result.getCurrentEvents().get(0);
    assertThat(event.contactRequestId().id()).isEqualTo(id);
    assertThat(event.creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
    assertThat(event.requesterUser()).isEqualTo(requesterUser);
    assertThat(event.requestedUser()).isEqualTo(requestedUser);
  }
  
  @Test
  void processRequestAsAcceptedOk() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var message = "foo message";
    var currentDate = new CurrentDate(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1));
    var contactRequest = new ContactRequest(ContactRequestId.of(id), requesterUser, requestedUser, currentDate,
            ContactRequestStatus.PENDING, message);
    
    // Act
    var result = contactRequest.processRequest(Boolean.TRUE, requestedUser);
    
    // Assert
    assertThat(result.getStatus()).isEqualTo(ContactRequestStatus.ACCEPTED);
    assertThat(result.getCurrentDate().lastUpdatedDate()).isCloseTo(LocalDateTime.now(), within(100,
            ChronoUnit.MILLIS));
    
    assertThat(result.getCurrentEvents()).isNotEmpty().hasSize(1).allMatch(event -> event instanceof ContactRequest.AcceptedContactRequestEvent);
    
    var event = (ContactRequest.AcceptedContactRequestEvent) result.getCurrentEvents().get(0);
    assertThat(event.contactRequestId().id()).isEqualTo(id);
    assertThat(event.creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
    assertThat(event.requesterUser()).isEqualTo(requesterUser);
    assertThat(event.requestedUser()).isEqualTo(requestedUser);
  }
  
  @Test
  void processRequestAsRejectedOk() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var message = "foo message";
    var currentDate = new CurrentDate(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1));
    var contactRequest = new ContactRequest(ContactRequestId.of(id), requesterUser, requestedUser, currentDate,
            ContactRequestStatus.PENDING, message);
    
    // Act
    var result = contactRequest.processRequest(Boolean.FALSE, requestedUser);
    
    // Assert
    assertThat(result.getStatus()).isEqualTo(ContactRequestStatus.REJECTED);
    assertThat(result.getCurrentDate().lastUpdatedDate()).isCloseTo(LocalDateTime.now(), within(100,
            ChronoUnit.MILLIS));
    
    assertThat(result.getCurrentEvents()).isNotEmpty().hasSize(1).allMatch(event -> event instanceof ContactRequest.RejectedContactRequestEvent);
    
    var event = (ContactRequest.RejectedContactRequestEvent) result.getCurrentEvents().get(0);
    assertThat(event.contactRequestId().id()).isEqualTo(id);
    assertThat(event.creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
    assertThat(event.requesterUser()).isEqualTo(requesterUser);
  }
  
  @Test
  void processRequestUsingANullRequestedUserThrowsException() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var message = "foo message";
    var currentDate = new CurrentDate(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1));
    var contactRequest = new ContactRequest(ContactRequestId.of(id), requesterUser, requestedUser, currentDate,
            ContactRequestStatus.PENDING, message);
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> contactRequest.processRequest(Boolean.TRUE,
            null));
    assertThat(exception.getMessage()).contains("The requested user id cannot be null.");
    
  }
  
  @Test
  void processRequestUsingADifferentRequestedUserThrowsException() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var message = "foo message";
    var currentDate = new CurrentDate(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1));
    var contactRequest = new ContactRequest(ContactRequestId.of(id), requesterUser, requestedUser, currentDate,
            ContactRequestStatus.PENDING, message);
    
    var anotherRequestedUserId = UserId.of(UUID.randomUUID().toString());
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> contactRequest.processRequest(Boolean.TRUE,
            anotherRequestedUserId));
    assertThat(exception.getMessage()).contains(("Requested users are not the same. It should be %s but the current " +
            "value is %s").formatted(requestedUser.id(), anotherRequestedUserId.id()));
    
  }
  
  @MethodSource("getBuildObjectInvalidData")
  @ParameterizedTest
  void buildObjectWithInvalidData(ContactRequestId contactRequestId, UserId requesterUser, UserId requestedUser, CurrentDate currentDate,
                                  ContactRequestStatus status, String message, String expectedErrorMessage) {
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class,
            () -> new ContactRequest(contactRequestId, requesterUser, requestedUser, currentDate, status, message));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getBuildObjectInvalidData() {
    
    var contactRequestId = ContactRequestId.of(UUID.randomUUID().toString());
    var requesterUser = UserId.of(UUID.randomUUID().toString());
    var requestedUser = UserId.of(UUID.randomUUID().toString());
    var currentDate = CurrentDate.now();
    var status = ContactRequestStatus.PENDING;
    var message = "foo message";
    
    var expectedErrorMessage1 = "The contact request id cannot be null.";
    var expectedErrorMessage2 = "The requester user cannot be null.";
    var expectedErrorMessage3 = "The requested user cannot be null.";
    var expectedErrorMessage4 = "The current date cannot be null.";
    var expectedErrorMessage5 = "The status cannot be null.";
    var expectedErrorMessage6 = "The message cannot be empty.";
    
    return Stream.of(
            Arguments.of(null, requesterUser, requestedUser, currentDate, status, message, expectedErrorMessage1),
            Arguments.of(contactRequestId, null, requestedUser, currentDate, status, message, expectedErrorMessage2),
            Arguments.of(contactRequestId, requesterUser, null, currentDate, status, message, expectedErrorMessage3),
            Arguments.of(contactRequestId, requesterUser, requestedUser, null, status, message, expectedErrorMessage4),
            Arguments.of(contactRequestId, requesterUser, requestedUser, currentDate, null, message, expectedErrorMessage5),
            Arguments.of(contactRequestId, requesterUser, requestedUser, currentDate, status, null, expectedErrorMessage6),
            Arguments.of(contactRequestId, requesterUser, requestedUser, currentDate, status, "", expectedErrorMessage6),
            Arguments.of(contactRequestId, requesterUser, requestedUser, currentDate, status, "  ", expectedErrorMessage6)
    );
  }
}
