package com.nancho313.loqui.users.test.application.util;

import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import com.nancho313.loqui.users.projection.model.ContactModel;
import com.nancho313.loqui.users.projection.model.UserModel;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataSourceSpy implements UserDataSource {

  private List<UserModel> data = new ArrayList<>();

  private Map<String, List<ContactModel>> contactData = new HashMap<>();

  public void initDataSource(List<UserModel> newData, Map<String, List<ContactModel>> newContactData) {

    if (!CollectionUtils.isEmpty(newData)) {

      data.addAll(newData);
    }

    if(contactData != null) {

      contactData = newContactData;
    }
  }

  @Override
  public List<UserModel> searchUsersByUsername(String username) {
    return data.stream().filter(value -> value.username().contains(username)).toList();
  }

  @Override
  public List<UserModel> searchUsersByEmail(String email) {
    return data.stream().filter(value -> value.email().contains(email)).toList();
  }

  @Override
  public List<ContactModel> searchContacts(String userId) {
    return contactData.getOrDefault(userId, List.of());
  }
}
