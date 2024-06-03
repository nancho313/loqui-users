package com.nancho313.loqui.users.test.application.commandhandler.user;

import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.AddContactToUserCommand;
import com.nancho313.loqui.users.application.command.user.handler.AddContactToUserCommandHandler;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.test.application.util.EventResolverFactorySpy;
import com.nancho313.loqui.users.test.application.util.UserRepositorySpy;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AddContactToUserCommandHandlerTest {

  private EventResolverFactorySpy eventResolverFactorySpy;

  private UserRepositorySpy userRepositorySpy;

  private AddContactToUserCommandHandler sut;

  @BeforeEach
  void setup() {

    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    eventResolverFactorySpy = new EventResolverFactorySpy();
    userRepositorySpy = new UserRepositorySpy();
    sut = new AddContactToUserCommandHandler(validator, eventResolverFactorySpy, userRepositorySpy);
  }

  @Disabled
  @Test
  void handleOk() {

    // Arrange
    var userId = UUID.randomUUID().toString();
    var contactId = UUID.randomUUID().toString();

    var user1 = User.createUser(UserId.of(userId), "foo1", "foo1@email.com");
    var user2 = User.createUser(UserId.of(contactId), "foo2", "foo2@email.com");
    userRepositorySpy.save(user1);
    userRepositorySpy.save(user2);

    var command = new AddContactToUserCommand(userId, contactId);

    // Act
    var result = sut.handle(command);

    // Assert
    assertThat(result).isNotNull().isEqualTo(EmptyCommandResponse.VALUE);
  }
}
