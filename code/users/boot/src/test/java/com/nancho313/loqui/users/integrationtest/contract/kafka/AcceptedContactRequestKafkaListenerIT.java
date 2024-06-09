package com.nancho313.loqui.users.integrationtest.contract.kafka;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.contract.kafka.listener.AcceptedContactRequestKafkaListener;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptedContactRequestKafkaListenerIT extends BaseIntegrationTest {

  @Autowired
  private Neo4jClient neo4jClient;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserDataSource userDataSource;

  @Autowired
  private AcceptedContactRequestKafkaListener sut;

  @AfterEach
  void teardown() {

    neo4jClient.query("""
        MATCH (n)
        DETACH DELETE n""").run();
  }

  @Test
  void consumeOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepository.save(user1);
    userRepository.save(user2);

    var payload = new AcceptedContactRequestEvent(UUID.randomUUID().toString(), userId, contactId, 1L);
    Message<AcceptedContactRequestEvent> message = new GenericMessage<>(payload);

    // Act
    sut.consumeMessage(message);

    // Assert
    var contacts = userDataSource.searchContacts(userId);
    assertThat(contacts).isNotNull().hasSize(1);
    var contact = contacts.getFirst();
    assertThat(contact.id()).isEqualTo(user2.getId().id());
    assertThat(contact.email()).isEqualTo(user2.getEmail());
    assertThat(contact.username()).isEqualTo(user2.getUsername());
  }
}