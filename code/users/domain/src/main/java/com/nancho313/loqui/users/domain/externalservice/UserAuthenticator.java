package com.nancho313.loqui.users.domain.externalservice;

import com.nancho313.loqui.users.domain.vo.UserId;

public interface UserAuthenticator {

    void storeBasicCredentials(UserId userId, String username, String password);
}
