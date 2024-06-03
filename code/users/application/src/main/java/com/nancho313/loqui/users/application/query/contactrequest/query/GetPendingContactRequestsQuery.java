package com.nancho313.loqui.users.application.query.contactrequest.query;

import com.nancho313.loqui.users.application.query.Query;
import jakarta.validation.constraints.NotBlank;

public record GetPendingContactRequestsQuery(
    @NotBlank(message = "The user id cannot be empty.") String userId) implements Query {
}
