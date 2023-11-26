package com.nancho313.loqui.users.test.domain.aggregate;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.entity.Contact;
import com.nancho313.loqui.users.domain.vo.ContactStatus;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {
  
  @Test
  void buildObjectOk() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = List.of(new Contact(UserId.of(UUID.randomUUID().toString()), ContactStatus.AVAILABLE));
    var currentDate = CurrentDate.now();
    
    // Act
    var result = new User(id, username, email, contacts, currentDate);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getUsername()).isEqualTo(username);
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getContacts()).containsExactlyElementsOf(contacts);
    assertThat(result.getCurrentDate().creationDate()).isCloseTo(currentDate.creationDate(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getCurrentDate().lastUpdatedDate()).isCloseTo(currentDate.lastUpdatedDate(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getCurrentEvents()).isEmpty();
  }
  
  @Test
  void createUserOk() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var currentDate = CurrentDate.now();
    
    // Act
    var result = User.createUser(id, username, email);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getUsername()).isEqualTo(username);
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getContacts()).isEmpty();
    assertThat(result.getCurrentDate().creationDate()).isCloseTo(currentDate.creationDate(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getCurrentDate().lastUpdatedDate()).isCloseTo(currentDate.lastUpdatedDate(), within(100,
            ChronoUnit.MILLIS));
    assertThat(result.getCurrentEvents()).isNotEmpty().hasSize(1).allMatch(event -> event instanceof User.CreatedUserEvent);
    var createdUserEvent = (User.CreatedUserEvent) result.getCurrentEvents().get(0);
    assertThat(createdUserEvent.getUsername()).isEqualTo(username);
    assertThat(createdUserEvent.getEmail()).isEqualTo(email);
    assertThat(createdUserEvent.getId()).isEqualTo(id);
    assertThat(createdUserEvent.getEventDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
  }
  
  @Test
  void addContactToUserWithoutContactsOk() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = new ArrayList<Contact>();
    var currentDate = CurrentDate.now();
    var currentUser = new User(id, username, email, contacts, currentDate);
    var contactToAdd = UserId.of(UUID.randomUUID().toString());
    
    // Act
    var result = currentUser.addContact(contactToAdd);
    
    // Assert
    assertThat(result.getContacts()).isNotEmpty().hasSize(1)
            .allMatch(contact -> contact.id().equals(contactToAdd) && contact.status().equals(ContactStatus.AVAILABLE));
    assertThat(result.getCurrentEvents()).isNotEmpty().hasSize(1).allMatch(event -> event instanceof User.AddedContactEvent);
    var addedContactEvent =
            result.getCurrentEvents().stream().map(User.AddedContactEvent.class::cast).findFirst().get();
    assertThat(addedContactEvent.getUserId()).isEqualTo(id);
    assertThat(addedContactEvent.getContactId()).isEqualTo(contactToAdd);
    assertThat(addedContactEvent.getEventDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
  }
  
  @Test
  void addContactToUserWithContactsOk() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = List.of(new Contact(UserId.of(UUID.randomUUID().toString()), ContactStatus.AVAILABLE));
    var currentDate = CurrentDate.now();
    var currentUser = new User(id, username, email, contacts, currentDate);
    var contactToAdd = UserId.of(UUID.randomUUID().toString());
    
    // Act
    var result = currentUser.addContact(contactToAdd);
    
    // Assert
    assertThat(result.getContacts()).isNotEmpty().hasSize(2);
    assertThat(result.getContacts()).filteredOn(contact -> contact.id().equals(contactToAdd))
            .allMatch(contact -> contact.id().equals(contactToAdd) && contact.status().equals(ContactStatus.AVAILABLE));
    assertThat(result.getCurrentEvents()).isNotEmpty().hasSize(1).allMatch(event -> event instanceof User.AddedContactEvent);
    var addedContactEvent =
            result.getCurrentEvents().stream().map(User.AddedContactEvent.class::cast).findFirst().get();
    assertThat(addedContactEvent.getUserId()).isEqualTo(id);
    assertThat(addedContactEvent.getContactId()).isEqualTo(contactToAdd);
    assertThat(addedContactEvent.getEventDate()).isCloseTo(LocalDateTime.now(), within(100, ChronoUnit.MILLIS));
  }
  
  @Test
  void addSameContactToUserThrowsException() {
    
    // Arrange
    var contactToAdd = UserId.of(UUID.randomUUID().toString());
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = List.of(new Contact(contactToAdd, ContactStatus.AVAILABLE));
    var currentDate = CurrentDate.now();
    var currentUser = new User(id, username, email, contacts, currentDate);
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> currentUser.addContact(contactToAdd));
    assertThat(exception.getMessage()).contains("The user with id %s is already a contact of %s".formatted(contactToAdd.id(), id.id()));
  }
  
  @Test
  void addContactUsingTheSameUserIdThrowsException() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = new ArrayList<Contact>();
    // a User object
    var currentDate = CurrentDate.now();
    var currentUser = new User(id, username, email, contacts, currentDate);
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> currentUser.addContact(id));
    assertThat(exception.getMessage()).contains("The new contact has the same id as the current user");
  }
  
  @Test
  void addNullContactToUserThrowsException() {
    
    // Arrange
    UserId contactToAdd = null;
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = new ArrayList<Contact>();
    var currentDate = CurrentDate.now();
    var currentUser = new User(id, username, email, contacts, currentDate);
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> currentUser.addContact(contactToAdd));
    assertThat(exception.getMessage()).contains("The contact to add cannot be null.");
  }
  
  @MethodSource("getBuildObjectInvalidData")
  @ParameterizedTest
  void buildObjectWithInvalidData(UserId id, String username, String email, List<Contact> contacts,
                                  CurrentDate currentDate, String expectedErrorMessage) {
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> new User(id, username, email, contacts,
            currentDate));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getBuildObjectInvalidData() {
    
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var contacts = List.of(new Contact(UserId.of(UUID.randomUUID().toString()), ContactStatus.AVAILABLE));
    var currentDate = CurrentDate.now();
    var contactWithSameUserId = new Contact(id, ContactStatus.AVAILABLE);
    
    var expectedErrorMessage1 = "The id cannot be null.";
    var expectedErrorMessage2 = "The username cannot be empty.";
    var expectedErrorMessage3 = "The email cannot be empty.";
    var expectedErrorMessage4 = "The current date cannot be null.";
    var expectedErrorMessage5 = "A contact has the same id as the user.";
    
    return Stream.of(
            Arguments.of(null, username, email, contacts, currentDate, expectedErrorMessage1),
            Arguments.of(id, null, email, contacts, currentDate, expectedErrorMessage2),
            Arguments.of(id, "", email, contacts, currentDate, expectedErrorMessage2),
            Arguments.of(id, "  ", email, contacts, currentDate, expectedErrorMessage2),
            Arguments.of(id, username, null, contacts, currentDate, expectedErrorMessage3),
            Arguments.of(id, username, "", contacts, currentDate, expectedErrorMessage3),
            Arguments.of(id, username, "  ", contacts, currentDate, expectedErrorMessage3),
            Arguments.of(id, username, email, List.of(contactWithSameUserId), currentDate, expectedErrorMessage5),
            Arguments.of(id, username, email, contacts, null, expectedErrorMessage4)
    );
  }
  
  @MethodSource("getCreateUserInvalidData")
  @ParameterizedTest
  void createUserWithInvalidData(UserId id, String username, String email, String expectedErrorMessage) {
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> User.createUser(id, username, email));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getCreateUserInvalidData() {
    
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    
    var expectedErrorMessage1 = "The id cannot be null.";
    var expectedErrorMessage2 = "The username cannot be empty.";
    var expectedErrorMessage3 = "The email cannot be empty.";
    
    return Stream.of(
            Arguments.of(null, username, email, expectedErrorMessage1),
            Arguments.of(id, null, email, expectedErrorMessage2),
            Arguments.of(id, "", email, expectedErrorMessage2),
            Arguments.of(id, "  ", email, expectedErrorMessage2),
            Arguments.of(id, username, null, expectedErrorMessage3),
            Arguments.of(id, username, "", expectedErrorMessage3),
            Arguments.of(id, username, "  ", expectedErrorMessage3)
    );
  }
  
  @MethodSource("getCreatedUserEventInvalidData")
  @ParameterizedTest
  void buildCreatedUserEventWithInvalidData(UserId id, String username, String email, LocalDateTime eventDate, String expectedErrorMessage) {
    
    // Arrange
    Constructor<User.CreatedUserEvent> constructor= (Constructor<User.CreatedUserEvent>) User.CreatedUserEvent.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    
    // Act & Assert
    var exception = assertThrows(InvocationTargetException.class, () -> constructor.newInstance(id, username, email, eventDate));
    assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
    assertThat(exception.getCause().getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getCreatedUserEventInvalidData() {
    
    var id = UserId.of(UUID.randomUUID().toString());
    var username = "foo";
    var email = "foo@email.com";
    var eventDate = LocalDateTime.now();
    
    var expectedErrorMessage1 = "The id cannot be null.";
    var expectedErrorMessage2 = "The username cannot be empty.";
    var expectedErrorMessage3 = "The email cannot be empty.";
    var expectedErrorMessage4 = "The event date cannot be null.";
    
    return Stream.of(
            Arguments.of(null, username, email, eventDate, expectedErrorMessage1),
            Arguments.of(id, null, email, eventDate, expectedErrorMessage2),
            Arguments.of(id, "", email, eventDate, expectedErrorMessage2),
            Arguments.of(id, "  ", email, eventDate, expectedErrorMessage2),
            Arguments.of(id, username, null, eventDate, expectedErrorMessage3),
            Arguments.of(id, username, "", eventDate, expectedErrorMessage3),
            Arguments.of(id, username, "  ", eventDate, expectedErrorMessage3),
            Arguments.of(id, username, email, null, expectedErrorMessage4)
    );
  }
  
  @MethodSource("getAddedContactEventInvalidData")
  @ParameterizedTest
  void buildAddedContactEventWithInvalidData(UserId id, UserId contactId, LocalDateTime eventDate, String expectedErrorMessage) {
    
    // Arrange
    Constructor<User.AddedContactEvent> constructor= (Constructor<User.AddedContactEvent>) User.AddedContactEvent.class.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    
    // Act & Assert
    var exception = assertThrows(InvocationTargetException.class, () -> constructor.newInstance(id, contactId, eventDate));
    assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
    assertThat(exception.getCause().getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getAddedContactEventInvalidData() {
    
    var id = UserId.of(UUID.randomUUID().toString());
    var contactId = UserId.of(UUID.randomUUID().toString());
    var eventDate = LocalDateTime.now();
    
    var expectedErrorMessage1 = "The user id cannot be null.";
    var expectedErrorMessage2 = "The contact id cannot be null.";
    var expectedErrorMessage3 = "The event date cannot be null.";
    
    return Stream.of(
            Arguments.of(null, contactId, eventDate, expectedErrorMessage1),
            Arguments.of(id, null, eventDate, expectedErrorMessage2),
            Arguments.of(id, contactId, null, expectedErrorMessage3)
    );
  }
}
