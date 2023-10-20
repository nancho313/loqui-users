package com.nancho313.loqui.users.contract.api.config;

import java.time.LocalDateTime;

public record ErrorDto(String message, LocalDateTime date) {
  
  ErrorDto(String message) {
    
    this(message, LocalDateTime.now());
  }
}
