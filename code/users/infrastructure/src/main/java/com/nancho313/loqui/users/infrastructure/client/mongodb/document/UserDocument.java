package com.nancho313.loqui.users.infrastructure.client.mongodb.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("user")
public record UserDocument(@Id String id,
                           @Field(UserDocument.USERNAME_FIELD) String username,
                           @Field(UserDocument.EMAIL_FIELD) String email,
                           LocalDateTime creationDate,
                           LocalDateTime lastUpdatedDate) {

    public static final String USERNAME_FIELD = "username";
    public static final String EMAIL_FIELD = "email";
}
