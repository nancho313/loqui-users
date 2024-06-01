package com.nancho313.loqui.users.test.application.commandhandler;

import com.nancho313.loqui.users.application.command.contactrequest.command.AddNewContactCommand;
import com.nancho313.loqui.users.application.command.contactrequest.handler.AddNewContactCommandHandler;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.test.application.util.ContactRequestRepositorySpy;
import com.nancho313.loqui.users.test.application.util.UserRepositorySpy;
import jakarta.validation.Validation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddNewContactCommandHandlerTest {
  
  private AddNewContactCommandHandler sut;
  
  private UserRepositorySpy userRepositorySpy;
  
  private ContactRequestRepositorySpy contactRequestRepositorySpy;
  
  @BeforeEach
  void setup() {
    
    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    userRepositorySpy = new UserRepositorySpy();
    contactRequestRepositorySpy = new ContactRequestRepositorySpy();
    sut = new AddNewContactCommandHandler(validator, userRepositorySpy, contactRequestRepositorySpy,
            () -> UUID.randomUUID().toString());
  }
  
  @Test
  void handleOk() {
    
    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();
    var initialMessage = "This is the initial message";
    
    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user1);
    userRepositorySpy.save(user2);
    
    var command = new AddNewContactCommand(userId, contactId, initialMessage);
    
    // Act
    sut.handle(command);
    
    // Assert
    var allContactRequests = contactRequestRepositorySpy.findAll();
    assertThat(allContactRequests).hasSize(1)
            .allMatch(contactRequest -> contactRequest.getRequesterUser().equals(user1.getId())
                    && contactRequest.getRequestedUser().equals(user2.getId())
                    && contactRequest.getStatus().equals(ContactRequestStatus.PENDING));
  }

  @Test
  void handleThrowsExceptionDueContactIdDoesNotExist() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();
    var initialMessage = "This is the initial message";

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");

    userRepositorySpy.save(user1);

    var command = new AddNewContactCommand(userId, contactId, initialMessage);

    // Act & Assert
    var exception = assertThrows(NoSuchElementException.class, () -> sut.handle(command));
    assertThat(exception.getMessage()).isEqualTo("The user with the id "+contactId+" does not exist.");



  }
}
