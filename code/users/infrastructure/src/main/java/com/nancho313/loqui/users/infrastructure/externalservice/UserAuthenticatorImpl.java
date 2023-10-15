package com.nancho313.loqui.users.infrastructure.externalservice;

import com.nancho313.loqui.users.domain.externalservice.UserAuthenticator;
import com.nancho313.loqui.users.domain.vo.UserId;
import com.nancho313.loqui.users.infrastructure.client.mongodb.dao.AuthUserMongodbDAO;
import com.nancho313.loqui.users.infrastructure.client.mongodb.document.AuthUserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticatorImpl implements UserAuthenticator {

    private final AuthUserMongodbDAO authUserDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void storeBasicCredentials(UserId userId, String username, String password) {

        authUserDao.save(new AuthUserDocument(userId.id(), username, passwordEncoder.encode(password)));
    }
}
