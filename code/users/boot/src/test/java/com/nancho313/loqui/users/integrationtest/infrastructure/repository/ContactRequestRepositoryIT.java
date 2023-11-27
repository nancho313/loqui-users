package com.nancho313.loqui.users.integrationtest.infrastructure.repository;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.repository.ContactRequestRepository;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactRequestRepositoryIT extends BaseIntegrationTest {
  
  @Autowired
  private ContactRequestRepository sut;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private IdGenerator idGenerator;
  
  @AfterEach
  void tearDown() {
    
    mongoTemplate.dropCollection("contact_request");
  }
  
  @Test
  void saveContactRequestOk() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    var requestedUserId = UserId.of(UUID.randomUUID().toString());
    var message = "Hey, do you wanna be my friend ???";
    var contactRequest = ContactRequest.create(idGenerator, requesterUserId, requestedUserId, message);
    
    // Act
    var result = sut.save(contactRequest);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContactRequestId()).isEqualTo(contactRequest.getContactRequestId());
    assertThat(result.getRequesterUser()).isEqualTo(contactRequest.getRequesterUser());
    assertThat(result.getRequestedUser()).isEqualTo(contactRequest.getRequestedUser());
    assertThat(result.getMessage()).isEqualTo(contactRequest.getMessage());
    assertThat(result.getStatus()).isEqualTo(contactRequest.getStatus());
  }
  
  @Test
  void findByIdOk() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    var requestedUserId = UserId.of(UUID.randomUUID().toString());
    var message = "Hey, do you wanna be my friend ???";
    var contactRequest = ContactRequest.create(idGenerator, requesterUserId, requestedUserId, message);
    sut.save(contactRequest);
    
    // Act
    var result = sut.findById(contactRequest.getContactRequestId());
    
    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getContactRequestId()).isEqualTo(contactRequest.getContactRequestId());
    assertThat(result.get().getRequesterUser()).isEqualTo(contactRequest.getRequesterUser());
    assertThat(result.get().getRequestedUser()).isEqualTo(contactRequest.getRequestedUser());
    assertThat(result.get().getMessage()).isEqualTo(contactRequest.getMessage());
    assertThat(result.get().getStatus()).isEqualTo(contactRequest.getStatus());
  }
  
  @Test
  void findByIdReturnsEmpty() {
    
    // Arrange
    var contactRequestId = ContactRequestId.of(idGenerator.generateId());
    
    // Act
    var result = sut.findById(contactRequestId);
    
    // Assert
    assertThat(result).isEmpty();
  }
  
  @Test
  void existsPendingRequestReturnsTrue() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    var requestedUserId = UserId.of(UUID.randomUUID().toString());
    var message = "Hey, do you wanna be my friend ???";
    var contactRequest = ContactRequest.create(idGenerator, requesterUserId, requestedUserId, message);
    sut.save(contactRequest);
    
    // Act
    var result = sut.existsPendingRequest(requesterUserId, requestedUserId);
    
    // Assert
    assertThat(result).isTrue();
  }
  
  @Test
  void existsPendingRequestReturnsFalse() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    var requestedUserId = UserId.of(UUID.randomUUID().toString());
    
    // Act
    var result = sut.existsPendingRequest(requesterUserId, requestedUserId);
    
    // Assert
    assertThat(result).isFalse();
  }
}
