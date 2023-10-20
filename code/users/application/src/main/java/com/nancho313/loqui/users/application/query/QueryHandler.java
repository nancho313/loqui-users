package com.nancho313.loqui.users.application.query;

import com.nancho313.loqui.users.application.exception.InvalidCommandDataException;
import com.nancho313.loqui.users.application.exception.InvalidResponseDataException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

public abstract class QueryHandler<Q extends Query, R extends QueryResponse> {

    private final Validator validator;

    public QueryHandler(Validator validator) {
        this.validator = validator;
    }

    public R execute(Q query) {

        validateQuery(query);
        var response = executeQuery(query);
        validateResponse(response);
        return response;
    }

    protected abstract R executeQuery(Q query);

    private void validateQuery(Q data) {

        var errors = validateData(data);
        if (!errors.isEmpty()) {

            throw new InvalidCommandDataException(errors);
        }
    }

    private void validateResponse(R data) {

        var errors = validateData(data);
        if (!errors.isEmpty()) {
            throw new InvalidResponseDataException(errors);
        }
    }

    private <Y> List<String> validateData(Y data) {

        Set<ConstraintViolation<Y>> violations = validator.validate(data);
        return violations.stream().map(ConstraintViolation::getMessage).toList();
    }
}
