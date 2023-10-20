package com.nancho313.loqui.users.application.command.contactrequest.handler;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.contactrequest.command.AddNewContactCommand;
import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.repository.ContactRequestRepository;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class AddNewContactCommandHandler extends CommandHandler<AddNewContactCommand, EmptyCommandResponse> {
  
  private static final String USER_NOT_FOUND_ERROR_MESSAGE = "The user with the id %s does not exist.";
  private static final String PENDING_CONTACT_REQUEST_EXIST_ERROR_MESSAGE = "There is already a pending contact " +
          "request with the given users. Requester -> %s, Requested -> %s";
  
  private final UserRepository userRepository;
  
  private final ContactRequestRepository contactRequestRepository;
  
  private final IdGenerator idGenerator;
  
  public AddNewContactCommandHandler(Validator validator, UserRepository userRepository,
                                     ContactRequestRepository contactRequestRepository, IdGenerator idGenerator) {
    super(validator);
    this.userRepository = userRepository;
    this.contactRequestRepository = contactRequestRepository;
    this.idGenerator = idGenerator;
  }
  
  protected HandleCommandResult<EmptyCommandResponse> handleCommand(AddNewContactCommand command) {
    
    var requesterUserId = UserId.of(command.userId());
    var requestedUserId = UserId.of(command.contactId());
    
    if (contactRequestRepository.existsPendingRequest(requesterUserId, requestedUserId)) {
      
      throw new IllegalArgumentException(PENDING_CONTACT_REQUEST_EXIST_ERROR_MESSAGE.formatted(requesterUserId.id(),
              requestedUserId.id()));
    }
    
    if (!userRepository.existsById(requestedUserId)) {
      
      throw new NoSuchElementException(USER_NOT_FOUND_ERROR_MESSAGE.formatted(requestedUserId.id()));
    }
    
    if (!userRepository.existsById(requesterUserId)) {
      
      throw new NoSuchElementException(USER_NOT_FOUND_ERROR_MESSAGE.formatted(requesterUserId.id()));
    }
    
    var newContactRequest = contactRequestRepository.save(ContactRequest.create(idGenerator, requesterUserId,
            requestedUserId, command.initialMessage()));
    
    return buildResult(EmptyCommandResponse.VALUE, newContactRequest.getCurrentEvents());
  }
  
  protected void processEvents(List<DomainEvent> events) {
    
    //TODO trigger events
    events.forEach(event -> log.info(event.toString()));
  }
}
