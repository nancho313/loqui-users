package com.nancho313.loqui.users.application.command.user.handler;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.user.command.AddContactToUserCommand;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class AddContactToUserCommandHandler extends CommandHandler<AddContactToUserCommand, EmptyCommandResponse> {
  
  private static final String USER_NOT_FOUND_ERROR_MESSAGE = "The user with id %s does not exist.";
  private final UserRepository userRepository;
  
  public AddContactToUserCommandHandler(Validator validator, UserRepository userRepository) {
    super(validator);
    this.userRepository = userRepository;
  }
  
  protected HandleCommandResult<EmptyCommandResponse> handleCommand(AddContactToUserCommand command) {
    
    var contactId = UserId.of(command.contactId());
    
    if (!userRepository.existsById(contactId)) {
      
      throw new NoSuchElementException(USER_NOT_FOUND_ERROR_MESSAGE.formatted(command.contactId()));
    }
    
    var currentUser =
            userRepository.findById(UserId.of(command.userId())).orElseThrow(() ->
                    new NoSuchElementException(USER_NOT_FOUND_ERROR_MESSAGE.formatted(command.userId())));
    
    var updatedUser = currentUser.addContact(contactId);
    
    return buildResult(EmptyCommandResponse.VALUE, updatedUser.getCurrentEvents());
  }
  
  protected void processEvents(List<DomainEvent> events) {
    
    events.forEach(event -> {
      
      switch (event) {
        
        case User.AddedContactEvent addedContactEvent ->
                userRepository.addContact(addedContactEvent.getUserId(), addedContactEvent.getContactId());
        default -> log.warn("Event not supported {}", event);
      }
    });
  }
}
