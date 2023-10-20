package com.nancho313.loqui.users.infrastructure.client.mongodb.dao;

import com.nancho313.loqui.users.infrastructure.client.mongodb.document.UserDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

@Repository
public class UserMongodbReadOnlyDAO {

    private final MongoTemplate mongoTemplate;

    public UserMongodbReadOnlyDAO(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<UserDocument> searchUsersByUsername(String username) {

        var query = Query.query(Criteria.where(UserDocument.USERNAME_FIELD).is(Pattern.compile(username)));
        return mongoTemplate.find(query, UserDocument.class);
    }

    public List<UserDocument> searchUsersByEmail(String email) {

        var query = Query.query(Criteria.where(UserDocument.EMAIL_FIELD).is(Pattern.compile(email)));
        return mongoTemplate.find(query, UserDocument.class);
    }
}
