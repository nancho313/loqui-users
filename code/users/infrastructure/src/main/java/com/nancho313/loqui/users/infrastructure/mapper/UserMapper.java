package com.nancho313.loqui.users.infrastructure.mapper;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.infrastructure.client.neo4j.node.UserNode;
import com.nancho313.loqui.users.projection.model.UserModel;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = ContactMapper.class)
public interface UserMapper {

  @Mapping(target = "id", source = "id.id")
  @Mapping(target = "creationDate", source = "currentDate.creationDate")
  @Mapping(target = "lastUpdatedDate", source = "currentDate.lastUpdatedDate")
  UserNode toNode(User entity);

  @Mapping(source = "id", target = "id.id")
  @Mapping(source = "creationDate", target = "currentDate.creationDate")
  @Mapping(source = "lastUpdatedDate", target = "currentDate.lastUpdatedDate")
  User toEntity(UserNode node);

  UserModel toProjection(UserNode writableUserNode);
}
