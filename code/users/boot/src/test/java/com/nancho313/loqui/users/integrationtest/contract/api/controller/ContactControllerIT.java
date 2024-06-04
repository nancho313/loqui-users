package com.nancho313.loqui.users.integrationtest.contract.api.controller;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import com.nancho313.loqui.users.integrationtest.contract.api.util.TestFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContactControllerIT extends BaseIntegrationTest {

  private static final TestFilter AUTH_TEST_FILTER = new TestFilter();

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Neo4jClient neo4jClient;

  @BeforeEach
  void setup() {

    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .addFilter(AUTH_TEST_FILTER, "/*")
        .build();
  }

  @AfterEach
  void tearDown() {

    neo4jClient.query("""
        MATCH (n)
        DETACH DELETE n
        """).run();
  }

  @Test
  void searchContactsOk() throws Exception {

    // Arrange
    var uri = URI.create("/v1/contact");
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    var userToSave2 = User.createUser(UserId.of("22222"), "foo2", "foo2@foo.com");
    userRepository.save(userToSave1);
    userRepository.save(userToSave2);
    userRepository.addContact(userToSave1.getId(), userToSave2.getId());

    // Act & Assert
    mockMvc.perform(get(uri)
            .header("test_user_id", userToSave1.getId().id())
            .header("test_username", userToSave1.getUsername()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "contacts": [
                {
                  "user": {
                    "id": "22222",
                    "username": "foo2",
                    "email": "foo2@foo.com"
                  },
                  "status": "AVAILABLE"
                }
              ]
            }"""));
  }
}
