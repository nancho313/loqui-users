package com.nancho313.loqui.users.infrastructure.client.neo4j.dao;

import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.ContactDto;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.UserContactDto;
import com.nancho313.loqui.users.infrastructure.client.neo4j.node.UserNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserNeo4jDAO {
  
  private static final String CREATE_QUERY = """
          MERGE (n:User
            {
              id: "%s",
              username: "%s",
              email: "%s",
              creationDate: localdatetime("%s"),
              lastUpdatedDate: localdatetime("%s")
            }
          )
          RETURN n;
          """;
  private static final String UPDATE_QUERY = """
          MATCH (n:User {id: "%s"})
          SET n = {
                    username: "%s",
                    email: "%s",
                    creationDate: localdatetime("%s"),
                    lastUpdatedDate: localdatetime("%s")
                  }
          RETURN n;
          """;
  
  private final Neo4jClient neo4jClient;
  
  public UserNode save(UserNode userNode) {
    
    Collection<Map<String, Object>> result;
    
    if (existsById(userNode.id())) {
      
      result = neo4jClient.query(UPDATE_QUERY.formatted(userNode.id(), userNode.username(), userNode.email(),
              userNode.creationDate(),
              userNode.lastUpdatedDate())).fetch().all();
    } else {
      result = neo4jClient.query(CREATE_QUERY.formatted(userNode.id(), userNode.username(), userNode.email(),
              userNode.creationDate(),
              userNode.lastUpdatedDate())).fetch().all();
    }
    
    return asUserNodes(result).get(0);
  }
  
  public void addContact(String principal, String target, String status) {
    
    var query = """
            MATCH (a:User WHERE a.id = '%s' )
            MATCH (b:User WHERE b.id = '%s' )
            MERGE (a)-[:CONTACT {status: '%s' }]-(b)
            RETURN a;
            """;
    
    neo4jClient.query(query.formatted(principal, target, status)).run();
  }
  
  public boolean existsByUsername(String username) {
    
    var query = """
            MATCH (n:User {username: '%s'})
            RETURN n.id;
            """;
    return neo4jClient.query(query.formatted(username)).fetch().one().isPresent();
  }
  
  public boolean existsByEmail(String email) {
    
    var query = """
            MATCH (n:User {email: '%s'})
            RETURN n.id;
            """;
    return neo4jClient.query(query.formatted(email)).fetch().one().isPresent();
  }
  
  public Optional<UserNode> findById(String id) {
    
    var query = """
            MATCH (n:User {id: '%s'})
            OPTIONAL MATCH (b:User)-[r:CONTACT]-(n)
            RETURN n,r,b;
            """;
    
    var result = asUserNodesWithContacts(neo4jClient.query(query.formatted(id)).fetch().all());
    return result.stream().findFirst();
  }
  
  public boolean existsById(String id) {
    
    var query = """
            MATCH (n:User {id: '%s'})
            RETURN n.id;
            """;
    return neo4jClient.query(query.formatted(id)).fetch().one().isPresent();
  }
  
  public List<UserNode> searchUsersByUsername(String username) {
    
    var query =
            """
                    MATCH (n:User)
                    WHERE n.username CONTAINS '%s'
                    RETURN n;
                    """.formatted(username);
    
    return asUserNodes(neo4jClient.query(query).fetch().all());
    
  }
  
  public List<UserNode> searchUsersByEmail(String email) {
    
    var query =
            """
                    MATCH (n:User)
                    WHERE n.email CONTAINS '%s'
                    RETURN n;
                    """.formatted(email);
    
    return asUserNodes(neo4jClient.query(query).fetch().all());
  }
  
  public List<UserContactDto> searchContactsFromUser(String idUser) {
    var query =
            """
                    MATCH (p:User {id: '%s'})
                    MATCH (n:User)-[r:CONTACT]-(p)
                    RETURN r,n;
                    """.formatted(idUser);
    
    return asContacts(neo4jClient.query(query).fetch().all());
  }
  
  private List<UserContactDto> asContacts(Collection<Map<String, Object>> listOfValues) {
    
    List<UserContactDto> result = new ArrayList<>();
    for (Map<String, Object> value : listOfValues) {
      
      InternalNode contact = (InternalNode) value.get("n");
      InternalRelationship relationship = (InternalRelationship) value.get("r");
      var username = contact.get("username").asString();
      var id = contact.get("id").asString();
      var email = contact.get("email").asString();
      var status = relationship.get("status").asString();
      result.add(new UserContactDto(id, username, email, status));
    }
    
    return result;
  }
  
  private List<UserNode> asUserNodes(Collection<Map<String, Object>> listOfValues) {
    
    List<UserNode> result;
    Map<String, InternalNode> users = new HashMap<>();
    
    for (Map<String, Object> value : listOfValues) {
      
      InternalNode user = (InternalNode) value.get("n");
      var idUser = user.get("id").asString();
      users.put(idUser, user);
    }
    
    result = users.values().stream().map(userNode -> {
      var username = userNode.get("username").asString();
      var id = userNode.get("id").asString();
      var email = userNode.get("email").asString();
      var creationDate = userNode.get("creationDate").asLocalDateTime();
      var lastUpdatedDate = userNode.get("lastUpdatedDate").asLocalDateTime();
      return new UserNode(id, username, email, Collections.emptyList(), creationDate, lastUpdatedDate);
    }).toList();
    return result;
  }
  
  private List<UserNode> asUserNodesWithContacts(Collection<Map<String, Object>> listOfValues) {
    
    List<UserNode> result;
    Map<String, InternalNode> users = new HashMap<>();
    Map<String, List<ContactDto>> contactsByUser = new HashMap<>();
    
    for (Map<String, Object> value : listOfValues) {
      
      InternalNode user = (InternalNode) value.get("n");
      var idUser = user.get("id").asString();
      users.put(idUser, user);
      
      contactsByUser.computeIfAbsent(idUser, k -> new ArrayList<>());
      
      var relationsData = Optional.ofNullable(value.get("r")).map(InternalRelationship.class::cast);
      
      var contactData = Optional.ofNullable(value.get("b")).map(InternalNode.class::cast);
      
      if (relationsData.isPresent() && contactData.isPresent()) {
        
        contactsByUser.get(idUser).add(new ContactDto(contactData.get().get("id").asString(),
                relationsData.get().get("status").asString()));
      }
    }
    
    result = users.values().stream().map(userNode -> {
      var username = userNode.get("username").asString();
      var id = userNode.get("id").asString();
      var email = userNode.get("email").asString();
      var creationDate = userNode.get("creationDate").asLocalDateTime();
      var lastUpdatedDate = userNode.get("lastUpdatedDate").asLocalDateTime();
      
      return new UserNode(id, username, email, contactsByUser.get(id), creationDate, lastUpdatedDate);
    }).toList();
    return result;
  }
}
