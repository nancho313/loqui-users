package com.nancho313.loqui.users.application.command.contactrequest.handler;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.contactrequest.command.ProcessContactRequestCommand;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.event.EventResolverFactory;
import com.nancho313.loqui.users.domain.repository.ContactRequestRepository;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import com.nancho313.loqui.users.domain.vo.UserId;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class ProcessContactRequestCommandHandler extends CommandHandler<ProcessContactRequestCommand,
        EmptyCommandResponse> {
  
  private static final String USER_NOT_FOUND_ERROR_MESSAGE = "The user with the id %s does not exist.";
  private static final String CONTACT_REQUEST_NOT_FOUND_ERROR_MESSAGE = "The contact request with the id %s does not " +
          "exist.";
  
  private final ContactRequestRepository contactRequestRepository;
  
  private final UserRepository userRepository;
  
  private final EventResolverFactory eventResolverFactory;
  
  public ProcessContactRequestCommandHandler(Validator validator, ContactRequestRepository contactRequestRepository,
                                             UserRepository userRepository, EventResolverFactory eventResolverFactory) {
    super(validator);
    this.contactRequestRepository = contactRequestRepository;
    this.userRepository = userRepository;
    this.eventResolverFactory = eventResolverFactory;
  }
  
  protected HandleCommandResult<EmptyCommandResponse> handleCommand(ProcessContactRequestCommand command) {
    
    var requestedUserId = UserId.of(command.requestedUserId());
    
    if (!userRepository.existsById(requestedUserId)) {
      
      throw new NoSuchElementException(USER_NOT_FOUND_ERROR_MESSAGE.formatted(command.requestedUserId()));
    }
    
    var contactRequestToProcess =
            contactRequestRepository.findById(ContactRequestId.of(command.contactRequestId())).orElseThrow(
                    () -> new NoSuchElementException(CONTACT_REQUEST_NOT_FOUND_ERROR_MESSAGE.formatted(command.contactRequestId())));
    
    var processedContactRequest = contactRequestToProcess.processRequest(command.accepted(), requestedUserId);
    contactRequestRepository.save(processedContactRequest);
    
    return buildResult(EmptyCommandResponse.VALUE, processedContactRequest.getCurrentEvents());
  }
  
  protected void processEvents(List<DomainEvent> events) {
    
    events.forEach(event -> eventResolverFactory.getResolver(event)
            .ifPresent(domainEventEventResolver -> domainEventEventResolver.processEvent(event)));
  }
}
