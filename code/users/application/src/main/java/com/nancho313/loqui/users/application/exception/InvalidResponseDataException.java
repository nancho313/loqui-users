package com.nancho313.loqui.users.application.exception;

import java.util.List;

public class InvalidResponseDataException extends RuntimeException {

    private static final String INVALID_RESPONSE_ERROR_MESSAGE = "The response has invalid values. Error -> %s";

    public InvalidResponseDataException(List<String> errors) {

        super(INVALID_RESPONSE_ERROR_MESSAGE.formatted(errors));
    }
}
