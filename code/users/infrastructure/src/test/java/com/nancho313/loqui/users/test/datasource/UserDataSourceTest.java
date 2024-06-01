package com.nancho313.loqui.users.test.datasource;

import com.nancho313.loqui.users.infrastructure.client.neo4j.dao.UserNeo4jDAO;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.ContactDto;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.UserContactDto;
import com.nancho313.loqui.users.infrastructure.client.neo4j.node.UserNode;
import com.nancho313.loqui.users.infrastructure.datasource.UserDataSourceImpl;
import com.nancho313.loqui.users.infrastructure.mapper.ContactMapperImpl;
import com.nancho313.loqui.users.infrastructure.mapper.UserMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDataSourceTest {

  private UserNeo4jDAO stubDao;

  private UserDataSourceImpl sut;

  @BeforeEach
  void setup() {

    stubDao = mock(UserNeo4jDAO.class);
    var contactMapper = new ContactMapperImpl();
    var userMapper = new UserMapperImpl(contactMapper);
    sut = new UserDataSourceImpl(stubDao, userMapper, contactMapper);
  }

  @Test
  void searchUsersByUsernameOk() {

    // Arrange
    var username = "foo1";
    UserNode userNode = buildUserNode(username, "foo1@foo.com", new ArrayList<>());
    when(stubDao.searchUsersByUsername(eq(username))).thenReturn(List.of(userNode));

    // Act
    var result = sut.searchUsersByUsername(username);

    // Assert
    assertThat(result).isNotNull().hasSize(1);

    var userToValidate = result.getFirst();
    assertThat(userToValidate.id()).isEqualTo(userNode.id());
    assertThat(userToValidate.username()).isEqualTo(userNode.username());
    assertThat(userToValidate.email()).isEqualTo(userNode.email());
  }

  @Test
  void searchUsersByUsernameReturnsEmpty() {

    // Arrange
    var username = "foo1";
    when(stubDao.searchUsersByUsername(eq(username))).thenReturn(List.of());

    // Act
    var result = sut.searchUsersByUsername(username);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void searchUsersByEmailOk() {

    // Arrange
    var email = "foo1@foo.com";
    UserNode userNode = buildUserNode("foo", email, new ArrayList<>());
    when(stubDao.searchUsersByEmail(eq(email))).thenReturn(List.of(userNode));

    // Act
    var result = sut.searchUsersByEmail(email);

    // Assert
    assertThat(result).isNotNull().hasSize(1);

    var userToValidate = result.getFirst();
    assertThat(userToValidate.id()).isEqualTo(userNode.id());
    assertThat(userToValidate.username()).isEqualTo(userNode.username());
    assertThat(userToValidate.email()).isEqualTo(userNode.email());
  }

  @Test
  void searchUsersByEmailReturnsEmpty() {

    // Arrange
    var email = "foo1@foo.com";
    when(stubDao.searchUsersByEmail(eq(email))).thenReturn(List.of());

    // Act
    var result = sut.searchUsersByEmail(email);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void searchContactsOk() {

    // Arrange
    var idUser = UUID.randomUUID().toString();
    var contact = new UserContactDto(idUser, "foo", "foo@foo.com", "STATUS");
    when(stubDao.searchContactsFromUser(idUser)).thenReturn(List.of(contact));

    // Act
    var result = sut.searchContacts(idUser);

    // Assert
    assertThat(result).isNotNull().hasSize(1);

    var contactToValidate = result.getFirst();
    assertThat(contactToValidate.id()).isEqualTo(contact.id());
    assertThat(contactToValidate.status()).isEqualTo(contact.status());
    assertThat(contactToValidate.email()).isEqualTo(contact.email());
    assertThat(contactToValidate.username()).isEqualTo(contact.username());
  }

  @Test
  void searchContactsReturnsEmpty() {

    // Arrange
    var idUser = UUID.randomUUID().toString();
    when(stubDao.searchContactsFromUser(idUser)).thenReturn(List.of());

    // Act
    var result = sut.searchContacts(idUser);

    // Assert
    assertThat(result).isNotNull().isEmpty();
  }

  private UserNode buildUserNode(String username, String email, List<ContactDto> contacts) {

    return new UserNode(UUID.randomUUID().toString(), username, email, contacts, LocalDateTime.now().minusDays(1), LocalDateTime.now());
  }
}
