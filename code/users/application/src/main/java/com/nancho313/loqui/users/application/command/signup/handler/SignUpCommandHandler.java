package com.nancho313.loqui.users.application.command.signup.handler;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.signup.command.SignUpCommand;
import com.nancho313.loqui.users.application.command.signup.response.SignUpResponse;
import com.nancho313.loqui.users.domain.aggregate.User;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.externalservice.UserAuthenticator;
import com.nancho313.loqui.users.domain.repository.UserRepository;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SignUpCommandHandler extends CommandHandler<SignUpCommand, SignUpResponse> {

    private final UserRepository repository;

    private final UserAuthenticator userAuthenticator;

    private final IdGenerator idGenerator;

    public SignUpCommandHandler(Validator validator, UserRepository repository, UserAuthenticator userAuthenticator, IdGenerator idGenerator) {
        super(validator);
        this.repository = repository;
        this.userAuthenticator = userAuthenticator;
        this.idGenerator = idGenerator;
    }

    @Override
    protected HandleCommandResult<SignUpResponse> handleCommand(SignUpCommand command) {

        if (repository.existsByUsername(command.username())) {

            throw new IllegalArgumentException("The username %s is already used.".formatted(command.username()));
        }

        if (repository.existsByEmail(command.email())) {

            throw new IllegalArgumentException("The email %s is already used.".formatted(command.email()));
        }

        var createdUser = User.createUser(idGenerator, command.username(), command.email());
        var user = repository.save(createdUser);
        userAuthenticator.storeBasicCredentials(user.getId(), user.getUsername(), command.password());
        return buildResult(new SignUpResponse(createdUser.getId().id()), user.getCurrentEvents());
    }

    @Override
    protected void processEvents(List<DomainEvent> events) {

        log.info("Processing some events {}", events);
    }
}
