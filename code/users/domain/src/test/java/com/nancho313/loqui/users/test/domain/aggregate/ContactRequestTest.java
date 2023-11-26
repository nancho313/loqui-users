package com.nancho313.loqui.users.test.domain.aggregate;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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
    
    var event = (ContactRequest.CreatedContactRequestEvent)result.getCurrentEvents().get(0);
    assertThat(event.contactRequestId().id()).isEqualTo(id);
    assertThat(event.creationDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
    assertThat(event.requesterUser()).isEqualTo(requesterUser);
    assertThat(event.requestedUser()).isEqualTo(requestedUser);
  }
}
