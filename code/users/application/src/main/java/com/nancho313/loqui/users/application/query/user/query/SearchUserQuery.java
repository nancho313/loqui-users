package com.nancho313.loqui.users.application.query.user.query;

import com.nancho313.loqui.users.application.query.Query;

public record SearchUserQuery(String username, String email) implements Query {
}
