package com.nancho313.loqui.users.contract.rabbitmq.listener;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.events.CreatedUserEvent;
import com.nancho313.loqui.users.contract.rabbitmq.handler.RabbitMqHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqListener {
  
  private final RabbitMqHandler<AcceptedContactRequestEvent> acceptedContactRequestEventHandler;
  
  private final RabbitMqHandler<CreatedUserEvent> createdUserEventHandler;
  
  public void consumeAcceptedContactRequestEvent(byte[] data) {
    
    acceptedContactRequestEventHandler.consume(data);
  }
  
  public void consumeCreatedUserEvent(byte[] data) {
    
    createdUserEventHandler.consume(data);
  }
}
