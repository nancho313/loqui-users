package com.nancho313.loqui.users.contract.api.controller;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.contactrequest.command.AddNewContactCommand;
import com.nancho313.loqui.users.application.command.contactrequest.command.ProcessContactRequestCommand;
import com.nancho313.loqui.users.application.query.QueryHandler;
import com.nancho313.loqui.users.application.query.contactrequest.dto.ContactRequestDataDto;
import com.nancho313.loqui.users.application.query.contactrequest.query.GetPendingContactRequestsQuery;
import com.nancho313.loqui.users.application.query.contactrequest.response.GetPendingContactRequestsQueryResponse;
import com.nancho313.loqui.users.application.query.user.query.SearchContactsQuery;
import com.nancho313.loqui.users.application.query.user.response.SearchContactsQueryResponse;
import com.nancho313.loqui.users.contract.api.config.AuthUser;
import com.nancho313.loqui.users.contract.api.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/contact")
@RequiredArgsConstructor
public class ContactController {
  
  private final CommandHandler<AddNewContactCommand, EmptyCommandResponse> addNewContactCommandHandler;
  
  private final CommandHandler<ProcessContactRequestCommand, EmptyCommandResponse> processContactRequestCommandHandler;
  
  private final QueryHandler<SearchContactsQuery, SearchContactsQueryResponse> searchContactsCommandHandler;
  
  private final QueryHandler<GetPendingContactRequestsQuery, GetPendingContactRequestsQueryResponse> getPendingContactRequestsQueryHandler;
  
  @GetMapping
  public ResponseEntity<SearchContactsApiResponse> searchContacts(@RequestAttribute("authUser") AuthUser authUser) {
    
    var query = new SearchContactsQuery(authUser.userId());
    var response = searchContactsCommandHandler.execute(query);
    var result =
            new SearchContactsApiResponse(response.contacts().stream().map(contact -> new ContactApiDto(new UserApiDto(contact.user().id(),
                    contact.user().username(), contact.user().email()), contact.status())).toList());
    return ResponseEntity.ok(result);
  }
  
  @PostMapping("/request")
  public ResponseEntity<Void> addNewContact(@RequestBody AddNewContactApiRequest request,
                                            @RequestAttribute("authUser") AuthUser authUser) {
    
    var command = new AddNewContactCommand(authUser.userId(), request.contactId(), request.initialMessage());
    addNewContactCommandHandler.handle(command);
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/request/{id}")
  public ResponseEntity<Void> processContactRequest(@PathVariable("id") String contactRequestId,
                                                    @RequestBody ProcessContactRequestApiRequest request,
                                                    @RequestAttribute("authUser") AuthUser authUser) {
    
    var command = new ProcessContactRequestCommand(contactRequestId, authUser.userId(), request.accept());
    processContactRequestCommandHandler.handle(command);
    return ResponseEntity.ok().build();
  }
  
  @GetMapping("/request")
  public ResponseEntity<GetContactRequestsApiResponse> getContactRequests(@RequestAttribute("authUser") AuthUser authUser) {
    
    var query = new GetPendingContactRequestsQuery(authUser.userId());
    var queryResponse = getPendingContactRequestsQueryHandler.execute(query);
    var sentRequests = queryResponse.sentRequests().stream().map(this::toContactRequest).toList();
    var receivedRequests = queryResponse.receivedRequests().stream().map(this::toContactRequest).toList();
    var result = new GetContactRequestsApiResponse(sentRequests, receivedRequests);
    return ResponseEntity.ok(result);
  }
  
  private GetContactRequestsApiResponse.ContactRequest toContactRequest(ContactRequestDataDto dto) {
    
    return new GetContactRequestsApiResponse.ContactRequest(dto.id(), dto.requestedUser(), dto.requesterUser(),
            dto.message(), dto.creationDate());
  }
}
