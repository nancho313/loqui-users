package com.nancho313.loqui.users.application.command;

import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.application.exception.InvalidResponseDataException;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.event.EventResolverFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class CommandHandler<T extends Command, V extends CommandResponse> {

  private final Validator validator;

  private final EventResolverFactory eventResolverFactory;

  public CommandHandler(Validator validator, EventResolverFactory eventResolverFactory) {
    this.validator = validator;
    this.eventResolverFactory = eventResolverFactory;
  }

  public V handle(T command) {

    validateCommand(command);
    var result = handleCommand(command);
    processEvents(result.events);
    validateResponse(result.response);
    return result.response;
  }

  protected abstract HandleCommandResult<V> handleCommand(T command);

  protected HandleCommandResult<V> buildResult(V response, List<DomainEvent> events) {

    return new HandleCommandResult<>(response, events);
  }

  protected HandleCommandResult<V> buildEventlessResult(V response) {

    return new HandleCommandResult<>(response, Collections.emptyList());
  }

  private void validateCommand(T data) {

    if (data == null) {

      throw new IllegalArgumentException("The command to process cannot be null.");
    }

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

  private void processEvents(List<DomainEvent> events) {

    events.forEach(event -> eventResolverFactory.getResolver(event)
            .ifPresent(domainEventResolver -> domainEventResolver.processEvent(event)));
  }

  protected record HandleCommandResult<V extends CommandResponse>(V response, List<DomainEvent> events) {
  }
}
