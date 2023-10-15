package com.nancho313.loqui.users.application.command.signup.handler;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.signup.command.SignUpCommand;
import com.nancho313.loqui.users.application.command.signup.response.SignUpResponse;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class SignUpCommandHandler extends CommandHandler<SignUpCommand, SignUpResponse> {

    private final UserRepository repository;

    private final IdGenerator idGenerator;

    public SignUpCommandHandler(Validator validator, UserRepository repository, IdGenerator idGenerator) {
        super(validator);
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    protected SignUpResponse handleCommand(SignUpCommand command) {

        var createdUser = User.createUser(idGenerator, command.username(), command.password(), command.email());
        repository.save(createdUser);
        return new SignUpResponse(createdUser.getId().id());
    }
}
