package com.nancho313.loqui.users.application.command;

import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.application.exception.InvalidResponseDataException;
import com.nancho313.loqui.users.domain.event.DomainEvent;
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
        processEvents(result.events);
        validateResponse(result.response);
        return result.response;
    }

    protected abstract HandleCommandResult<V> handleCommand(T command);

    protected abstract void processEvents(List<DomainEvent> events);

    protected HandleCommandResult<V> buildResult(V response, List<DomainEvent> events) {

        return new HandleCommandResult<>(response, events);
    }

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

    protected record HandleCommandResult <V extends Response> (V response, List<DomainEvent> events) {}
}
