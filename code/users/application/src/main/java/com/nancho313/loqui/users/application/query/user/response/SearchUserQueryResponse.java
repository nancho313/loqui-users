package com.nancho313.loqui.users.application.query.user.response;

import com.nancho313.loqui.users.application.query.QueryResponse;
import com.nancho313.loqui.users.application.query.user.dto.UserResultDto;
import jakarta.validation.Valid;

import java.util.List;

public record SearchUserQueryResponse(@Valid List<UserResultDto> users) implements QueryResponse {
}
