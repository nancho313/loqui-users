package com.nancho313.loqui.users.domain.repository;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.IdUser;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(IdUser id);
}
