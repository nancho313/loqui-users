package com.nancho313.loqui.users.infrastructure.repository;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.repository.ContactRequestRepository;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.ContactRequestMongodbDAO;
import com.nancho313.loqui.users.infrastructure.mapper.ContactRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContactRequestRepositoryImpl implements ContactRequestRepository {
  
  private final ContactRequestMongodbDAO dao;
  
  private final ContactRequestMapper mapper;
  
  public boolean existsPendingRequest(UserId requesterId, UserId requestedId) {
    return dao.existsByRequesterUserAndRequestedUserAndStatus(requesterId.id(), requestedId.id(),
            ContactRequestStatus.PENDING.name());
  }
  
  public ContactRequest save(ContactRequest newContactRequest) {
    return mapper.toEntity(dao.save(mapper.toDocument(newContactRequest)));
  }
  
  public Optional<ContactRequest> findById(ContactRequestId id) {
    return dao.findById(id.id()).map(mapper::toEntity);
  }
}
