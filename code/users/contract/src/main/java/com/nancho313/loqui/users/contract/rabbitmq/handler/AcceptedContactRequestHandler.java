package com.nancho313.loqui.users.contract.rabbitmq.handler;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.AddContactToUserCommand;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcceptedContactRequestHandler extends RabbitMqHandler<AcceptedContactRequestEvent> {
  
  private final CommandHandler<AddContactToUserCommand, EmptyCommandResponse> commandHandler;
  
  protected void consumeMessage(AcceptedContactRequestEvent message) {
    
    var command = new AddContactToUserCommand(message.getRequesterUser().toString(),
            message.getRequestedUser().toString());
    commandHandler.handle(command);
  }
  
  public Schema getSchema() {
    return AcceptedContactRequestEvent.getClassSchema();
  }
}
