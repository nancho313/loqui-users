package com.nancho313.loqui.users.infrastructure.datasource;

import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.ContactRequestMongodbDAO;
import com.nancho313.loqui.users.infrastructure.mapper.ContactRequestMapper;
import com.nancho313.loqui.users.projection.datasource.ContactRequestDataSource;
import com.nancho313.loqui.users.projection.model.ContactRequestModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContactRequestDataSourceImpl implements ContactRequestDataSource {
  
  private final ContactRequestMongodbDAO dao;
  
  private final ContactRequestMapper mapper;
  
  public List<ContactRequestModel> getContactRequests(String idUser, String status) {
    return dao.findByIdUserAndStatus(idUser, status).stream().map(mapper::toProjection).toList();
  }
}
