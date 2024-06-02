package com.nancho313.loqui.users.application.command.contactrequest.command;

import com.nancho313.loqui.users.application.command.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProcessContactRequestCommand(@NotBlank(message = "The contact request id cannot be empty.") String contactRequestId,
                                           @NotBlank(message = "The requested user id cannot be empty.") String requestedUserId,
                                           @NotNull(message = "The accepted flag cannot be null.") Boolean accepted) implements Command {
}
