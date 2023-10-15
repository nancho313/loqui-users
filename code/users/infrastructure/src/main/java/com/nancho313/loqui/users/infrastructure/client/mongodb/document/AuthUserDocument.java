package com.nancho313.loqui.users.infrastructure.client.mongodb.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("authuser")
public record AuthUserDocument(@Id String id, String username, String password) {
}
