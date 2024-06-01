package com.nancho313.loqui.users.test.repository;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.ContactRequestMongodbDAO;
import com.nancho313.loqui.users.infrastructure.client.mongodb.document.ContactRequestDocument;
import com.nancho313.loqui.users.infrastructure.externalservice.IdGeneratorImpl;
import com.nancho313.loqui.users.infrastructure.mapper.ContactRequestMapperImpl;
import com.nancho313.loqui.users.infrastructure.repository.ContactRequestRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ContactRequestRepositoryTest {

  private ContactRequestMongodbDAO mockDao;

  private ContactRequestRepositoryImpl sut;

  @BeforeEach
  void setup() {

    mockDao = mock(ContactRequestMongodbDAO.class);
    sut = new ContactRequestRepositoryImpl(mockDao, new ContactRequestMapperImpl());
  }


  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void existsPendingRequest(boolean value) {

    // Arrange
    var requesterUser = "user_1";
    var requestedUser = "user_2";
    var requesterId = UserId.of(requesterUser);
    var requestedId = UserId.of(requestedUser);
    when(mockDao.existsByRequesterUserAndRequestedUserAndStatus(eq(requesterUser), eq(requestedUser), eq("PENDING"))).thenReturn(value);

    // Act
    var result = sut.existsPendingRequest(requesterId, requestedId);

    // Assert
    assertThat(result).isEqualTo(value);
  }

  @Test
  void saveOk() {

    // Arrange
    var requesterUser = "user_1";
    var requestedUser = "user_2";
    var message = "Greetings";
    var requesterId = UserId.of(requesterUser);
    var requestedId = UserId.of(requestedUser);
    var contactRequestToStore = ContactRequest.create(new IdGeneratorImpl(), requesterId, requestedId, message);

    ContactRequestDocument documentToReturn = new ContactRequestDocument(contactRequestToStore.getContactRequestId().id(),
            requesterUser, requestedUser, contactRequestToStore.getCurrentDate().creationDate(),
            contactRequestToStore.getCurrentDate().lastUpdatedDate(), contactRequestToStore.getStatus().name(), message);

    when(mockDao.save(any())).thenReturn(documentToReturn);

    // Act
    var result = sut.save(contactRequestToStore);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContactRequestId().id()).isEqualTo(documentToReturn.id());
    assertThat(result.getRequestedUser().id()).isEqualTo(documentToReturn.requestedUser());
    assertThat(result.getRequesterUser().id()).isEqualTo(documentToReturn.requesterUser());
    assertThat(result.getStatus()).isEqualTo(ContactRequestStatus.valueOf(documentToReturn.status()));
    assertThat(result.getMessage()).isEqualTo(documentToReturn.message());
    assertThat(result.getCurrentDate().creationDate()).isEqualTo(documentToReturn.creationDate());
    assertThat(result.getCurrentDate().lastUpdatedDate()).isEqualTo(documentToReturn.lastUpdatedDate());

    var argCaptor = ArgumentCaptor.forClass(ContactRequestDocument.class);
    verify(mockDao).save(argCaptor.capture());
    var storedValue = argCaptor.getValue();

    assertThat(storedValue).isNotNull();
    assertThat(storedValue.id()).isEqualTo(contactRequestToStore.getContactRequestId().id());
    assertThat(storedValue.requesterUser()).isEqualTo(requesterUser);
    assertThat(storedValue.requestedUser()).isEqualTo(requestedUser);
    assertThat(storedValue.message()).isEqualTo(message);
    assertThat(storedValue.status()).isEqualTo(contactRequestToStore.getStatus().name());
    assertThat(storedValue.creationDate()).isEqualTo(contactRequestToStore.getCurrentDate().creationDate());
    assertThat(storedValue.lastUpdatedDate()).isEqualTo(contactRequestToStore.getCurrentDate().lastUpdatedDate());
  }

  @Test
  void findByIdOk() {

    // Arrange
    var id = "123";
    var contactRequestId = ContactRequestId.of(id);
    ContactRequestDocument documentToReturn = new ContactRequestDocument(id,
            "111", "222", LocalDateTime.now().minusDays(1),
            LocalDateTime.now(), ContactRequestStatus.PENDING.name(), "Greetings");

    when(mockDao.findById(eq(id))).thenReturn(Optional.of(documentToReturn));

    // Act
    var result = sut.findById(contactRequestId);

    // Assert
    assertThat(result).isNotNull().isPresent();
    assertThat(result.get().getContactRequestId().id()).isEqualTo(documentToReturn.id());
    assertThat(result.get().getRequesterUser().id()).isEqualTo(documentToReturn.requesterUser());
    assertThat(result.get().getRequestedUser().id()).isEqualTo(documentToReturn.requestedUser());
    assertThat(result.get().getStatus()).isEqualTo(ContactRequestStatus.valueOf(documentToReturn.status()));
    assertThat(result.get().getMessage()).isEqualTo(documentToReturn.message());
    assertThat(result.get().getCurrentDate().creationDate()).isEqualTo(documentToReturn.creationDate());
    assertThat(result.get().getCurrentDate().lastUpdatedDate()).isEqualTo(documentToReturn.lastUpdatedDate());
  }

  @Test
  void findByIdReturnsEmpty() {

    // Arrange
    var id = "123";
    var contactRequestId = ContactRequestId.of(id);
    when(mockDao.findById(eq(id))).thenReturn(Optional.empty());

    // Act
    var result = sut.findById(contactRequestId);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }
}
