package com.nancho313.loqui.users.test.contract.kafka;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.AddContactToUserCommand;
import com.nancho313.loqui.users.contract.kafka.listener.AcceptedContactRequestKafkaListener;
import com.nancho313.loqui.users.test.contract.util.CommandHandlerTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.GenericMessage;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptedContactRequestKafkaListenerTest {

  private CommandHandlerTestUtil<AddContactToUserCommand, EmptyCommandResponse> commandHandlerSpy;

  private AcceptedContactRequestKafkaListener sut;

  @BeforeEach
  void setup() {

    commandHandlerSpy = new CommandHandlerTestUtil<>();
    sut = new AcceptedContactRequestKafkaListener(commandHandlerSpy);
  }

  @Test
  void consumeMessageOk() {

    // Arrange
    var contactRequestId = UUID.randomUUID().toString();
    var requesterUser = UUID.randomUUID().toString();
    var requestedUser = UUID.randomUUID().toString();
    var date = 1234567L;
    var payload = new AcceptedContactRequestEvent(contactRequestId, requesterUser, requestedUser, date);
    var message = new GenericMessage<>(payload);
    commandHandlerSpy.initResponse(EmptyCommandResponse.VALUE);

    // Act
    sut.consumeMessage(message);

    // Assert
    var commandToProcess = commandHandlerSpy.getCommandToProcess();
    assertThat(commandToProcess.contactId()).isEqualTo(requestedUser);
    assertThat(commandToProcess.userId()).isEqualTo(requesterUser);
  }
}
