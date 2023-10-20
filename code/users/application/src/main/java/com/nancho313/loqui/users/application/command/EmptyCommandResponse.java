package com.nancho313.loqui.users.application.command;

public final class EmptyCommandResponse implements CommandResponse {
  
  private EmptyCommandResponse() {
  
  }
  
  public static final EmptyCommandResponse VALUE = new EmptyCommandResponse();
}
