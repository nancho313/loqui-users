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
    
    var exists = contacts.stream().map(Contact::id).anyMatch(id -> id.equals(newContact));
    
    if (exists) {
      
      throw new IllegalArgumentException(CONTACT_ALREADY_EXIST_ERROR_MESSAGE.formatted(newContact.id(), this.id.id()));
    }
    
    if (newContact.equals(this.id)) {
      
      throw new IllegalArgumentException(ADDING_CONTACT_SAME_ID_ERROR_MESSAGE.formatted(newContact.id()));
    }
    
    var event = new AddedContactEvent(this.id, newContact);
    List<Contact> newContacts = new ArrayList<>(this.contacts);
    newContacts.add(new Contact(newContact, ContactStatus.AVAILABLE));
    return new User(List.of(event), this.id, username, email, newContacts, this.currentDate.update());
  }
  
  public static User createUser(UserId id, String username, String email) {
    
    var event = new CreatedUserEvent(id, username, email);
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
    
    if (!errors.isEmpty()) {
      
      throw new IllegalArgumentException(ERROR_MESSAGE.formatted(errors));
    }
  }
  
  public record CreatedUserEvent(UserId id, String username, String email,
                                  LocalDateTime eventDate) implements DomainEvent {
    
    public CreatedUserEvent {
      
      assert (isNotNull(id));
      assert (isNotAnEmptyString(username));
      assert (isNotAnEmptyString(email));
      assert (isNotNull(eventDate));
    }
    
    public CreatedUserEvent(UserId id, String username, String email) {
      
      this(id, username, email, LocalDateTime.now());
    }
  }
  
  public record AddedContactEvent(UserId userId, UserId contactId, LocalDateTime eventDate) implements DomainEvent {
    
    public AddedContactEvent {
      
      assert (isNotNull(userId));
      assert (isNotNull(contactId));
      assert (isNotNull(eventDate));
    }
    
    public AddedContactEvent(UserId userId, UserId contactId) {
      
      this(userId, contactId, LocalDateTime.now());
    }
  }
  
}
