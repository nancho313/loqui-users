package com.nancho313.loqui.users.contract.api.dto;

import java.util.List;

public record SearchUserApiResponse(List<UserApiDto> users) {

    public record UserApiDto(String id, String username, String email) {
    }
}
