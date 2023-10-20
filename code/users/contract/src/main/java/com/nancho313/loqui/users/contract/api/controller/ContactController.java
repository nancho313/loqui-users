package com.nancho313.loqui.users.contract.api.controller;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.EmptyCommandResponse;
import com.nancho313.loqui.users.application.command.contactrequest.command.AddNewContactCommand;
import com.nancho313.loqui.users.application.command.contactrequest.command.ProcessContactRequestCommand;
import com.nancho313.loqui.users.contract.api.config.AuthUser;
import com.nancho313.loqui.users.contract.api.dto.AddNewContactApiRequest;
import com.nancho313.loqui.users.contract.api.dto.GetContactRequestsApiResponse;
import com.nancho313.loqui.users.contract.api.dto.ProcessContactRequestApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/contact")
@RequiredArgsConstructor
public class ContactController {
  
  private final CommandHandler<AddNewContactCommand, EmptyCommandResponse> addNewContactCommandHandler;
  
  private final CommandHandler<ProcessContactRequestCommand, EmptyCommandResponse> processContactRequestCommandHandler;
  
  @GetMapping
  public ResponseEntity<String> searchContacts() {
    
    return null;
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
    
    var response = new GetContactRequestsApiResponse();
    return ResponseEntity.ok(response);
  }
  
}
