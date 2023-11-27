package com.nancho313.loqui.users.integrationtest.infrastructure.repository;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.entity.Contact;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIT extends BaseIntegrationTest {
  
  @Autowired
  private UserRepository sut;
  
  @Autowired
  private Neo4jClient neo4jClient;
  
  @AfterEach
  void tearDown() {
    
    neo4jClient.query("""
            MATCH (n)
            DETACH DELETE n
            """).run();
  }
  
  @Test
  void saveUserOk() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    
    // Act
    var result = sut.save(user);
    
    // Assert
    assertThat(result.getId()).isEqualTo(user.getId());
    assertThat(result.getEmail()).isEqualTo(user.getEmail());
    assertThat(result.getUsername()).isEqualTo(user.getUsername());
  }
  
  @Test
  void updateUserOk() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    sut.save(user);
    User userToUpdate = User.createUser(user.getId(), "foo2", "foo2@email.com");
    
    // Act
    var result = sut.save(userToUpdate);
    
    // Assert
    assertThat(result.getId()).isEqualTo(userToUpdate.getId());
    assertThat(result.getEmail()).isEqualTo(userToUpdate.getEmail());
    assertThat(result.getUsername()).isEqualTo(userToUpdate.getUsername());
  }
  
  @Test
  void findByIdOk() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    sut.save(user);
    
    // Act
    var result = sut.findById(user.getId());
    
    // Assert
    assertThat(result).isPresent();
    var currentUser = result.get();
    assertThat(currentUser.getId()).isEqualTo(user.getId());
    assertThat(currentUser.getEmail()).isEqualTo(user.getEmail());
    assertThat(currentUser.getUsername()).isEqualTo(user.getUsername());
  }
  
  @Test
  void findByIdReturnsEmptyData() {
    
    // Arrange
    UserId id = UserId.of(UUID.randomUUID().toString());
    
    // Act
    var result = sut.findById(id);
    
    // Assert
    assertThat(result).isEmpty();
  }
  
  @Test
  void existsByUsernameReturnsTrue() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    sut.save(user);
    
    // Act
    var result = sut.existsByUsername(user.getUsername());
    
    // Assert
    assertThat(result).isTrue();
  }
  
  @Test
  void existsByUsernameReturnsFalse() {
    
    // Arrange
    String username = "foo";
    
    // Act
    var result = sut.existsByUsername(username);
    
    // Assert
    assertThat(result).isFalse();
  }
  
  @Test
  void existsByEmailReturnsTrue() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    sut.save(user);
    
    // Act
    var result = sut.existsByEmail(user.getEmail());
    
    // Assert
    assertThat(result).isTrue();
  }
  
  @Test
  void existsByEmailReturnsFalse() {
    
    // Arrange
    String email = "foo@email.com";
    
    // Act
    var result = sut.existsByEmail(email);
    
    // Assert
    assertThat(result).isFalse();
  }
  
  @Test
  void existsByIdReturnsTrue() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    sut.save(user);
    
    // Act
    var result = sut.existsById(user.getId());
    
    // Assert
    assertThat(result).isTrue();
  }
  
  @Test
  void existsByIdReturnsFalse() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    
    // Act
    var result = sut.existsById(id);
    
    // Assert
    assertThat(result).isFalse();
  }
  
  @Test
  void addContactOk() {
    
    // Arrange
    User user1 = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo1", "foo1@email.com");
    User user2 = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo2", "foo2@email.com");
    sut.save(user1);
    sut.save(user2);
    
    // Act
    sut.addContact(user1.getId(), user2.getId());
    
    // Assert
    var actualUser1 = sut.findById(user1.getId());
    assertThat(actualUser1).isPresent();
    assertThat(actualUser1.get().getContacts().stream().map(Contact::id)).containsExactly(user2.getId());
  }
}
