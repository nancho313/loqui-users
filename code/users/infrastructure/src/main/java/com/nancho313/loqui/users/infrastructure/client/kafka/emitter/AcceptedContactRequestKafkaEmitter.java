package com.nancho313.loqui.users.infrastructure.client.kafka.emitter;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AcceptedContactRequestKafkaEmitter extends LoquiKafkaEmitter<AcceptedContactRequestEvent> {
  
  private static final String TOPIC_NAME = "accepted-contact-request";
  
  public AcceptedContactRequestKafkaEmitter(ProducerFactory<String, AcceptedContactRequestEvent> producerFactory) {
    super(producerFactory);
  }
  
  protected String getTopic() {
    return TOPIC_NAME;
  }
}
