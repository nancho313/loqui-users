package com.nancho313.loqui.users.infrastructure.externalservice;

import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class IdGeneratorImpl implements IdGenerator {

    @Override
    public String generateId() {
        return new ObjectId().toHexString();
    }
}
