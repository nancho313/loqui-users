package com.nancho313.loqui.users.application.query.contactrequest.query;

import com.nancho313.loqui.users.application.query.Query;

public record GetPendingContactRequestsQuery(String idUser) implements Query {
}
