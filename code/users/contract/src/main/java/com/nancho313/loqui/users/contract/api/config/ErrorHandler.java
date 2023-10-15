package com.nancho313.loqui.users.contract.api.config;

import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.application.exception.InvalidResponseDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidCommandDataException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {

        return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDto> defaultException(Exception e) {

        return ResponseEntity.internalServerError().body(new ErrorDto(e.getMessage()));
    }

    protected record ErrorDto(String message, LocalDateTime date) {

        ErrorDto(String message) {

            this(message, LocalDateTime.now());
        }
    }
}
