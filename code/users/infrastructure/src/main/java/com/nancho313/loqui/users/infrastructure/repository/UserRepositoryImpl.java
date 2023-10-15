package com.nancho313.loqui.users.infrastructure.repository;

import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.UserMongodbDAO;
import com.nancho313.loqui.users.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMongodbDAO dao;

    private final UserMapper mapper;

    @Override
    public User save(User user) {
        return mapper.toEntity(dao.save(mapper.toDocument(user)));
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return dao.findById(userId.id()).map(mapper::toEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return dao.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return dao.existsByEmail(email);
    }
}
