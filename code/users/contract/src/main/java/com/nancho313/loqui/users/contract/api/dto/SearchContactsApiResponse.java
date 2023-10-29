package com.nancho313.loqui.users.contract.api.dto;

import java.util.List;

public record SearchContactsApiResponse(List<ContactApiDto> contacts) {
}
