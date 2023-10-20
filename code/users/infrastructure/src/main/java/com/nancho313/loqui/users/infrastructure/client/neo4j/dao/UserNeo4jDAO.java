package com.nancho313.loqui.users.infrastructure.client.neo4j.dao;

import com.nancho313.loqui.users.infrastructure.client.neo4j.document.UserNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.internal.value.NodeValue;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.PreparedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserNeo4jDAO {
  
  private final Neo4jTemplate neo4jTemplate;
  
  public UserNode save(UserNode userNode) {
    
    return neo4jTemplate.save(userNode);
  }
  
  public void addContact(String principal, String target, String status) {
    
    var cypherQuery = """
            MATCH (a:User WHERE a.id = $principal )
            MATCH (b:User WHERE b.id = $target )
            MERGE (a)-[:CONTACT {status: $status }]-(b)
            RETURN a
            """;
    
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("principal", principal);
    parameters.put("target", target);
    parameters.put("status", status);
    
    var preparedQuery =
            PreparedQuery.queryFor(NodeValue.class).withCypherQuery(cypherQuery).withParameters(parameters).build();
    
    var value = neo4jTemplate.toExecutableQuery(preparedQuery).getSingleResult();
    log.info("Value -> " + value);
    
  }
  
  public boolean existsByUsername(String username) {
    
    var cypherQuery = """
            MATCH (a:User WHERE a.username = $username)
            """;
    
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("username", username);
    var result = neo4jTemplate.findAll(cypherQuery, parameters, UserNode.class);
    return !result.isEmpty();
  }
  
  public boolean existsByEmail(String email) {
    
    var cypherQuery = """
            MATCH (a:User WHERE a.email = $email)
            """;
    
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("email", email);
    var result = neo4jTemplate.findAll(cypherQuery, parameters, UserNode.class);
    return !result.isEmpty();
  }
  
  public Optional<UserNode> findById(String id) {
    
    return neo4jTemplate.findById(id, UserNode.class);
  }
  
  public boolean existsById(String id) {
    
    return neo4jTemplate.existsById(id, UserNode.class);
  }
  
}
