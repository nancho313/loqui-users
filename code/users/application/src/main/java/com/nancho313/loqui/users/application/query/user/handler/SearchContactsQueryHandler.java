package com.nancho313.loqui.users.application.query.user.handler;

import com.nancho313.loqui.users.application.query.QueryHandler;
import com.nancho313.loqui.users.application.query.user.dto.ContactResultDto;
import com.nancho313.loqui.users.application.query.user.dto.UserResultDto;
import com.nancho313.loqui.users.application.query.user.query.SearchContactsQuery;
import com.nancho313.loqui.users.application.query.user.response.SearchContactsQueryResponse;
import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class SearchContactsQueryHandler extends QueryHandler<SearchContactsQuery, SearchContactsQueryResponse> {
  
  private final UserDataSource userDataSource;
  
  public SearchContactsQueryHandler(Validator validator, UserDataSource userDataSource) {
    super(validator);
    this.userDataSource = userDataSource;
  }
  
  protected SearchContactsQueryResponse executeQuery(SearchContactsQuery query) {
    
    return new SearchContactsQueryResponse(userDataSource.searchContacts(query.userId()).stream()
            .map(contact -> new ContactResultDto(new UserResultDto(contact.id(), contact.username(), contact.email())
                    , contact.status())).toList());
  }
}
