package com.nancho313.loqui.users.test.contract.util;

import com.nancho313.loqui.users.application.query.Query;
import com.nancho313.loqui.users.application.query.QueryHandler;
import com.nancho313.loqui.users.application.query.QueryResponse;
import jakarta.validation.Validation;
import lombok.Getter;

@Getter
public class QueryHandlerTestUtil<Q extends Query, R extends QueryResponse> extends QueryHandler<Q, R> {

  private Q queryToExecute;

  private R responseToReturn;

  public QueryHandlerTestUtil() {

    super(Validation.buildDefaultValidatorFactory().getValidator());
  }

  public void initResponseToReturn(R responseToReturn) {

    this.responseToReturn = responseToReturn;
  }

  @Override
  protected R executeQuery(Q query) {
    this.queryToExecute = query;
    return responseToReturn;
  }
}
