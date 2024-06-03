package com.nancho313.loqui.users.application.exception;

import java.util.List;

public class InvalidInputDataException extends RuntimeException {

  private static final String INVALID_INPUT_DATA_ERROR_MESSAGE = "The input data has invalid values. Error -> %s";

  public InvalidInputDataException(List<String> errors) {

    super(INVALID_INPUT_DATA_ERROR_MESSAGE.formatted(errors));
  }
}
