package com.nancho313.loqui.users.infrastructure.client.mongodb.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("user")
public record UserDocument(@Id String id,
                           String username,
                           String password,
                           String email,
                           LocalDateTime creationDate,
                           LocalDateTime lastUpdatedDate) {
}
