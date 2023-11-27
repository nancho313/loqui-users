package com.nancho313.loqui.users.integrationtest.infrastructure.event;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.kafka.emitter.AcceptedContactRequestKafkaEmitter;
import com.nancho313.loqui.users.infrastructure.event.AcceptedContactRequestEventResolver;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

class AcceptedContactRequestEventResolverIT extends BaseIntegrationTest {
  
  @Autowired
  private AcceptedContactRequestEventResolver sut;
  
  @SpyBean
  private AcceptedContactRequestKafkaEmitter acceptedContactRequestKafkaEmitter;
  
  @Autowired
  private IdGenerator idGenerator;
  
  @Test
  void processEventOk() {
    
    // Arrange
    var requesterUserId = UserId.of(UUID.randomUUID().toString());
    var requestedUserId = UserId.of(UUID.randomUUID().toString());
    var message = "Hey, do you wanna be my friend ???";
    var contactRequest = ContactRequest.create(idGenerator, requesterUserId, requestedUserId, message);
    var events = contactRequest.processRequest(Boolean.TRUE, requestedUserId).getCurrentEvents();
    ArgumentCaptor<AcceptedContactRequestEvent> argumentCaptor =
            ArgumentCaptor.forClass(AcceptedContactRequestEvent.class);
    
    // Act
    sut.processEvent((ContactRequest.AcceptedContactRequestEvent) events.get(0));
    
    // Assert
    verify(acceptedContactRequestKafkaEmitter).sendMessage(argumentCaptor.capture(), anyList());
    var sentMessage = argumentCaptor.getValue();
    assertThat(sentMessage.getContactRequestId().toString()).isEqualTo(contactRequest.getContactRequestId().id());
    assertThat(sentMessage.getRequesterUser().toString()).isEqualTo(contactRequest.getRequesterUser().id());
    assertThat(sentMessage.getRequestedUser().toString()).isEqualTo(contactRequest.getRequestedUser().id());
  }
}
