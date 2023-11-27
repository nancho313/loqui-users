package com.nancho313.loqui.users.integrationtest.infrastructure.datasource;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.repository.ContactRequestRepository;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import com.nancho313.loqui.users.projection.datasource.ContactRequestDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContactRequestDataSourceIT extends BaseIntegrationTest {
  
  @Autowired
  private ContactRequestDataSource sut;
  
  @Autowired
  private ContactRequestRepository contactRequestRepository;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private IdGenerator idGenerator;
  
  @AfterEach
  void tearDown() {
    
    mongoTemplate.dropCollection("contact_request");
  }
  
  @Test
  void getContactRequestsOk() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    var requestedUserId = UserId.of(UUID.randomUUID().toString());
    var message = "Hey, do you wanna be my friend ???";
    var contactRequest = ContactRequest.create(idGenerator, requesterUserId, requestedUserId, message);
    contactRequestRepository.save(contactRequest);
    
    // Act
    var result = sut.getContactRequests(requesterUserId.id(), contactRequest.getStatus().name());
    
    // Assert
    assertThat(result).isNotEmpty().hasSize(1);
    var contactRequestProjection = result.get(0);
    assertThat(contactRequestProjection.requesterUser()).isEqualTo(requesterUserId.id());
    assertThat(contactRequestProjection.requestedUser()).isEqualTo(requestedUserId.id());
    assertThat(contactRequestProjection.status()).isEqualTo(contactRequest.getStatus().name());
    assertThat(contactRequestProjection.message()).isEqualTo(contactRequest.getMessage());
  }
  
  @Test
  void getContactRequestsReturnsEmptyData() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    
    // Act
    var result = sut.getContactRequests(requesterUserId.id(), ContactRequestStatus.PENDING.name());
    
    // Assert
    assertThat(result).isEmpty();
  }
}
