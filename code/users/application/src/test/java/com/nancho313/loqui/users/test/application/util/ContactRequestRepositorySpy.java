package com.nancho313.loqui.users.test.application.util;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.repository.ContactRequestRepository;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.UserId;

import java.util.*;

public class ContactRequestRepositorySpy implements ContactRequestRepository {
  
  private final List<ContactRequest> data = new ArrayList<>();
  
  public boolean existsPendingRequest(UserId requesterId, UserId requestedId) {
    return data.stream().anyMatch(contactRequest -> contactRequest.getRequesterUser().equals(requesterId)
            && contactRequest.getRequestedUser().equals(requestedId) && contactRequest.getStatus().equals(ContactRequestStatus.PENDING));
  }
  
  public ContactRequest save(ContactRequest newContactRequest) {
    data.removeIf(value -> value.getContactRequestId().equals(newContactRequest.getContactRequestId()));
    data.add(newContactRequest);
    return newContactRequest;
  }
  
  public Optional<ContactRequest> findById(ContactRequestId id) {
    return data.stream().filter(contactRequest -> contactRequest.getContactRequestId().equals(id)).findFirst();
  }
  
  public List<ContactRequest> findAll() {
    
    return List.copyOf(data);
  }
}
