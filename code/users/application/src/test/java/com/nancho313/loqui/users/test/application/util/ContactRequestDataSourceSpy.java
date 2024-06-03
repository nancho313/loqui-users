package com.nancho313.loqui.users.test.application.util;

import com.nancho313.loqui.users.projection.datasource.ContactRequestDataSource;
import com.nancho313.loqui.users.projection.model.ContactRequestModel;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactRequestDataSourceSpy implements ContactRequestDataSource {

  private List<ContactRequestModel> data = new ArrayList<>();

  public void initDataSource(List<ContactRequestModel> newData) {

    if(!CollectionUtils.isEmpty(newData)){

      data.addAll(newData);
    }
  }

  @Override
  public List<ContactRequestModel> getContactRequests(String idUser, String status) {

    return data.stream().filter(value -> (value.requesterUser().equals(idUser) || value.requestedUser().equals(idUser)) && value.status().equals(status)).toList();
  }
}
