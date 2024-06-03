package com.nancho313.loqui.users.test.contract.util;

import com.nancho313.loqui.users.application.command.Command;
import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.CommandResponse;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.event.EventResolver;
import com.nancho313.loqui.users.domain.event.EventResolverFactory;
import jakarta.validation.Validation;
import lombok.Getter;

import java.util.Optional;

@Getter
public class CommandHandlerTestUtil<T extends Command, V extends CommandResponse> extends CommandHandler<T, V> {

  private T commandToProcess;

  private V responseToReturn;

  public CommandHandlerTestUtil() {
    super(Validation.buildDefaultValidatorFactory().getValidator(), new EventResolverFactory() {
      @Override
      public <T extends DomainEvent> Optional<EventResolver<T>> getResolver(T event) {
        return Optional.empty();
      }
    });
  }

  public void initResponse(V responseToReturn) {
    this.responseToReturn = responseToReturn;
  }

  @Override
  protected HandleCommandResult<V> handleCommand(T command) {
    commandToProcess = command;
    return buildEventlessResult(responseToReturn);
  }
}
