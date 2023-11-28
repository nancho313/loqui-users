package com.nancho313.loqui.users.test.application.util;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;

import java.util.*;

public class UserRepositorySpy implements UserRepository {
  
  private final Set<User> data = new HashSet<>();
  
  public User save(User user) {
    data.add(user);
    return user;
  }
  
  public Optional<User> findById(UserId id) {
    
    return data.stream().filter(user -> user.getId().equals(id)).findFirst();
  }
  
  public boolean existsByUsername(String username) {
    return data.stream().anyMatch(user -> user.getUsername().equals(username));
  }
  
  public boolean existsByEmail(String email) {
    return data.stream().anyMatch(user -> user.getEmail().equals(email));
  }
  
  public boolean existsById(UserId requestedUserId) {
    return data.stream().anyMatch(user -> user.getId().equals(requestedUserId));
  }
  
  public void addContact(UserId userId, UserId contactId) {
  
    throw new UnsupportedOperationException("Not supported, yet");
  }
}
