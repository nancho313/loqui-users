package com.nancho313.loqui.users.test.application.util;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;

import java.util.*;

public class UserRepositorySpy implements UserRepository {
  
  private final List<User> data = new ArrayList<>();
  
  public User save(User user) {
    data.removeIf(value -> value.getId().equals(user.getId()));
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

    var optionalUser = data.stream().filter(value -> value.getId().equals(userId)).findFirst();
    if(optionalUser.isPresent()) {

      var user = optionalUser.get().addContact(contactId);
      this.save(user);
    }
  }
}
