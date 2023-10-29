package com.nancho313.loqui.users.application.query.user.response;

import com.nancho313.loqui.users.application.query.QueryResponse;
import com.nancho313.loqui.users.application.query.user.dto.ContactResultDto;
import jakarta.validation.Valid;

import java.util.List;

public record SearchContactsQueryResponse(@Valid List<ContactResultDto> contacts) implements QueryResponse {
}
