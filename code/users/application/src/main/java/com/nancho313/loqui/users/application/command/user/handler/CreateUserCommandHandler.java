package com.nancho313.loqui.users.application.command.user.handler;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.CreateUserCommand;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.event.EventResolverFactory;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateUserCommandHandler extends CommandHandler<CreateUserCommand, EmptyCommandResponse> {

  private final UserRepository userRepository;

  public CreateUserCommandHandler(Validator validator, EventResolverFactory eventResolverFactory, UserRepository userRepository) {
    super(validator, eventResolverFactory);
    this.userRepository = userRepository;
  }

  protected HandleCommandResult<EmptyCommandResponse> handleCommand(CreateUserCommand command) {

    if (userRepository.existsById(UserId.of(command.userId()))) {

      throw new IllegalArgumentException("The user with id " + command.userId() + " already exists.");
    }

    var newUser = User.createUser(UserId.of(command.userId()), command.username(), command.email());
    userRepository.save(newUser);
    return buildResult(EmptyCommandResponse.VALUE, newUser.getCurrentEvents());
  }
}
