package com.nancho313.loqui.users.infrastructure.datasource;

import com.nancho313.loqui.users.infrastructure.client.neo4j.dao.UserNeo4jDAO;
import com.nancho313.loqui.users.infrastructure.mapper.ContactMapper;
import com.nancho313.loqui.users.infrastructure.mapper.UserMapper;
import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import com.nancho313.loqui.users.projection.model.ContactModel;
import com.nancho313.loqui.users.projection.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDataSourceImpl implements UserDataSource {
  
  private final UserNeo4jDAO dao;
  
  private final UserMapper userMapper;
  
  private final ContactMapper contactMapper;
  
  @Override
  public List<UserModel> searchUsersByUsername(String username) {
    return dao.searchUsersByUsername(username).stream().map(userMapper::toProjection).toList();
  }
  
  @Override
  public List<UserModel> searchUsersByEmail(String email) {
    return dao.searchUsersByEmail(email).stream().map(userMapper::toProjection).toList();
  }
  
  @Override
  public List<ContactModel> searchContacts(String idUser) {
    return dao.searchContactsFromUser(idUser).stream().map(contactMapper::toProjection).toList();
  }
}
