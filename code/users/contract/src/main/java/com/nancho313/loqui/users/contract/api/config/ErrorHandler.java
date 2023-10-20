package com.nancho313.loqui.users.contract.api.config;

import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
  
  @ExceptionHandler({InvalidCommandDataException.class, IllegalArgumentException.class, IllegalStateException.class})
  public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {
    
    return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
  }
  
  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorDto> defaultException(Exception e) {
    
    return ResponseEntity.internalServerError().body(new ErrorDto(e.getMessage()));
  }
}
