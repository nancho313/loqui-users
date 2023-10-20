package com.nancho313.loqui.users.infrastructure.mapper;

import com.nancho313.loqui.users.domain.aggregate.ContactRequest;
import com.nancho313.loqui.users.infrastructure.client.mongodb.document.ContactRequestDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactRequestMapper {
  
  @Mapping(source = "id", target = "contactRequestId.id")
  @Mapping(source = "requesterUser", target = "requesterUser.id")
  @Mapping(source = "requestedUser", target = "requestedUser.id")
  @Mapping(source = "creationDate", target = "currentDate.creationDate")
  @Mapping(source = "lastUpdatedDate", target = "currentDate.lastUpdatedDate")
  ContactRequest toEntity(ContactRequestDocument document);
  
  @Mapping(target = "id", source = "contactRequestId.id")
  @Mapping(target = "requesterUser", source = "requesterUser.id")
  @Mapping(target = "requestedUser", source = "requestedUser.id")
  @Mapping(target = "creationDate", source = "currentDate.creationDate")
  @Mapping(target = "lastUpdatedDate", source = "currentDate.lastUpdatedDate")
  ContactRequestDocument toDocument(ContactRequest entity);
}
