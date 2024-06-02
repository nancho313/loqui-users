package com.nancho313.loqui.users.application.command.contactrequest.command;

import com.nancho313.loqui.users.application.command.Command;
import jakarta.validation.constraints.NotBlank;

public record AddNewContactCommand(@NotBlank(message = "The user id cannot be empty.") String userId,
                                   @NotBlank(message = "The contact id cannot be empty.") String contactId,
                                   @NotBlank(message = "The initial message cannot be empty.") String initialMessage) implements Command {
}
