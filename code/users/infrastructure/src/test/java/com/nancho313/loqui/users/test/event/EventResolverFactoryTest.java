package com.nancho313.loqui.users.test.event;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.event.EventResolver;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.event.AcceptedContactRequestEventResolver;
import com.nancho313.loqui.users.infrastructure.event.EventResolverFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventResolverFactoryTest {

  private List<EventResolver<?>> currentEventResolvers;

  private EventResolverFactoryImpl sut;

  @BeforeEach
  void setup() {

    currentEventResolvers = new ArrayList<>();
    sut = new EventResolverFactoryImpl(currentEventResolvers);
  }

  @Test
  void getResolverOk() {

    // Arrange
    var eventResolver = new AcceptedContactRequestEventResolver(null);
    currentEventResolvers.add(eventResolver);
    var event = new ContactRequest.AcceptedContactRequestEvent(new ContactRequestId("123"), new UserId("111"), new UserId("222"), LocalDateTime.now());

    // Act
    var result = sut.getResolver(event);

    // Assert
    assertThat(result).isNotNull().isPresent();
    assertThat(result.get().getType()).isEqualTo(eventResolver.getType());
  }

  @Test
  void getResolverReturnsEmpty() {

    // Arrange
    var event = new ContactRequest.AcceptedContactRequestEvent(new ContactRequestId("123"), new UserId("111"), new UserId("222"), LocalDateTime.now());

    // Act
    var result = sut.getResolver(event);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void getResolverReturnsEmptyWithNonEmptyResolvers() {

    // Arrange
    currentEventResolvers.add(new EventResolverUtilTest());
    var event = new ContactRequest.AcceptedContactRequestEvent(new ContactRequestId("123"), new UserId("111"), new UserId("222"), LocalDateTime.now());

    // Act
    var result = sut.getResolver(event);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }

  private class DomainEventUtilTest implements DomainEvent {
  }

  private class EventResolverUtilTest implements EventResolver<DomainEventUtilTest> {
    @Override
    public void processEvent(DomainEventUtilTest domainEventUtilTest) {
    }

    @Override
    public Class<DomainEventUtilTest> getType() {
      return DomainEventUtilTest.class;
    }
  }

}
