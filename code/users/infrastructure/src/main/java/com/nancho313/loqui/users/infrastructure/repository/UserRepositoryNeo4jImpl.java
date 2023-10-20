package com.nancho313.loqui.users.infrastructure.repository;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.ContactStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dao.UserNeo4jDAO;
import com.nancho313.loqui.users.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryNeo4jImpl implements UserRepository {
  
  private final UserNeo4jDAO dao;
  
  private final UserMapper mapper;
  
  public User save(User user) {
    return mapper.toEntity(dao.save(mapper.toNode(user)));
  }
  
  public Optional<User> findById(UserId id) {
    return dao.findById(id.id()).map(mapper::toEntity);
  }
  
  public boolean existsByUsername(String username) {
    return dao.existsByUsername(username);
  }
  
  public boolean existsByEmail(String email) {
    return dao.existsByEmail(email);
  }
  
  public boolean existsById(UserId requestedUserId) {
    return dao.existsById(requestedUserId.id());
  }
  
  public void addContact(UserId userId, UserId contactId) {
    
    dao.addContact(userId.id(), contactId.id(), ContactStatus.AVAILABLE.name());
  }
}
