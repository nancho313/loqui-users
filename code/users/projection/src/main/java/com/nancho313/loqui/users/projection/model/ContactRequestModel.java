package com.nancho313.loqui.users.projection.model;

import java.time.LocalDateTime;

public record ContactRequestModel(String id, String requestedUser, String requesterUser, String message,
                                  LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String status) {
}
