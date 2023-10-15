package com.nancho313.loqui.users.domain.aggregate;

import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.IdUser;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nancho313.loqui.users.commons.validator.ObjectValidator.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class User extends DomainAggregate {

    private static final String ERROR_MESSAGE = "Cannot create an User object. Errors -> %s";

    IdUser id;

    String username;

    String password;

    String email;

    CurrentDate currentDate;

    public User(IdUser id, String username, String password, String email, CurrentDate currentDate) {
        this(Collections.emptyList(), id, username, password, email, currentDate);
    }

    User(List<DomainEvent> events, IdUser id, String username, String password, String email, CurrentDate currentDate) {
        super(events);
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.currentDate = currentDate;
        validate();
    }

    public static User createUser(IdGenerator idGenerator, String username, String password, String email) {

        if (isNull(idGenerator)) {
            throw new IllegalArgumentException("The id generator cannot be null.");
        }
        var id = new IdUser(idGenerator.generateId());
        var event = new CreatedUserEvent(id, username, email);
        return new User(List.of(event), id, username, password, email, CurrentDate.now());
    }

    private void validate() {

        List<String> errors = new ArrayList<>();

        if (isNull(id)) {

            errors.add("The id cannot be null.");
        }

        if (isEmptyString(username)) {

            errors.add("The username cannot be empty.");
        }

        if (isEmptyString(password)) {

            errors.add("The password cannot be empty.");
        }

        if (isEmptyString(email)) {

            errors.add("The email cannot be empty.");
        }

        if (!errors.isEmpty()) {

            throw new IllegalArgumentException(ERROR_MESSAGE.formatted(errors));
        }
    }

    private record CreatedUserEvent(IdUser id, String username, String email,
                                    LocalDateTime eventDate) implements DomainEvent {

        public CreatedUserEvent {

            assert (isNotNull(id));
            assert (isNotAnEmptyString(username));
            assert (isNotAnEmptyString(email));
            assert (isNotNull(eventDate));
        }

        public CreatedUserEvent(IdUser id, String username, String email) {

            this(id, username, email, LocalDateTime.now());
        }
    }

}
