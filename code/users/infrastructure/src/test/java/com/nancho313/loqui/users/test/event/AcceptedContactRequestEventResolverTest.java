package com.nancho313.loqui.users.test.event;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.kafka.emitter.LoquiKafkaEmitter;
import com.nancho313.loqui.users.infrastructure.event.AcceptedContactRequestEventResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AcceptedContactRequestEventResolverTest {

  private LoquiKafkaEmitter<AcceptedContactRequestEvent> emitterMock;

  private AcceptedContactRequestEventResolver sut;

  @BeforeEach
  void setup() {

    emitterMock = mock(LoquiKafkaEmitter.class);
    sut = new AcceptedContactRequestEventResolver(emitterMock);
  }

  @Test
  void processEventOk() {

    // Arrange
    var event = new ContactRequest.AcceptedContactRequestEvent(new ContactRequestId("123"), new UserId("111"), new UserId("222"), LocalDateTime.now());
    var expectedDateValue = event.creationDate().toInstant(ZoneOffset.UTC).toEpochMilli();

    // Act
    sut.processEvent(event);

    // Assert
    var argCaptorSentEvent = ArgumentCaptor.forClass(AcceptedContactRequestEvent.class);
    var argCaptorSentHeaders = ArgumentCaptor.forClass(List.class);
    verify(emitterMock).sendMessage(argCaptorSentEvent.capture(), argCaptorSentHeaders.capture());

    var sentEvent = argCaptorSentEvent.getValue();

    assertThat(sentEvent).isNotNull();
    assertThat(sentEvent.getContactRequestId()).hasToString(event.contactRequestId().id());
    assertThat(sentEvent.getRequesterUser()).hasToString(event.requesterUser().id());
    assertThat(sentEvent.getRequestedUser()).hasToString(event.requestedUser().id());
    assertThat(sentEvent.getDate()).isEqualTo(expectedDateValue);
  }

  @Test
  void getTypeOk() {

    // Act
    var result = sut.getType();

    // Assert
    assertThat(result).isEqualTo(ContactRequest.AcceptedContactRequestEvent.class);
  }
}
