package com.nancho313.loqui.users.contract.kafka.listener;

import com.nancho313.loqui.events.CreatedUserEvent;
import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.CreateUserCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatedUsersKafkaListener {
  
  private final CommandHandler<CreateUserCommand, EmptyCommandResponse> commandHandler;
  
  @KafkaListener(topics = "created-users")
  public void consumeMessage(Message<CreatedUserEvent> message) {
    
    var value = message.getPayload();
    var command = new CreateUserCommand(value.getUserId().toString(), value.getUsername().toString(),
            value.getEmail().toString());
    commandHandler.handle(command);
  }
}
