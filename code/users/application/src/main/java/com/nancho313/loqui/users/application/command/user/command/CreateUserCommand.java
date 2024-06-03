package com.nancho313.loqui.users.application.command.user.command;

import com.nancho313.loqui.users.application.command.Command;
import jakarta.validation.constraints.NotBlank;

public record CreateUserCommand(@NotBlank(message = "The user id cannot be empty.") String userId,
                                @NotBlank(message = "The username cannot be empty.") String username,
                                @NotBlank(message = "The email cannot be empty.") String email) implements Command {
}
