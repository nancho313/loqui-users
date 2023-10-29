package com.nancho313.loqui.users.contract.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GetContactRequestsApiResponse(List<ContactRequest> sentRequests, List<ContactRequest> receivedRequests) {
  
  public record ContactRequest(String id, String requestedUser, String requesterUser, String message, LocalDateTime creationDate) {
  }
}
