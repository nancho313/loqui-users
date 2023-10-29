package com.nancho313.loqui.users.infrastructure.client.neo4j.node;

import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.ContactDto;

import java.time.LocalDateTime;
import java.util.List;

public record UserNode(String id,
                       String username,
                       String email,
                       List<ContactDto> contacts,
                       LocalDateTime creationDate,
                       LocalDateTime lastUpdatedDate) {
}
