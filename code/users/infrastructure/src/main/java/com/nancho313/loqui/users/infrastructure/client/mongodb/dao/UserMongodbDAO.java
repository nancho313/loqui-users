package com.nancho313.loqui.users.infrastructure.client.mongodb.dao;

import com.nancho313.loqui.users.infrastructure.client.mongodb.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongodbDAO extends MongoRepository<UserDocument, String> {
}
