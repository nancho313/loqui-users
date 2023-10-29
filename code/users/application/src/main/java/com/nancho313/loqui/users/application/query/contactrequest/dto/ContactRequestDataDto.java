package com.nancho313.loqui.users.application.query.contactrequest.dto;

import java.time.LocalDateTime;

public record ContactRequestDataDto(String id, String requestedUser, String requesterUser, String message,
                                    LocalDateTime creationDate) {
}