package com.nancho313.loqui.users.application.query.user.query;

import com.nancho313.loqui.users.application.query.Query;
import jakarta.validation.constraints.NotBlank;

public record SearchContactsQuery(@NotBlank(message = "The user id cannot be empty.") String userId) implements Query {
}
