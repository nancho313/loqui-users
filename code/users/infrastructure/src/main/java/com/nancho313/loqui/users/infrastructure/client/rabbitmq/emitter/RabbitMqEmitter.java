package com.nancho313.loqui.users.infrastructure.client.rabbitmq.emitter;

import com.nancho313.loqui.commons.AvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class RabbitMqEmitter <T extends SpecificRecordBase>{
  
  private final AvroSerializer<T> serializer;
  
  private final RabbitTemplate rabbitTemplate;
  
  public RabbitMqEmitter(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
    this.serializer = new AvroSerializer<>(getSchema());
  }
  
  public void sendMessage(T message) {
  
    var serializedMessage = serializer.serialize(message);
    rabbitTemplate.send(getExchange(), "", new Message(serializedMessage));
  }
  
  protected abstract Schema getSchema();
  
  protected abstract String getExchange();
}
