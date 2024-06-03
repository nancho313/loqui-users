package com.nancho313.loqui.users.test.application.queryhandler.contactrequest;

import com.nancho313.loqui.users.application.exception.InvalidInputDataException;
import com.nancho313.loqui.users.application.query.contactrequest.handler.GetPendingContactRequestsQueryHandler;
import com.nancho313.loqui.users.application.query.contactrequest.query.GetPendingContactRequestsQuery;
import com.nancho313.loqui.users.projection.model.ContactRequestModel;
import com.nancho313.loqui.users.test.application.util.ContactRequestDataSourceSpy;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetPendingContactRequestsQueryHandlerTest {

  private ContactRequestDataSourceSpy dataSourceSpy;

  private GetPendingContactRequestsQueryHandler sut;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    dataSourceSpy = new ContactRequestDataSourceSpy();
    sut = new GetPendingContactRequestsQueryHandler(validator, dataSourceSpy);
  }

  @Test
  void executeUsingRequesterUsersOk() {

    // Arrange
    var idUser = UUID.randomUUID().toString();
    var query = new GetPendingContactRequestsQuery(idUser);
    var contactRequestModelToStore = buildContactRequest(idUser, UUID.randomUUID().toString(), "PENDING");

    dataSourceSpy.initDataSource(List.of(contactRequestModelToStore));

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.sentRequests()).isNotNull().hasSize(1);
    var sentRequest = result.sentRequests().getFirst();
    assertThat(sentRequest.requesterUser()).isEqualTo(contactRequestModelToStore.requesterUser());
    assertThat(sentRequest.requestedUser()).isEqualTo(contactRequestModelToStore.requestedUser());
    assertThat(sentRequest.id()).isEqualTo(contactRequestModelToStore.id());
    assertThat(sentRequest.message()).isEqualTo(contactRequestModelToStore.message());
    assertThat(sentRequest.creationDate()).isEqualTo(contactRequestModelToStore.creationDate());

    assertThat(result.receivedRequests()).isNotNull().isEmpty();
  }

  @Test
  void executeUsingRequestedUsersOk() {

    // Arrange
    var idUser = UUID.randomUUID().toString();
    var query = new GetPendingContactRequestsQuery(idUser);
    var contactRequestModelToStore = buildContactRequest(UUID.randomUUID().toString(), idUser, "PENDING");

    dataSourceSpy.initDataSource(List.of(contactRequestModelToStore));

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.sentRequests()).isNotNull().isEmpty();

    assertThat(result.receivedRequests()).isNotNull().hasSize(1);
    var receivedRequest = result.receivedRequests().getFirst();
    assertThat(receivedRequest.requesterUser()).isEqualTo(contactRequestModelToStore.requesterUser());
    assertThat(receivedRequest.requestedUser()).isEqualTo(contactRequestModelToStore.requestedUser());
    assertThat(receivedRequest.id()).isEqualTo(contactRequestModelToStore.id());
    assertThat(receivedRequest.message()).isEqualTo(contactRequestModelToStore.message());
    assertThat(receivedRequest.creationDate()).isEqualTo(contactRequestModelToStore.creationDate());
  }

  @Test
  void executeUsingSentAndReceivedRequestsOk() {

    // Arrange
    var idUser = UUID.randomUUID().toString();
    var query = new GetPendingContactRequestsQuery(idUser);
    var sentRequestToStore = buildContactRequest(idUser, UUID.randomUUID().toString(), "PENDING");
    var receivedRequestToStore = buildContactRequest(UUID.randomUUID().toString(), idUser, "PENDING");

    dataSourceSpy.initDataSource(List.of(sentRequestToStore, receivedRequestToStore));

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.sentRequests()).isNotNull().hasSize(1);
    var sentRequest = result.sentRequests().getFirst();
    assertThat(sentRequest.requesterUser()).isEqualTo(sentRequestToStore.requesterUser());
    assertThat(sentRequest.requestedUser()).isEqualTo(sentRequestToStore.requestedUser());
    assertThat(sentRequest.id()).isEqualTo(sentRequestToStore.id());
    assertThat(sentRequest.message()).isEqualTo(sentRequestToStore.message());
    assertThat(sentRequest.creationDate()).isEqualTo(sentRequestToStore.creationDate());

    assertThat(result.receivedRequests()).isNotNull().hasSize(1);
    var receivedRequest = result.receivedRequests().getFirst();
    assertThat(receivedRequest.requesterUser()).isEqualTo(receivedRequestToStore.requesterUser());
    assertThat(receivedRequest.requestedUser()).isEqualTo(receivedRequestToStore.requestedUser());
    assertThat(receivedRequest.id()).isEqualTo(receivedRequestToStore.id());
    assertThat(receivedRequest.message()).isEqualTo(receivedRequestToStore.message());
    assertThat(receivedRequest.creationDate()).isEqualTo(receivedRequestToStore.creationDate());
  }

  @Test
  void executeReturnsEmpty() {

    // Arrange
    var idUser = UUID.randomUUID().toString();
    var query = new GetPendingContactRequestsQuery(idUser);

    // Act
    var result = sut.execute(query);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.sentRequests()).isNotNull().isEmpty();
    assertThat(result.receivedRequests()).isNotNull().isEmpty();
  }

  @Test
  void executeThrowsExceptionWhenUsingNullQuery() {

    // Arrange
    GetPendingContactRequestsQuery query = null;

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> sut.execute(query));
    assertThat(exception.getMessage()).isEqualTo("The query to execute cannot be null.");
  }

  @MethodSource("getInvalidData")
  @ParameterizedTest
  void executeWithInvalidData(String userId, String expectedErrorMessage) {

    // Arrange
    var query = new GetPendingContactRequestsQuery(userId);

    // Act & Assert
    var exception = assertThrows(InvalidInputDataException.class, () -> sut.execute(query));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }

  private static Stream<Arguments> getInvalidData() {

    String nullString = null;
    String expectedErrorMessage1 = "The user id cannot be empty.";

    return Stream.of(
        Arguments.of(nullString, expectedErrorMessage1),
        Arguments.of("", expectedErrorMessage1),
        Arguments.of("  ", expectedErrorMessage1)
    );
  }

  private ContactRequestModel buildContactRequest(String requesterUser, String requestedUser, String status) {

    return new ContactRequestModel(UUID.randomUUID().toString(), requestedUser, requesterUser, "Greetings",
        LocalDateTime.now().minusDays(1), LocalDateTime.now(), status);
  }
}
