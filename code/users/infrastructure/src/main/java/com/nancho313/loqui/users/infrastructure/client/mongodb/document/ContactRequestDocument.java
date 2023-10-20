package com.nancho313.loqui.users.infrastructure.client.mongodb.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("contact_request")
public record ContactRequestDocument(@Id String id, String requesterUser, String requestedUser,
                                     LocalDateTime creationDate, LocalDateTime lastUpdatedDate,
                                     String status, String message) {
  
}
