package com.nancho313.loqui.users.infrastructure.event;

import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.event.EventResolver;
import com.nancho313.loqui.users.domain.event.EventResolverFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventResolverFactoryImpl implements EventResolverFactory {
  
  private final List<EventResolver<?>> currentEventResolvers;
  
  @SuppressWarnings("unchecked")
  public <T extends DomainEvent> Optional<EventResolver<T>> getResolver(T event) {
    var value = currentEventResolvers.stream()
            .filter(eventResolver -> eventResolver.getType().equals(event.getClass())).findFirst();
    return value.map(eventResolver -> (EventResolver<T>) eventResolver);
  }
}
