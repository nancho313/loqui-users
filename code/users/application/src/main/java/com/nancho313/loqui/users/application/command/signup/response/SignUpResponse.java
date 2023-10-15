package com.nancho313.loqui.users.application.command.signup.response;

import com.nancho313.loqui.users.application.command.Response;
import jakarta.validation.constraints.NotBlank;

public record SignUpResponse(@NotBlank(message = "The id cannot be empty.") String id) implements Response {
}
