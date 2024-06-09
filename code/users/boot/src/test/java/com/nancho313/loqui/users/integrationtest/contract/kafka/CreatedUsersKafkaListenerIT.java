package com.nancho313.loqui.users.integrationtest.contract.kafka;

import com.nancho313.loqui.events.CreatedUserEvent;
import com.nancho313.loqui.users.contract.kafka.listener.CreatedUsersKafkaListener;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreatedUsersKafkaListenerIT extends BaseIntegrationTest {

  @Autowired
  private CreatedUsersKafkaListener sut;

  @Autowired
  private Neo4jClient neo4jClient;

  @Autowired
  private UserRepository userRepository;

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
    var username = "foo";
    var email = "foo@foo.com";
    var payload = new CreatedUserEvent(userId, username, email);
    Message<CreatedUserEvent> message = new GenericMessage<>(payload);

    // Act
    sut.consumeMessage(message);

    // Assert
    var opCreatedUser = userRepository.findById(UserId.of(userId));
    assertThat(opCreatedUser).isPresent();
    var createdUser = opCreatedUser.get();
    assertThat(createdUser.getUsername()).isEqualTo(username);
    assertThat(createdUser.getEmail()).isEqualTo(email);
    assertThat(createdUser.getId().id()).isEqualTo(userId);
  }
}
