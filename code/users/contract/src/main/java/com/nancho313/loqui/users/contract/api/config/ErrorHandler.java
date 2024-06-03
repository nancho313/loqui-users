package com.nancho313.loqui.users.contract.api.config;

import com.nancho313.loqui.users.application.exception.InvalidInputDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
  
  private static final String UNEXPECTED_ERROR_MESSAGE = "There was an unexpected error while processing your request.";
  
  @ExceptionHandler({InvalidInputDataException.class, IllegalArgumentException.class, IllegalStateException.class})
  public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {
    
    log.error("Bad Request error.", e);
    return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
  }
  
  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorDto> defaultException(Exception e) {
    
    log.error("Unexpected error.", e);
    return ResponseEntity.internalServerError().body(new ErrorDto(UNEXPECTED_ERROR_MESSAGE));
  }
}
