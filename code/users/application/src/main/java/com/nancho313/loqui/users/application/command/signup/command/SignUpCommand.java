package com.nancho313.loqui.users.application.command.signup.command;

import com.nancho313.loqui.users.application.command.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpCommand(@NotBlank(message = "The username cannot be empty.") String username,
                            @NotBlank(message = "The password cannot be empty.") String password,
                            @Pattern(regexp = "^\\S+@\\S+\\.\\S+$", message = "The email is not valid.")
                            @NotBlank(message = "The email cannot be empty.") String email) implements Command {
}
