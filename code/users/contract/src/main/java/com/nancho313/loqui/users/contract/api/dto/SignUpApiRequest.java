package com.nancho313.loqui.users.contract.api.dto;

public record SignUpApiRequest(String username, String password, String email) implements ApiRequest {
}
