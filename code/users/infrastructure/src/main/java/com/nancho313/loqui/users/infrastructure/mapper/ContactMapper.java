package com.nancho313.loqui.users.infrastructure.mapper;

import com.nancho313.loqui.users.domain.entity.Contact;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.ContactDto;
import com.nancho313.loqui.users.infrastructure.client.neo4j.dto.UserContactDto;
import com.nancho313.loqui.users.projection.model.ContactModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {
  
  @Mapping(source = "id", target = "id.id")
  Contact toEntity(ContactDto dto);
  
  @Mapping(target = "id", source = "id.id")
  ContactDto toDto(Contact entity);
  
  ContactModel toProjection(UserContactDto dto);
}
