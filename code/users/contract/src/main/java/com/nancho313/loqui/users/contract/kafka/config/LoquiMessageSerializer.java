package com.nancho313.loqui.users.contract.kafka.config;

import com.nancho313.loqui.commons.AvroSerializer;
import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.events.CreatedUserEvent;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

public class LoquiMessageSerializer<T extends SpecificRecordBase> implements Deserializer<T>, Serializer<T> {
  
  private final Map<String, AvroSerializer<T>> avroSerializers;
  
  public LoquiMessageSerializer() {
    
    avroSerializers = new HashMap<>();
    avroSerializers.put("accepted-contact-request", new AvroSerializer<>(AcceptedContactRequestEvent.getClassSchema()));
    avroSerializers.put("created-users", new AvroSerializer<>(CreatedUserEvent.getClassSchema()));
  }
  
  public T deserialize(String key, byte[] bytes) {
    return avroSerializers.get(key).deserialize(bytes);
  }
  
  public byte[] serialize(String key, T message) {
    return avroSerializers.get(key).serialize(message);
  }
  
  public void configure(Map<String, ?> configs, boolean isKey) {
    Deserializer.super.configure(configs, isKey);
  }
  
  public T deserialize(String topic, Headers headers, byte[] data) {
    return Deserializer.super.deserialize(topic, headers, data);
  }
  
  public void close() {
    Deserializer.super.close();
  }
  
  public byte[] serialize(String topic, Headers headers, T data) {
    return Serializer.super.serialize(topic, headers, data);
  }
}
