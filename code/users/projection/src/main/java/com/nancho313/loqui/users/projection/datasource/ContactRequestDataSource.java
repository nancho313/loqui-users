package com.nancho313.loqui.users.projection.datasource;

import com.nancho313.loqui.users.projection.model.ContactRequestModel;

import java.util.List;

public interface ContactRequestDataSource {
  
  List<ContactRequestModel> getContactRequests(String idUser, String status);
}
