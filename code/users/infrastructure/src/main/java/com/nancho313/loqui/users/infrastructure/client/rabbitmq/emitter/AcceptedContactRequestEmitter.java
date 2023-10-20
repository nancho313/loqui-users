package com.nancho313.loqui.users.infrastructure.client.rabbitmq.emitter;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import org.apache.avro.Schema;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AcceptedContactRequestEmitter extends RabbitMqEmitter<AcceptedContactRequestEvent> {
  
  private static final String EXCHANGE = "accepted-contact-request-exch";
  
  public AcceptedContactRequestEmitter(RabbitTemplate rabbitTemplate) {
    super(rabbitTemplate);
  }
  
  protected Schema getSchema() {
    return AcceptedContactRequestEvent.getClassSchema();
  }
  
  protected String getExchange() {
    return EXCHANGE;
  }
}
