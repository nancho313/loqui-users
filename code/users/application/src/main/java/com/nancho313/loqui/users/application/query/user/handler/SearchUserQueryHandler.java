package com.nancho313.loqui.users.application.query.user.handler;

import com.nancho313.loqui.users.application.query.QueryHandler;
import com.nancho313.loqui.users.application.query.user.dto.UserResultDto;
import com.nancho313.loqui.users.application.query.user.query.SearchUserQuery;
import com.nancho313.loqui.users.application.query.user.response.SearchUserQueryResponse;
import com.nancho313.loqui.users.projection.datasource.UserDataSource;
import com.nancho313.loqui.users.projection.model.UserModel;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nancho313.loqui.commons.ObjectValidator.isNotAnEmptyString;

@Service
public class SearchUserQueryHandler extends QueryHandler<SearchUserQuery, SearchUserQueryResponse> {

    private final UserDataSource userDataSource;

    public SearchUserQueryHandler(Validator validator, UserDataSource userDataSource) {
        super(validator);
        this.userDataSource = userDataSource;
    }

    @Override
    protected SearchUserQueryResponse executeQuery(SearchUserQuery query) {

        Set<UserModel> result = new HashSet<>();

        if (isNotAnEmptyString(query.username())) {

            result.addAll(userDataSource.searchUsersByUsername(query.username()));
        }

        if (isNotAnEmptyString(query.email())) {

            result.addAll(userDataSource.searchUsersByEmail(query.email()));
        }

        return new SearchUserQueryResponse(result.stream().map(this::toUserResult).toList());
    }

    private UserResultDto toUserResult(UserModel userModel) {
        return new UserResultDto(userModel.id(), userModel.username(), userModel.email());
    }
}
