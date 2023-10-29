package com.nancho313.loqui.users.infrastructure.client.neo4j.dto;

public record UserContactDto(String id,
                             String username,
                             String email,
                             String status) {
}
