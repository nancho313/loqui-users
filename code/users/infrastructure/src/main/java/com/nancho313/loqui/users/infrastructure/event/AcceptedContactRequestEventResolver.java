package com.nancho313.loqui.users.infrastructure.event;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.event.EventResolver;
import com.nancho313.loqui.users.infrastructure.client.kafka.emitter.AcceptedContactRequestKafkaEmitter;
import com.nancho313.loqui.users.infrastructure.client.kafka.emitter.LoquiKafkaEmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcceptedContactRequestEventResolver implements EventResolver<ContactRequest.AcceptedContactRequestEvent> {

  private final LoquiKafkaEmitter<AcceptedContactRequestEvent> emitter;
  
  public void processEvent(ContactRequest.AcceptedContactRequestEvent event) {
    
    var message = AcceptedContactRequestEvent.newBuilder().setContactRequestId(event.contactRequestId().id())
            .setRequesterUser(event.requesterUser().id())
            .setRequestedUser(event.requestedUser().id())
            .setDate(event.creationDate().toInstant(ZoneOffset.UTC).toEpochMilli()).build();
    log.info("Message to send -> {}", message);

    emitter.sendMessage(message, new ArrayList<>());
  }
  
  public Class<ContactRequest.AcceptedContactRequestEvent> getType() {
    return ContactRequest.AcceptedContactRequestEvent.class;
  }
  
}
