package com.nancho313.loqui.users.infrastructure.client.mongodb.dao;

import com.nancho313.loqui.users.infrastructure.client.mongodb.document.ContactRequestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ContactRequestMongodbDAO extends MongoRepository<ContactRequestDocument, String> {
  
  boolean existsByRequesterUserAndRequestedUserAndStatus(String requesterUser, String requestedUser, String status);
  
  @Query("{'$or':[ {requesterUser: ?0, status: ?1}, {requestedUser: ?0, status: ?1} ] }")
  List<ContactRequestDocument> findByIdUserAndStatus(String idUser, String status);
}
