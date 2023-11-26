package com.nancho313.loqui.users.domain.aggregate;

import com.nancho313.loqui.users.domain.entity.Contact;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.vo.ContactStatus;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nancho313.loqui.commons.ObjectValidator.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class User extends DomainAggregate {
  
  private static final String ERROR_MESSAGE = "Cannot create an User object. Errors -> %s";
  private static final String CONTACT_ALREADY_EXIST_ERROR_MESSAGE = "The user with id %s is already a contact of %s";
  private static final String ADDING_CONTACT_SAME_ID_ERROR_MESSAGE = "The new contact has the same id as the current " +
          "user. Id -> %s";
  private static final String NULL_CONTACT_ERROR_MESSAGE = "The contact to add cannot be null.";
  
  UserId id;
  
  String username;
  
  String email;
  
  List<Contact> contacts;
  
  CurrentDate currentDate;
  
  public User(UserId id, String username, String email, List<Contact> contacts, CurrentDate currentDate) {
    this(Collections.emptyList(), id, username, email, contacts, currentDate);
  }
  
  User(List<DomainEvent> events, UserId id, String username, String email, List<Contact> contacts,
       CurrentDate currentDate) {
    super(events);
    this.id = id;
    this.username = username;
    this.email = email;
    this.currentDate = currentDate;
    this.contacts = getImmutableList(contacts);
    validate();
  }
  
  public User addContact(UserId newContact) {
    
    if (isNull(newContact)) {
      
      throw new IllegalArgumentException(NULL_CONTACT_ERROR_MESSAGE);
    }
    
    var exists = contacts.stream().map(Contact::id).anyMatch(id -> id.equals(newContact));
    
    if (exists) {
      
      throw new IllegalArgumentException(CONTACT_ALREADY_EXIST_ERROR_MESSAGE.formatted(newContact.id(), this.id.id()));
    }
    
    if (newContact.equals(this.id)) {
      
      throw new IllegalArgumentException(ADDING_CONTACT_SAME_ID_ERROR_MESSAGE.formatted(newContact.id()));
    }
    
    var event = AddedContactEvent.create(this.id, newContact);
    List<Contact> newContacts = new ArrayList<>(this.contacts);
    newContacts.add(new Contact(newContact, ContactStatus.AVAILABLE));
    return new User(List.of(event), this.id, username, email, newContacts, this.currentDate.update());
  }
  
  public static User createUser(UserId id, String username, String email) {
    
    var event = CreatedUserEvent.create(id, username, email);
    return new User(List.of(event), id, username, email, Collections.emptyList(), CurrentDate.now());
  }
  
  private void validate() {
    
    List<String> errors = new ArrayList<>();
    
    if (isNull(id)) {
      
      errors.add("The id cannot be null.");
    }
    
    if (isEmptyString(username)) {
      
      errors.add("The username cannot be empty.");
    }
    
    if (isEmptyString(email)) {
      
      errors.add("The email cannot be empty.");
    }
    
    if (isNull(currentDate)) {
      
      errors.add("The current date cannot be null.");
    }
    
    if (isNotNull(contacts) && contacts.stream().anyMatch(contact -> contact.id().equals(id))) {
      
      errors.add("A contact has the same id as the user.");
    }
    
    if (!errors.isEmpty()) {
      
      throw new IllegalArgumentException(ERROR_MESSAGE.formatted(errors));
    }
  }
  
  @Value
  public static class CreatedUserEvent implements DomainEvent {
    
    private static final String CREATE_ERROR_MESSAGE = "Cannot create a CreatedUserEvent object. Errors -> %s";
    
    UserId id;
    String username;
    String email;
    LocalDateTime eventDate;
    
    private CreatedUserEvent(UserId id, String username, String email, LocalDateTime eventDate) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.eventDate = eventDate;
      this.validate();
    }
    
    static CreatedUserEvent create(UserId id, String username, String email) {
      
      return new CreatedUserEvent(id, username, email, LocalDateTime.now());
    }
    
    private void validate() {
      
      List<String> errors = new ArrayList<>();
      
      if (isNull(id)) {
        
        errors.add("The id cannot be null.");
      }
      
      if (isEmptyString(username)) {
        
        errors.add("The username cannot be empty.");
      }
      
      if (isEmptyString(email)) {
        
        errors.add("The email cannot be empty.");
      }
      
      if (isNull(eventDate)) {
        
        errors.add("The event date cannot be null.");
      }
      
      if (!errors.isEmpty()) {
        
        throw new IllegalArgumentException(CREATE_ERROR_MESSAGE.formatted(errors));
      }
    }
  }
  
  @Value
  public static class AddedContactEvent implements DomainEvent {
    
    private static final String CREATE_ERROR_MESSAGE = "Cannot create an AddedContactEvent object. Errors -> %s";
    
    UserId userId;
    UserId contactId;
    LocalDateTime eventDate;
    
    private AddedContactEvent(UserId userId, UserId contactId, LocalDateTime eventDate) {
      this.userId = userId;
      this.contactId = contactId;
      this.eventDate = eventDate;
      validate();
    }
    
    static AddedContactEvent create(UserId userId, UserId contactId) {
      
      return new AddedContactEvent(userId, contactId, LocalDateTime.now());
    }
    
    private void validate() {
      
      List<String> errors = new ArrayList<>();
      
      if (isNull(userId)) {
        
        errors.add("The user id cannot be null.");
      }
      
      if (isNull(contactId)) {
        
        errors.add("The contact id cannot be null.");
      }
      
      if (isNull(eventDate)) {
        
        errors.add("The event date cannot be null.");
      }
      
      if (!errors.isEmpty()) {
        
        throw new IllegalArgumentException(CREATE_ERROR_MESSAGE.formatted(errors));
      }
    }
  }
}