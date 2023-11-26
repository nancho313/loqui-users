package com.nancho313.loqui.users.domain.vo;

import static com.nancho313.loqui.commons.ObjectValidator.isEmptyString;

public record ContactRequestId(String id) {
  
  public ContactRequestId {
    
    if (isEmptyString(id)) {
      throw new IllegalArgumentException("Cannot create a ContactRequestId object. Errors -> The id cannot be empty.");
    }
  }
  
  public static ContactRequestId of(String id) {
    return new ContactRequestId(id);
  }
}
