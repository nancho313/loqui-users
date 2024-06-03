package com.nancho313.loqui.users.application.query.contactrequest.handler;

import com.nancho313.loqui.users.application.query.QueryHandler;
import com.nancho313.loqui.users.application.query.contactrequest.dto.ContactRequestDataDto;
import com.nancho313.loqui.users.application.query.contactrequest.query.GetPendingContactRequestsQuery;
import com.nancho313.loqui.users.application.query.contactrequest.response.GetPendingContactRequestsQueryResponse;
import com.nancho313.loqui.users.domain.vo.ContactRequestStatus;
import com.nancho313.loqui.users.projection.datasource.ContactRequestDataSource;
import com.nancho313.loqui.users.projection.model.ContactRequestModel;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class GetPendingContactRequestsQueryHandler extends QueryHandler<GetPendingContactRequestsQuery,
        GetPendingContactRequestsQueryResponse> {
  
  private final ContactRequestDataSource dataSource;
  
  public GetPendingContactRequestsQueryHandler(Validator validator, ContactRequestDataSource dataSource) {
    super(validator);
    this.dataSource = dataSource;
  }
  
  protected GetPendingContactRequestsQueryResponse executeQuery(GetPendingContactRequestsQuery query) {
    
    var contactRequests = dataSource.getContactRequests(query.userId(), ContactRequestStatus.PENDING.name());
    
    var sentRequests =
            contactRequests.stream().filter(request -> request.requesterUser().equals(query.userId()))
                    .map(this::toContactRequestDataDto).toList();
    
    var receivedRequests =
            contactRequests.stream().filter(request -> request.requestedUser().equals(query.userId()))
                    .map(this::toContactRequestDataDto).toList();
    
    return new GetPendingContactRequestsQueryResponse(sentRequests, receivedRequests);
  }
  
  private ContactRequestDataDto toContactRequestDataDto(ContactRequestModel model) {
    
    return new ContactRequestDataDto(model.id(), model.requestedUser(), model.requesterUser(), model.message(),
            model.creationDate());
  }
}
