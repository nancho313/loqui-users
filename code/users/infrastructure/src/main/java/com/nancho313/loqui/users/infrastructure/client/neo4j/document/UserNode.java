package com.nancho313.loqui.users.infrastructure.client.neo4j.document;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.Set;

@Node("User")
public record UserNode(@Id String id,
                       String username,
                       String email,
                       LocalDateTime creationDate,
                       LocalDateTime lastUpdatedDate) {
}
