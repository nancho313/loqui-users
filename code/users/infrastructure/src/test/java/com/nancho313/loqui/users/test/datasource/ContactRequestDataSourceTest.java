package com.nancho313.loqui.users.test.datasource;

import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.ContactRequestMongodbDAO;
import com.nancho313.loqui.users.infrastructure.client.mongodb.document.ContactRequestDocument;
import com.nancho313.loqui.users.infrastructure.datasource.ContactRequestDataSourceImpl;
import com.nancho313.loqui.users.infrastructure.mapper.ContactRequestMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContactRequestDataSourceTest {

  private ContactRequestDataSourceImpl sut;

  private ContactRequestMongodbDAO mockDao;

  @BeforeEach
  void setup() {

    mockDao = mock(ContactRequestMongodbDAO.class);
    sut = new ContactRequestDataSourceImpl(mockDao, new ContactRequestMapperImpl());
  }

  @Test
  void getContactRequestsOk() {

    // Arrange
    String idUser = UUID.randomUUID().toString();
    String status = "FOO_STATUS";
    ContactRequestDocument document = new ContactRequestDocument(UUID.randomUUID().toString(),
            idUser, UUID.randomUUID().toString(),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(), status, "Greetings!");
    List<ContactRequestDocument> documentsToReturn = List.of(document);
    when(mockDao.findByIdUserAndStatus(eq(idUser), eq(status))).thenReturn(documentsToReturn);

    // Act
    var result = sut.getContactRequests(idUser, status);

    // Assert
    assertThat(result)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);

    var contactRequestToValidate = result.getFirst();

    assertThat(contactRequestToValidate.id()).isEqualTo(document.id());
    assertThat(contactRequestToValidate.requestedUser()).isEqualTo(document.requestedUser());
    assertThat(contactRequestToValidate.requesterUser()).isEqualTo(document.requesterUser());
    assertThat(contactRequestToValidate.message()).isEqualTo(document.message());
    assertThat(contactRequestToValidate.status()).isEqualTo(document.status());
    assertThat(contactRequestToValidate.creationDate()).isEqualTo(document.creationDate());
    assertThat(contactRequestToValidate.lastUpdatedDate()).isEqualTo(document.lastUpdatedDate());

  }

  @Test
  void getContactRequestsReturnsEmpty() {

    // Arrange
    String idUser = UUID.randomUUID().toString();
    String status = "FOO_STATUS";
    when(mockDao.findByIdUserAndStatus(eq(idUser), eq(status))).thenReturn(Collections.emptyList());

    // Act
    var result = sut.getContactRequests(idUser, status);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }
}
