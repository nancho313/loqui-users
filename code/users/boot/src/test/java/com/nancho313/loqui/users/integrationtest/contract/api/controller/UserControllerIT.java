package com.nancho313.loqui.users.integrationtest.contract.api.controller;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
public class UserControllerIT extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

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
  void searchUsersUsingUsernameOk() throws Exception {

    // Arrange
    var uri = URI.create("/v1/user?username=foo");
    var userToSave = User.createUser(UserId.of("123456"), "foo", "foo@foo.com");
    userRepository.save(userToSave);

    // Act & Assert
    mockMvc.perform(get(uri))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "users": [
                {
                  "id": "123456",
                  "username": "foo",
                  "email": "foo@foo.com"
                }
              ]
            }"""));
  }

  @Test
  void searchUsersUsingEmailOk() throws Exception {

    // Arrange
    var uri = URI.create("/v1/user?email=foo@foo.com");
    var userToSave = User.createUser(UserId.of("123456"), "foo", "foo@foo.com");
    userRepository.save(userToSave);

    // Act & Assert
    mockMvc.perform(get(uri))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "users": [
                {
                  "id": "123456",
                  "username": "foo",
                  "email": "foo@foo.com"
                }
              ]
            }"""));
  }

  @Test
  void searchUsersReturnsEmptyValue() throws Exception {

    // Arrange
    var uri = URI.create("/v1/user?username=foo");

    // Act & Assert
    mockMvc.perform(get(uri))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "users": []
            }"""));
  }
}
