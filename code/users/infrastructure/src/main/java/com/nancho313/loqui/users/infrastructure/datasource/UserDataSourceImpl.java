package com.nancho313.loqui.users.infrastructure.datasource;

import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.UserMongodbReadOnlyDAO;
import com.nancho313.loqui.users.infrastructure.mapper.UserMapper;
import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import com.nancho313.loqui.users.projection.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDataSourceImpl implements UserDataSource {

    private final UserMongodbReadOnlyDAO dao;

    private final UserMapper mapper;

    @Override
    public List<UserModel> searchUsersByUsername(String username) {
        return dao.searchUsersByUsername(username).stream().map(mapper::toProjection).toList();
    }

    @Override
    public List<UserModel> searchUsersByEmail(String email) {
        return dao.searchUsersByEmail(email).stream().map(mapper::toProjection).toList();
    }
}
