package com.nancho313.loqui.users.application.command;

import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.application.exception.InvalidResponseDataException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

public abstract class CommandHandler<T extends Command, V extends Response> {

    private final Validator validator;

    public CommandHandler(Validator validator) {
        this.validator = validator;
    }

    public V handle(T command) {

        validateCommand(command);
        var result = handleCommand(command);
        validateResponse(result);
        return result;
    }

    protected abstract V handleCommand(T command);

    private void validateCommand(T data) {

        var errors = validateData(data);
        if (!errors.isEmpty()) {

            throw new InvalidCommandDataException(errors);
        }
    }

    private void validateResponse(V data) {

        var errors = validateData(data);
        if (!errors.isEmpty()) {
            throw new InvalidResponseDataException(errors);
        }
    }

    private <Y> List<String> validateData(Y data) {

        Set<ConstraintViolation<Y>> violations = validator.validate(data);
        return violations.stream().map(ConstraintViolation::getMessage).toList();
    }
}
