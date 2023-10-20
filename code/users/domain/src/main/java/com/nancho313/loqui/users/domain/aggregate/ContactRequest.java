package com.nancho313.loqui.users.domain.aggregate;

import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nancho313.loqui.commons.ObjectValidator.isEmptyString;
import static com.nancho313.loqui.commons.ObjectValidator.isNull;

@Value
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ContactRequest extends DomainAggregate {
  
  private static final String INVALID_REQUESTED_USERS_ERROR_MESSAGE = "Requested users are not the same. It should be" +
          " " +
          "%s but the current value is %s.";
  
  private static final String INVALID_STATUS_PERMUTATION_ERROR_MESSAGE = "Invalid status permutation. The " +
          "ContactRequest %s cannot change from %s to %s.";
  
  ContactRequestId contactRequestId;
  
  UserId requesterUser;
  
  UserId requestedUser;
  
  CurrentDate currentDate;
  
  ContactRequestStatus status;
  
  String message;
  
  ContactRequest(List<DomainEvent> events, ContactRequestId contactRequestId, UserId requesterUser,
                 UserId requestedUser, CurrentDate currentDate,
                 ContactRequestStatus status, String message) {
    super(events);
    this.contactRequestId = contactRequestId;
    this.requesterUser = requesterUser;
    this.requestedUser = requestedUser;
    this.currentDate = currentDate;
    this.status = status;
    this.message = message;
    validate();
  }
  
  public ContactRequest(ContactRequestId contactRequestId, UserId requesterUser, UserId requestedUser,
                        CurrentDate currentDate, ContactRequestStatus status, String message) {
    this(Collections.emptyList(), contactRequestId, requesterUser, requestedUser, currentDate, status, message);
  }
  
  public static ContactRequest create(IdGenerator idGenerator, UserId requesterUserId, UserId requestedUserId,
                                      String message) {
    
    if (isNull(idGenerator)) {
      
      throw new IllegalArgumentException("The id generator cannot be null.");
    }
    
    var id = ContactRequestId.of(idGenerator.generateId());
    var event = new CreatedContactRequestEvent(id, requesterUserId, requestedUserId, LocalDateTime.now());
    return new ContactRequest(List.of(event), id, requesterUserId, requestedUserId, CurrentDate.now(),
            ContactRequestStatus.PENDING, message);
  }
  
  public ContactRequest processRequest(boolean accept, UserId requestedUserId) {
    
    if (isNull(requestedUserId)) {
      
      throw new IllegalArgumentException("The requested user id cannot be null.");
    }
    
    if (!requestedUser.equals(requestedUserId)) {
      
      throw new IllegalArgumentException(INVALID_REQUESTED_USERS_ERROR_MESSAGE.formatted(requestedUser.id(),
              requestedUserId.id()));
    }
    
    ContactRequest processedContactRequest;
    
    if (accept) {
      
      var event = new AcceptedContactRequestEvent(this.contactRequestId, this.requesterUser, requestedUserId,
              LocalDateTime.now());
      processedContactRequest = changeStatus(List.of(event), ContactRequestStatus.ACCEPTED);
    } else {
      var event = new RejectedContactRequestEvent(this.contactRequestId, this.requesterUser, LocalDateTime.now());
      processedContactRequest = changeStatus(List.of(event), ContactRequestStatus.REJECTED);
    }
    return processedContactRequest;
  }
  
  private ContactRequest changeStatus(List<DomainEvent> events, ContactRequestStatus newStatus) {
    
    assert (newStatus != null);
    validateStatusPermutation(newStatus);
    return new ContactRequest(events, this.contactRequestId, this.requesterUser,
            this.requestedUser, this.currentDate.update(), newStatus, this.message);
  }
  
  private void validateStatusPermutation(ContactRequestStatus newStatus) {
    
    assert (newStatus != null);
    List<ContactRequestStatus> allowedStatuses = Collections.emptyList();
    
    if (status.equals(ContactRequestStatus.PENDING)) {
      
      allowedStatuses = List.of(ContactRequestStatus.ACCEPTED, ContactRequestStatus.DELETED,
              ContactRequestStatus.REJECTED);
    }
    
    if (!allowedStatuses.contains(newStatus)) {
      
      throw new IllegalStateException(INVALID_STATUS_PERMUTATION_ERROR_MESSAGE.formatted(this.contactRequestId.id(),
              this.status, newStatus));
    }
  }
  
  private void validate() {
    
    List<String> errors = new ArrayList<>();
    
    if (isNull(contactRequestId)) {
      
      errors.add("The contact request id cannot be null.");
    }
    
    if (isNull(requesterUser)) {
      
      errors.add("The requester user cannot be null.");
    }
    
    if (isNull(requestedUser)) {
      
      errors.add("The requested user cannot be null.");
    }
    
    if (isNull(currentDate)) {
      
      errors.add("The current date cannot be null.");
    }
    
    if (isNull(status)) {
      
      errors.add("The status cannot be null.");
    }
    
    if (isEmptyString(message)) {
      
      errors.add("The message cannot be empty.");
    }
    
    if (!errors.isEmpty()) {
      
      throw new IllegalArgumentException("Cannot create a ContactRequest object. Errors -> %s".formatted(errors));
    }
  }
  
  
  public record CreatedContactRequestEvent(ContactRequestId contactRequestId, UserId requesterUser,
                                           UserId requestedUser, LocalDateTime creationDate) implements DomainEvent {
  }
  
  public record AcceptedContactRequestEvent(ContactRequestId contactRequestId, UserId requesterUser,
                                            UserId requestedUser,
                                            LocalDateTime creationDate) implements DomainEvent {
  }
  
  public record RejectedContactRequestEvent(ContactRequestId contactRequestId, UserId requesterUser,
                                            LocalDateTime creationDate) implements DomainEvent {
  }
}
