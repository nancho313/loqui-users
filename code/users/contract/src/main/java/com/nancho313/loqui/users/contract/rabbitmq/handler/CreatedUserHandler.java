package com.nancho313.loqui.users.contract.rabbitmq.handler;

import com.nancho313.loqui.events.CreatedUserEvent;
import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.CreateUserCommand;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatedUserHandler extends RabbitMqHandler<CreatedUserEvent> {
  
  private final CommandHandler<CreateUserCommand, EmptyCommandResponse> commandHandler;
  
  protected void consumeMessage(CreatedUserEvent message) {
    
    var command = new CreateUserCommand(message.getUserId().toString(), message.getUsername().toString(),
            message.getEmail().toString());
    commandHandler.handle(command);
  }
  
  public Schema getSchema() {
    return CreatedUserEvent.getClassSchema();
  }
}
