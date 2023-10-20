package com.nancho313.loqui.users.infrastructure.client.mongodb.dao;

import com.nancho313.loqui.users.infrastructure.client.mongodb.document.ContactRequestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRequestMongodbDAO extends MongoRepository<ContactRequestDocument, String> {
  
  boolean existsByRequesterUserAndRequestedUserAndStatus(String requesterUser, String requestedUser, String status);
}
