package com.nancho313.loqui.users.domain.entity;

import com.nancho313.loqui.users.domain.vo.ContactStatus;
import com.nancho313.loqui.users.domain.vo.UserId;

import java.util.ArrayList;
import java.util.List;

import static com.nancho313.loqui.commons.ObjectValidator.isNull;

public record Contact(UserId id, ContactStatus status) {
  
  private static final String VALIDATE_ERROR_MESSAGE = "Cannot create a Contact object. Errors -> %s";
  
  public Contact(UserId id, ContactStatus status) {
    this.id = id;
    this.status = status;
    validate();
  }
  
  private void validate() {
    
    List<String> errors = new ArrayList<>();
    
    if (isNull(id)) {
      
      errors.add("The id cannot be null.");
    }
    
    if (isNull(status)) {
      
      errors.add("The status cannot be null.");
    }
    
    if (!errors.isEmpty()) {
      
      throw new IllegalArgumentException(VALIDATE_ERROR_MESSAGE.formatted(errors));
    }
  }
}
