package com.nancho313.loqui.users.contract.kafka.listener;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.AddContactToUserCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcceptedContactRequestKafkaListener {
  
  private final CommandHandler<AddContactToUserCommand, EmptyCommandResponse> commandHandler;
  
  @KafkaListener(topics = "accepted-contact-request")
  public void consumeMessage(Message<AcceptedContactRequestEvent> message) {
    
    var value = message.getPayload();
    var command = new AddContactToUserCommand(value.getRequesterUser().toString(),
            value.getRequestedUser().toString());
    commandHandler.handle(command);
  }
}
