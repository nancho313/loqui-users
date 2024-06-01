package com.nancho313.loqui.users.test.repository;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.ContactStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dao.UserNeo4jDAO;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.ContactDto;
import com.nancho313.loqui.users.infrastructure.client.neo4j.node.UserNode;
import com.nancho313.loqui.users.infrastructure.mapper.ContactMapperImpl;
import com.nancho313.loqui.users.infrastructure.mapper.UserMapperImpl;
import com.nancho313.loqui.users.infrastructure.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

  private UserRepositoryImpl sut;

  private UserNeo4jDAO mockDao;

  @BeforeEach
  void setup() {

    mockDao = mock(UserNeo4jDAO.class);
    sut = new UserRepositoryImpl(mockDao, new UserMapperImpl(new ContactMapperImpl()));
  }

  @Test
  void saveOk() {

    // Arrange
    var userToSave = User.createUser(UserId.of("123"), "foo", "foo@foo.com");
    var userNodeToReturn = new UserNode(userToSave.getId().id(), userToSave.getUsername(), userToSave.getEmail(),
            List.of(), LocalDateTime.now().minusDays(1), LocalDateTime.now());
    when(mockDao.save(any())).thenReturn(userNodeToReturn);

    // Act
    var result = sut.save(userToSave);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContacts()).isEmpty();
    assertThat(result.getId().id()).isEqualTo(userNodeToReturn.id());
    assertThat(result.getEmail()).isEqualTo(userNodeToReturn.email());
    assertThat(result.getUsername()).isEqualTo(userNodeToReturn.username());
    assertThat(result.getCurrentDate().creationDate()).isEqualTo(userNodeToReturn.creationDate());
    assertThat(result.getCurrentDate().lastUpdatedDate()).isEqualTo(userNodeToReturn.lastUpdatedDate());

    var argCaptor = ArgumentCaptor.forClass(UserNode.class);
    verify(mockDao).save(argCaptor.capture());

    var capturedValue = argCaptor.getValue();
    assertThat(capturedValue).isNotNull();
    assertThat(capturedValue.id()).isEqualTo(userToSave.getId().id());
    assertThat(capturedValue.contacts()).isEmpty();
    assertThat(capturedValue.email()).isEqualTo(userToSave.getEmail());
    assertThat(capturedValue.username()).isEqualTo(userToSave.getUsername());
    assertThat(capturedValue.creationDate()).isEqualTo(userToSave.getCurrentDate().creationDate());
    assertThat(capturedValue.lastUpdatedDate()).isEqualTo(userToSave.getCurrentDate().lastUpdatedDate());
  }

  @Test
  void saveWithContactsOk() {

    // Arrange
    var userToSave = User.createUser(UserId.of("123"), "foo", "foo@foo.com").addContact(UserId.of("321"));
    var contactToSave = userToSave.getContacts().getFirst();
    var contactToReturn = new ContactDto("321", "AVAILABLE");
    var userNodeToReturn = new UserNode(userToSave.getId().id(), userToSave.getUsername(), userToSave.getEmail(),
            List.of(contactToReturn), LocalDateTime.now().minusDays(1), LocalDateTime.now());
    when(mockDao.save(any())).thenReturn(userNodeToReturn);

    // Act
    var result = sut.save(userToSave);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId().id()).isEqualTo(userNodeToReturn.id());
    assertThat(result.getEmail()).isEqualTo(userNodeToReturn.email());
    assertThat(result.getUsername()).isEqualTo(userNodeToReturn.username());
    assertThat(result.getCurrentDate().creationDate()).isEqualTo(userNodeToReturn.creationDate());
    assertThat(result.getCurrentDate().lastUpdatedDate()).isEqualTo(userNodeToReturn.lastUpdatedDate());
    assertThat(result.getContacts()).isNotEmpty().hasSize(1).allMatch(value -> value.status().name().equals(contactToReturn.status()));

    var argCaptor = ArgumentCaptor.forClass(UserNode.class);
    verify(mockDao).save(argCaptor.capture());

    var capturedValue = argCaptor.getValue();
    assertThat(capturedValue).isNotNull();
    assertThat(capturedValue.id()).isEqualTo(userToSave.getId().id());
    assertThat(capturedValue.email()).isEqualTo(userToSave.getEmail());
    assertThat(capturedValue.username()).isEqualTo(userToSave.getUsername());
    assertThat(capturedValue.creationDate()).isEqualTo(userToSave.getCurrentDate().creationDate());
    assertThat(capturedValue.lastUpdatedDate()).isEqualTo(userToSave.getCurrentDate().lastUpdatedDate());
    assertThat(capturedValue.contacts())
            .isNotEmpty()
            .hasSize(1)
            .allMatch(value -> value.status().equals(contactToSave.status().name()) && value.id().equals(contactToSave.id().id()));
  }

  @Test
  void findByIdOk() {

    // Arrange
    var id = "123";
    var userId = UserId.of(id);
    var userNodeToReturn = buildUserNode(id, "foo", "foo@foo.com");

    when(mockDao.findById(id)).thenReturn(Optional.of(userNodeToReturn));

    // Act
    var result = sut.findById(userId);

    // Assert
    assertThat(result).isNotNull().isPresent();
    assertThat(result.get().getContacts()).isEmpty();
    assertThat(result.get().getId().id()).isEqualTo(userNodeToReturn.id());
    assertThat(result.get().getEmail()).isEqualTo(userNodeToReturn.email());
    assertThat(result.get().getUsername()).isEqualTo(userNodeToReturn.username());
    assertThat(result.get().getCurrentDate().creationDate()).isEqualTo(userNodeToReturn.creationDate());
    assertThat(result.get().getCurrentDate().lastUpdatedDate()).isEqualTo(userNodeToReturn.lastUpdatedDate());
  }

  @Test
  void findByIdWithContactsOk() {

    // Arrange
    var id = "123";
    var userId = UserId.of(id);
    var contact = new ContactDto("1", "AVAILABLE");
    var userNodeToReturn = buildUserNode(id, "foo", "foo@foo.com", List.of(contact));

    when(mockDao.findById(id)).thenReturn(Optional.of(userNodeToReturn));

    // Act
    var result = sut.findById(userId);

    // Assert
    assertThat(result).isNotNull().isPresent();
    assertThat(result.get().getId().id()).isEqualTo(userNodeToReturn.id());
    assertThat(result.get().getEmail()).isEqualTo(userNodeToReturn.email());
    assertThat(result.get().getUsername()).isEqualTo(userNodeToReturn.username());
    assertThat(result.get().getCurrentDate().creationDate()).isEqualTo(userNodeToReturn.creationDate());
    assertThat(result.get().getCurrentDate().lastUpdatedDate()).isEqualTo(userNodeToReturn.lastUpdatedDate());

    assertThat(result.get().getContacts())
            .isNotEmpty()
            .hasSize(1)
            .allMatch(value -> value.id().id().equals(contact.id()) && value.status().equals(ContactStatus.valueOf(contact.status())));
  }

  @Test
  void findByIdReturnsEmpty() {

    // Arrange
    var id = "123";
    var userId = UserId.of(id);
    when(mockDao.findById(id)).thenReturn(Optional.empty());

    // Act
    var result = sut.findById(userId);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void existsByUsernameOk(boolean value) {

    // Arrange
    var username = "foo";
    when(mockDao.existsByUsername(username)).thenReturn(value);

    // Act
    var result = sut.existsByUsername(username);

    // Assert
    assertThat(result).isEqualTo(value);
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void existsByEmailOk(boolean value) {

    // Arrange
    var email = "foo@foo.com";
    when(mockDao.existsByEmail(email)).thenReturn(value);

    // Act
    var result = sut.existsByEmail(email);

    // Assert
    assertThat(result).isEqualTo(value);
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void existsByIdOk(boolean value) {

    // Arrange
    var id = "123";
    var userId = UserId.of(id);
    when(mockDao.existsById(id)).thenReturn(value);

    // Act
    var result = sut.existsById(userId);

    // Assert
    assertThat(result).isEqualTo(value);
  }

  @Test
  void addContactOk() {

    // Arrange
    var userId = UserId.of("123");
    var contactId = UserId.of("321");

    // Act
    sut.addContact(userId, contactId);

    // Assert
    var userIdCaptor = ArgumentCaptor.forClass(String.class);
    var contactIdCaptor = ArgumentCaptor.forClass(String.class);
    var statusCaptor = ArgumentCaptor.forClass(String.class);

    verify(mockDao).addContact(userIdCaptor.capture(), contactIdCaptor.capture(), statusCaptor.capture());

    assertThat(userIdCaptor.getValue()).isNotNull().isEqualTo(userId.id());
    assertThat(contactIdCaptor.getValue()).isNotNull().isEqualTo(contactId.id());
    assertThat(statusCaptor.getValue()).isNotNull().isEqualTo("AVAILABLE");
  }

  private UserNode buildUserNode(String id, String username, String email) {

    return buildUserNode(id, username, email, List.of());
  }

  private UserNode buildUserNode(String id, String username, String email, List<ContactDto> contacts) {

    return new UserNode(id, username, email, contacts, LocalDateTime.now().minusDays(1), LocalDateTime.now());
  }
}
