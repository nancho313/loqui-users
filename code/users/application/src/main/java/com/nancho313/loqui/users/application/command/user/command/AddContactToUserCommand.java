package com.nancho313.loqui.users.application.command.user.command;

import com.nancho313.loqui.users.application.command.Command;
import jakarta.validation.constraints.NotBlank;

public record AddContactToUserCommand(@NotBlank(message = "The user id cannot be empty.") String userId,
                                      @NotBlank(message = "The contact id cannot be empty.") String contactId) implements Command {
}
