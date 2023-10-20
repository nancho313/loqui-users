package com.nancho313.loqui.users.contract.rabbitmq.handler;

import com.nancho313.loqui.commons.AvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;

public abstract class RabbitMqHandler<T extends SpecificRecordBase> {

  private final AvroSerializer<T> serializer;
  
  public RabbitMqHandler() {
    this.serializer = new AvroSerializer<>(getSchema());
  }
  
  protected abstract void consumeMessage(T message);
  
  public void consume(byte[] data) {
  
    consumeMessage(serializer.deserialize(data));
  }
  
  public abstract Schema getSchema();
}
