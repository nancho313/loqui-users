package com.nancho313.loqui.users.test.contract.api.controller;

import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.contactrequest.command.AddNewContactCommand;
import com.nancho313.loqui.users.application.command.contactrequest.command.ProcessContactRequestCommand;
import com.nancho313.loqui.users.application.query.contactrequest.dto.ContactRequestDataDto;
import com.nancho313.loqui.users.application.query.contactrequest.query.GetPendingContactRequestsQuery;
import com.nancho313.loqui.users.application.query.contactrequest.response.GetPendingContactRequestsQueryResponse;
import com.nancho313.loqui.users.application.query.user.dto.ContactResultDto;
import com.nancho313.loqui.users.application.query.user.dto.UserResultDto;
import com.nancho313.loqui.users.application.query.user.query.SearchContactsQuery;
import com.nancho313.loqui.users.application.query.user.response.SearchContactsQueryResponse;
import com.nancho313.loqui.users.contract.api.config.AuthUser;
import com.nancho313.loqui.users.contract.api.controller.ContactController;
import com.nancho313.loqui.users.contract.api.dto.AddNewContactApiRequest;
import com.nancho313.loqui.users.contract.api.dto.ProcessContactRequestApiRequest;
import com.nancho313.loqui.users.test.contract.util.CommandHandlerTestUtil;
import com.nancho313.loqui.users.test.contract.util.QueryHandlerTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContactControllerTest {

  private ContactController sut;

  private CommandHandlerTestUtil<AddNewContactCommand, EmptyCommandResponse> addNewContactCommandHandlerSpy;

  private CommandHandlerTestUtil<ProcessContactRequestCommand, EmptyCommandResponse> processContactRequestCommandHandlerSpy;

  private QueryHandlerTestUtil<SearchContactsQuery, SearchContactsQueryResponse> searchContactsCommandHandlerSpy;

  private QueryHandlerTestUtil<GetPendingContactRequestsQuery, GetPendingContactRequestsQueryResponse> getPendingContactRequestsQueryHandlerSpy;

  @BeforeEach
  void setup() {

    addNewContactCommandHandlerSpy = new CommandHandlerTestUtil<>();
    processContactRequestCommandHandlerSpy = new CommandHandlerTestUtil<>();
    searchContactsCommandHandlerSpy = new QueryHandlerTestUtil<>();
    getPendingContactRequestsQueryHandlerSpy = new QueryHandlerTestUtil<>();
    sut = new ContactController(addNewContactCommandHandlerSpy, processContactRequestCommandHandlerSpy, searchContactsCommandHandlerSpy, getPendingContactRequestsQueryHandlerSpy);
  }

  @Test
  void searchContactsOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var user = new UserResultDto(UUID.randomUUID().toString(), "foo2", "foo2@foo.com");
    var contact = new ContactResultDto(user, "ACCEPTED");
    var contacts = List.of(contact);
    var response = new SearchContactsQueryResponse(contacts);
    searchContactsCommandHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.searchContacts(authUser);

    // Assert
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().contacts()).isNotNull().hasSize(1);
    var contactToValidate = result.getBody().contacts().getFirst();
    assertThat(contactToValidate.status()).isEqualTo(contact.status());
    assertThat(contactToValidate.user().id()).isEqualTo(contact.user().id());
    assertThat(contactToValidate.user().email()).isEqualTo(contact.user().email());
    assertThat(contactToValidate.user().username()).isEqualTo(contact.user().username());

    var queryToExecute = searchContactsCommandHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.userId()).isEqualTo(userId);
  }

  @Test
  void searchContactsReturnsEmpty() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var response = new SearchContactsQueryResponse(new ArrayList<>());
    searchContactsCommandHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.searchContacts(authUser);

    // Assert
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().contacts()).isNotNull().isEmpty();

    var queryToExecute = searchContactsCommandHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.userId()).isEqualTo(userId);
  }

  @Test
  void getContactRequestsOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var sentRequest = buildContactRequest(userId, UUID.randomUUID().toString());
    var sentRequests = List.of(sentRequest);
    var receivedRequest = buildContactRequest(UUID.randomUUID().toString(), userId);
    var receivedRequests = List.of(receivedRequest);

    var response = new GetPendingContactRequestsQueryResponse(sentRequests, receivedRequests);
    getPendingContactRequestsQueryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.getContactRequests(authUser);

    // Assert
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();

    assertThat(result.getBody().receivedRequests()).isNotNull().hasSize(1);
    var receivedRequestToValidate = result.getBody().receivedRequests().getFirst();
    assertThat(receivedRequestToValidate.requestedUser()).isEqualTo(receivedRequest.requestedUser());
    assertThat(receivedRequestToValidate.requesterUser()).isEqualTo(receivedRequest.requesterUser());
    assertThat(receivedRequestToValidate.id()).isEqualTo(receivedRequest.id());
    assertThat(receivedRequestToValidate.creationDate()).isEqualTo(receivedRequest.creationDate());
    assertThat(receivedRequestToValidate.message()).isEqualTo(receivedRequest.message());

    assertThat(result.getBody().sentRequests()).isNotNull().hasSize(1);
    var sentRequestToValidate = result.getBody().sentRequests().getFirst();
    assertThat(sentRequestToValidate.requestedUser()).isEqualTo(sentRequest.requestedUser());
    assertThat(sentRequestToValidate.requesterUser()).isEqualTo(sentRequest.requesterUser());
    assertThat(sentRequestToValidate.id()).isEqualTo(sentRequest.id());
    assertThat(sentRequestToValidate.creationDate()).isEqualTo(sentRequest.creationDate());
    assertThat(sentRequestToValidate.message()).isEqualTo(sentRequest.message());

    var queryToExecute = getPendingContactRequestsQueryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.userId()).isEqualTo(userId);
  }

  @Test
  void getContactRequestsWithOnlySentRequestsOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var sentRequest = buildContactRequest(userId, UUID.randomUUID().toString());
    var sentRequests = List.of(sentRequest);

    var response = new GetPendingContactRequestsQueryResponse(sentRequests, List.of());
    getPendingContactRequestsQueryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.getContactRequests(authUser);

    // Assert
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();

    assertThat(result.getBody().receivedRequests()).isNotNull().isEmpty();

    assertThat(result.getBody().sentRequests()).isNotNull().hasSize(1);
    var sentRequestToValidate = result.getBody().sentRequests().getFirst();
    assertThat(sentRequestToValidate.requestedUser()).isEqualTo(sentRequest.requestedUser());
    assertThat(sentRequestToValidate.requesterUser()).isEqualTo(sentRequest.requesterUser());
    assertThat(sentRequestToValidate.id()).isEqualTo(sentRequest.id());
    assertThat(sentRequestToValidate.creationDate()).isEqualTo(sentRequest.creationDate());
    assertThat(sentRequestToValidate.message()).isEqualTo(sentRequest.message());

    var queryToExecute = getPendingContactRequestsQueryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.userId()).isEqualTo(userId);
  }

  @Test
  void getContactRequestsWithOnlyReceivedRequestsOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var receivedRequest = buildContactRequest(UUID.randomUUID().toString(), userId);
    var receivedRequests = List.of(receivedRequest);

    var response = new GetPendingContactRequestsQueryResponse(List.of(), receivedRequests);
    getPendingContactRequestsQueryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.getContactRequests(authUser);

    // Assert
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();

    assertThat(result.getBody().receivedRequests()).isNotNull().hasSize(1);
    var receivedRequestToValidate = result.getBody().receivedRequests().getFirst();
    assertThat(receivedRequestToValidate.requestedUser()).isEqualTo(receivedRequest.requestedUser());
    assertThat(receivedRequestToValidate.requesterUser()).isEqualTo(receivedRequest.requesterUser());
    assertThat(receivedRequestToValidate.id()).isEqualTo(receivedRequest.id());
    assertThat(receivedRequestToValidate.creationDate()).isEqualTo(receivedRequest.creationDate());
    assertThat(receivedRequestToValidate.message()).isEqualTo(receivedRequest.message());

    assertThat(result.getBody().sentRequests()).isNotNull().isEmpty();

    var queryToExecute = getPendingContactRequestsQueryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.userId()).isEqualTo(userId);
  }

  @Test
  void getContactRequestsReturnsEmptyData() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var response = new GetPendingContactRequestsQueryResponse(List.of(), List.of());
    getPendingContactRequestsQueryHandlerSpy.initResponseToReturn(response);

    // Act
    var result = sut.getContactRequests(authUser);

    // Assert
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(result.getBody()).isNotNull();

    assertThat(result.getBody().receivedRequests()).isNotNull().isEmpty();
    assertThat(result.getBody().sentRequests()).isNotNull().isEmpty();

    var queryToExecute = getPendingContactRequestsQueryHandlerSpy.getQueryToExecute();
    assertThat(queryToExecute).isNotNull();
    assertThat(queryToExecute.userId()).isEqualTo(userId);
  }

  @Test
  void addNewContactOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var contactId = UUID.randomUUID().toString();
    var initialMessage = "Greetings";
    var request = new AddNewContactApiRequest(contactId, initialMessage);
    addNewContactCommandHandlerSpy.initResponse(EmptyCommandResponse.VALUE);

    // Act
    var result = sut.addNewContact(request, authUser);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    var commandToProcess = addNewContactCommandHandlerSpy.getCommandToProcess();
    assertThat(commandToProcess).isNotNull();
    assertThat(commandToProcess.contactId()).isEqualTo(contactId);
    assertThat(commandToProcess.initialMessage()).isEqualTo(initialMessage);
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void processContactRequestOk(boolean accept) {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var username = "foo";
    var authUser = new AuthUser(userId, username);

    var contactRequestId = UUID.randomUUID().toString();
    var request = new ProcessContactRequestApiRequest(accept);

    processContactRequestCommandHandlerSpy.initResponse(EmptyCommandResponse.VALUE);

    // Act
    var result = sut.processContactRequest(contactRequestId, request, authUser);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    var commandToProcess = processContactRequestCommandHandlerSpy.getCommandToProcess();
    assertThat(commandToProcess).isNotNull();
    assertThat(commandToProcess.contactRequestId()).isEqualTo(contactRequestId);
    assertThat(commandToProcess.requestedUserId()).isEqualTo(userId);
    assertThat(commandToProcess.accepted()).isEqualTo(accept);
  }

  private ContactRequestDataDto buildContactRequest(String requesterUser, String requestedUser) {
    return new ContactRequestDataDto(UUID.randomUUID().toString(), requestedUser, requesterUser, "Greetings", LocalDateTime.now());
  }
}
