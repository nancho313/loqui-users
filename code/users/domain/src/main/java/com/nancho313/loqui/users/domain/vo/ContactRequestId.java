package com.nancho313.loqui.users.domain.vo;

public record ContactRequestId(String id) {
  
  public static ContactRequestId of(String id) {
    return new ContactRequestId(id);
  }
}
