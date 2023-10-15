package com.nancho313.loqui.users.domain.repository;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.vo.UserId;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
