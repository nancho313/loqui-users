package com.nancho313.loqui.users.integrationtest.contract.api.controller;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.ContactRequestMongodbDAO;
import com.nancho313.loqui.users.infrastructure.client.mongodb.document.ContactRequestDocument;
import com.nancho313.loqui.users.integrationtest.BaseIntegrationTest;
import com.nancho313.loqui.users.integrationtest.contract.api.util.TestFilter;
import com.nancho313.loqui.users.integrationtest.util.KafkaMessageCaptor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class ContactControllerIT extends BaseIntegrationTest {

  private static final TestFilter AUTH_TEST_FILTER = new TestFilter();

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRequestMongodbDAO contactRequestMongodbDAO;

  @Autowired
  private Neo4jClient neo4jClient;

  @Autowired
  private KafkaMessageCaptor<AcceptedContactRequestEvent> messageCaptor;

  private MockMvc mockMvc;

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
            DETACH DELETE n""").run();

    contactRequestMongodbDAO.deleteAll();
    messageCaptor.cleanMessages();
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

  @Test
  void searchContactsReturnsEmptyData() throws Exception {

    // Arrange
    var uri = URI.create("/v1/contact");
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    userRepository.save(userToSave1);

    // Act & Assert
    mockMvc.perform(get(uri)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json("""
                    {
                      "contacts": []
                    }"""));
  }

  @Test
  void addNewContactOk() throws Exception {

    // Arrange
    var uri = URI.create("/v1/contact/request");
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    var userToSave2 = User.createUser(UserId.of("22222"), "foo2", "foo2@foo.com");
    userRepository.save(userToSave1);
    userRepository.save(userToSave2);

    var payload = """
            {
              "contactId": 22222,
              "initialMessage": "Greetings!!!!"
            }
            """;

    // Act & Assert
    mockMvc.perform(post(uri)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isNoContent());

    var allContactRequests = contactRequestMongodbDAO.findAll();
    assertThat(allContactRequests).isNotNull().hasSize(1);
    var storedContactRequest = allContactRequests.getFirst();
    assertThat(storedContactRequest.message()).isEqualTo("Greetings!!!!");
    assertThat(storedContactRequest.status()).isEqualTo("PENDING");
    assertThat(storedContactRequest.requesterUser()).isEqualTo(userToSave1.getId().id());
    assertThat(storedContactRequest.requestedUser()).isEqualTo(userToSave2.getId().id());
  }

  @Test
  void processContactRequestAcceptOk() throws Exception {

    // Arrange
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    var userToSave2 = User.createUser(UserId.of("22222"), "foo2", "foo2@foo.com");
    userRepository.save(userToSave1);
    userRepository.save(userToSave2);
    ContactRequestDocument document = buildContactRequestDocument(userToSave2.getId().id(), userToSave1.getId().id(), "PENDING");
    contactRequestMongodbDAO.save(document);
    var uri = URI.create("/v1/contact/request/" + document.id());

    var payload = """
            {
              "accept" : true
            }
            """;

    // Act & Assert
    mockMvc.perform(post(uri)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isNoContent());

    var allContactRequests = contactRequestMongodbDAO.findAll();
    assertThat(allContactRequests).isNotNull().hasSize(1);
    var storedContactRequest = allContactRequests.getFirst();
    assertThat(storedContactRequest.status()).isEqualTo("ACCEPTED");

    await().atMost(3, TimeUnit.SECONDS).until(()-> !messageCaptor.getCapturedMessages().isEmpty());

    var sentMessages = messageCaptor.getCapturedMessages();
    assertThat(sentMessages).isNotNull().hasSize(1);
    var sentMessage = sentMessages.getFirst().getPayload();
    assertThat(sentMessage).isNotNull();
    assertThat(sentMessage.getContactRequestId()).hasToString(document.id());
    assertThat(sentMessage.getRequestedUser()).hasToString(document.requestedUser());
    assertThat(sentMessage.getRequesterUser()).hasToString(document.requesterUser());
  }

  @Test
  void processContactRequestRejectOk() throws Exception {

    // Arrange
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    var userToSave2 = User.createUser(UserId.of("22222"), "foo2", "foo2@foo.com");
    userRepository.save(userToSave1);
    userRepository.save(userToSave2);
    ContactRequestDocument document = buildContactRequestDocument(userToSave2.getId().id(), userToSave1.getId().id(), "PENDING");
    contactRequestMongodbDAO.save(document);
    var uri = URI.create("/v1/contact/request/" + document.id());

    var payload = """
            {
              "accept" : false
            }
            """;

    // Act & Assert
    mockMvc.perform(post(uri)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isNoContent());

    var allContactRequests = contactRequestMongodbDAO.findAll();
    assertThat(allContactRequests).isNotNull().hasSize(1);
    var storedContactRequest = allContactRequests.getFirst();
    assertThat(storedContactRequest.status()).isEqualTo("REJECTED");
  }

  @Test
  void processContactRequestAcceptARejectedRequestThrowsBadRequest() throws Exception {

    // Arrange
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    var userToSave2 = User.createUser(UserId.of("22222"), "foo2", "foo2@foo.com");
    userRepository.save(userToSave1);
    userRepository.save(userToSave2);
    ContactRequestDocument document = buildContactRequestDocument(userToSave2.getId().id(), userToSave1.getId().id(), "REJECTED");
    contactRequestMongodbDAO.save(document);
    var uri = URI.create("/v1/contact/request/" + document.id());

    var payload = """
            {
              "accept" : true
            }
            """;

    // Act & Assert
    mockMvc.perform(post(uri)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().json("""
                    {
                      "message": "Invalid status permutation. The ContactRequest %s cannot change from REJECTED to ACCEPTED."
                    }""".formatted(document.id())));

    var allContactRequests = contactRequestMongodbDAO.findAll();
    assertThat(allContactRequests).isNotNull().hasSize(1);
    var storedContactRequest = allContactRequests.getFirst();
    assertThat(storedContactRequest.status()).isEqualTo("REJECTED");
  }

  @Test
  void getContactRequestsOk() throws Exception {

    // Arrange
    var uri = URI.create("/v1/contact/request");
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    var userToSave2 = User.createUser(UserId.of("22222"), "foo2", "foo2@foo.com");
    var userToSave3 = User.createUser(UserId.of("33333"), "foo3", "foo3@foo.com");
    userRepository.save(userToSave1);
    userRepository.save(userToSave2);
    userRepository.save(userToSave3);
    ContactRequestDocument document1 = buildContactRequestDocument(userToSave2.getId().id(), userToSave1.getId().id(), "PENDING");
    ContactRequestDocument document2 = buildContactRequestDocument(userToSave1.getId().id(), userToSave3.getId().id(), "PENDING");
    contactRequestMongodbDAO.save(document1);
    contactRequestMongodbDAO.save(document2);

    // Act & Assert
    mockMvc.perform(get(uri)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json("""
                    {
                      "sentRequests": [
                        {
                          "requestedUser": "33333",
                          "requesterUser": "11111",
                          "message": "Greetings"
                        }
                      ],
                      "receivedRequests": [
                        {
                          "requestedUser": "11111",
                          "requesterUser": "22222",
                          "message": "Greetings"
                        }
                      ]
                    }"""));
  }

  @Test
  void getContactRequestsReturnsEmptyData() throws Exception {

    // Arrange
    var uri = URI.create("/v1/contact/request");
    var userToSave1 = User.createUser(UserId.of("11111"), "foo1", "foo1@foo.com");
    userRepository.save(userToSave1);

    // Act & Assert
    mockMvc.perform(get(uri)
                    .header("test_user_id", userToSave1.getId().id())
                    .header("test_username", userToSave1.getUsername()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sentRequests").isEmpty())
            .andExpect(jsonPath("$.receivedRequests").isEmpty());
  }

  private ContactRequestDocument buildContactRequestDocument(String requesterUser, String requestedUser, String status) {
    return new ContactRequestDocument(new ObjectId().toHexString(), requesterUser, requestedUser, LocalDateTime.now(), LocalDateTime.now(), status, "Greetings");
  }
}
