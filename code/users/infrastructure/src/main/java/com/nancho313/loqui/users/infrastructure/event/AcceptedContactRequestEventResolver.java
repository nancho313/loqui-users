package com.nancho313.loqui.users.infrastructure.event;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.event.EventResolver;
import com.nancho313.loqui.users.infrastructure.client.rabbitmq.emitter.RabbitMqEmitter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class AcceptedContactRequestEventResolver implements EventResolver<ContactRequest.AcceptedContactRequestEvent> {
  
  private final RabbitMqEmitter<AcceptedContactRequestEvent> emitter;
  
  public void processEvent(ContactRequest.AcceptedContactRequestEvent event) {
    
    var message = AcceptedContactRequestEvent.newBuilder().setContactRequestId(event.contactRequestId().id())
            .setRequesterUser(event.requesterUser().id())
            .setRequestedUser(event.requestedUser().id())
                    .setDate(event.creationDate().toInstant(ZoneOffset.UTC).toEpochMilli()).build();
    emitter.sendMessage(message);
  }
  
  public Class<ContactRequest.AcceptedContactRequestEvent> getType() {
    return ContactRequest.AcceptedContactRequestEvent.class;
  }
  
}
