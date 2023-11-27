package com.nancho313.loqui.users.integrationtest.infrastructure.datasource;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserDataSourceIT extends BaseIntegrationTest {
  
  @Autowired
  private UserDataSource sut;
  
  @Autowired
  private UserRepository userRepository;
  
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
  void searchUsersByUsernameOk() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    userRepository.save(user);
    
    // Act
    var result = sut.searchUsersByUsername("foo");
    
    // Assert
    assertThat(result).isNotEmpty().hasSize(1);
    var userProjection = result.get(0);
    assertThat(userProjection.username()).isEqualTo(user.getUsername());
    assertThat(userProjection.email()).isEqualTo(user.getEmail());
    assertThat(userProjection.id()).isEqualTo(user.getId().id());
  }
  
  @Test
  void searchUsersByUsernameReturnsEmptyData() {
    
    // Arrange
    String username = "foo";
    
    // Act
    var result = sut.searchUsersByUsername(username);
    
    // Assert
    assertThat(result).isEmpty();
  }
  
  @Test
  void searchUsersByEmailOk() {
    
    // Arrange
    User user = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo", "foo@email.com");
    userRepository.save(user);
    
    // Act
    var result = sut.searchUsersByEmail("foo@email.com");
    
    // Assert
    assertThat(result).isNotEmpty().hasSize(1);
    var userProjection = result.get(0);
    assertThat(userProjection.username()).isEqualTo(user.getUsername());
    assertThat(userProjection.email()).isEqualTo(user.getEmail());
    assertThat(userProjection.id()).isEqualTo(user.getId().id());
  }
  
  @Test
  void searchUsersByEmailReturnsEmptyData() {
    
    // Arrange
    String username = "foo@email.com";
    
    // Act
    var result = sut.searchUsersByEmail(username);
    
    // Assert
    assertThat(result).isEmpty();
  }
  
  @Test
  void searchContactsOk() {
    
    // Arrange
    User user1 = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo1", "foo1@email.com");
    User user2 = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo2", "foo2@email.com");
    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.addContact(user1.getId(), user2.getId());
    
    // Act
    var result = sut.searchContacts(user1.getId().id());
    
    // Assert
    assertThat(result).isNotEmpty().hasSize(1);
    var contactProjection = result.get(0);
    assertThat(contactProjection.username()).isEqualTo(user2.getUsername());
    assertThat(contactProjection.email()).isEqualTo(user2.getEmail());
    assertThat(contactProjection.id()).isEqualTo(user2.getId().id());
  }
  
  @Test
  void searchContactsReturnsEmptyData() {
    
    // Arrange
    User user1 = User.createUser(UserId.of(UUID.randomUUID().toString()), "foo1", "foo1@email.com");
    userRepository.save(user1);
    
    
    // Act
    var result = sut.searchContacts(user1.getId().id());
    
    // Assert
    assertThat(result).isEmpty();
  }
  
}