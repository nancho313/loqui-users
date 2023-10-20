package com.nancho313.loqui.users.domain.repository;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.UserId;

import java.util.Optional;

public interface ContactRequestRepository {
  
  boolean existsPendingRequest(UserId requesterId, UserId requestedId);
  
  ContactRequest save(ContactRequest newContactRequest);
  
  Optional<ContactRequest> findById(ContactRequestId id);
}
