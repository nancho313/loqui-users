package com.nancho313.loqui.users.application.query.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserResultDto(@NotBlank(message = "The id cannot be empty.") String id,
                            @NotBlank(message = "The username cannot be empty.") String username,
                            @NotBlank(message = "The email cannot be empty.") String email) {
}
