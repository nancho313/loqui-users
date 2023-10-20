package com.nancho313.loqui.users.projection.datasource;

import com.nancho313.loqui.users.projection.model.UserModel;

import java.util.List;

public interface UserDataSource {

    List<UserModel> searchUsersByUsername(String username);

    List<UserModel> searchUsersByEmail(String email);
}
