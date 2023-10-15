package com.nancho313.loqui.users.infrastructure.client.mongodb.dao;

import com.nancho313.loqui.users.infrastructure.client.mongodb.document.AuthUserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthUserMongodbDAO extends MongoRepository<AuthUserDocument, String> {

}
