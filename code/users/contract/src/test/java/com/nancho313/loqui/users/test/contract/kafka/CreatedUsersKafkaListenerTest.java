package com.nancho313.loqui.users.test.contract.kafka;

import com.nancho313.loqui.events.CreatedUserEvent;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.CreateUserCommand;
import com.nancho313.loqui.users.contract.kafka.listener.CreatedUsersKafkaListener;
import com.nancho313.loqui.users.test.contract.util.CommandHandlerTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreatedUsersKafkaListenerTest {

  private CommandHandlerTestUtil<CreateUserCommand, EmptyCommandResponse> commandHandlerSpy;

  private CreatedUsersKafkaListener sut;

  @BeforeEach
  void setup() {

    commandHandlerSpy = new CommandHandlerTestUtil<>();
    sut = new CreatedUsersKafkaListener(commandHandlerSpy);
  }

  @Test
  void consumeMessageOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var email = "foo@foo.com";
    var payload = new CreatedUserEvent(userId, username, email);
    var message = new GenericMessage<>(payload);
    commandHandlerSpy.initResponse(EmptyCommandResponse.VALUE);

    // Act
    sut.consumeMessage(message);

    // Assert
    var commandToProcess = commandHandlerSpy.getCommandToProcess();
    assertThat(commandToProcess.userId()).isEqualTo(userId);
    assertThat(commandToProcess.username()).isEqualTo(username);
    assertThat(commandToProcess.email()).isEqualTo(email);
  }
}
