package com.nancho313.loqui.users.application.query.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactResultDto(@NotNull(message = "The user data cannot be null.") UserResultDto user,
                               @NotBlank(message = "The status cannot be empty.") String status) {
}
