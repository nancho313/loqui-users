package com.nancho313.loqui.users.application.query.contactrequest.response;

import com.nancho313.loqui.users.application.query.QueryResponse;
import com.nancho313.loqui.users.application.query.contactrequest.dto.ContactRequestDataDto;

import java.util.List;

public record GetPendingContactRequestsQueryResponse(
        List<ContactRequestDataDto> sentRequests, List<ContactRequestDataDto> receivedRequests) implements QueryResponse {
  
}