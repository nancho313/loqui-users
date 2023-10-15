package com.nancho313.loqui.users.domain.vo;

import java.util.ArrayList;
import java.util.List;

import static com.nancho313.loqui.users.commons.validator.ObjectValidator.isEmptyString;

public record UserId(String id) {

    private static final String ERROR_MESSAGE = "Cannot create an UserId object. Errors -> %s";

    public UserId {

        List<String> errors = new ArrayList<>();
        if (isEmptyString(id)) {

            errors.add("The id cannot be empty.");
        }

        if (!errors.isEmpty()) {

            throw new IllegalArgumentException(ERROR_MESSAGE.formatted(errors));
        }
    }
}
