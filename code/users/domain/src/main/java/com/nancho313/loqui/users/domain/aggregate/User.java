package com.nancho313.loqui.users.domain.aggregate;

import com.nancho313.loqui.users.domain.event.DomainEvent;
import com.nancho313.loqui.users.domain.externalservice.IdGenerator;
import com.nancho313.loqui.users.domain.vo.CurrentDate;
import com.nancho313.loqui.users.domain.vo.UserId;
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

    UserId id;

    String username;

    String email;

    CurrentDate currentDate;

    public User(UserId id, String username, String email, CurrentDate currentDate) {
        this(Collections.emptyList(), id, username, email, currentDate);
    }

    User(List<DomainEvent> events, UserId id, String username, String email, CurrentDate currentDate) {
        super(events);
        this.id = id;
        this.username = username;
        this.email = email;
        this.currentDate = currentDate;
        validate();
    }

    public static User createUser(IdGenerator idGenerator, String username, String email) {

        if (isNull(idGenerator)) {
            throw new IllegalArgumentException("The id generator cannot be null.");
        }
        var id = new UserId(idGenerator.generateId());
        var event = new CreatedUserEvent(id, username, email);
        return new User(List.of(event), id, username, email, CurrentDate.now());
    }

    private void validate() {

        List<String> errors = new ArrayList<>();

        if (isNull(id)) {

            errors.add("The id cannot be null.");
        }

        if (isEmptyString(username)) {

            errors.add("The username cannot be empty.");
        }

        if (isEmptyString(email)) {

            errors.add("The email cannot be empty.");
        }

        if (!errors.isEmpty()) {

            throw new IllegalArgumentException(ERROR_MESSAGE.formatted(errors));
        }
    }

    private record CreatedUserEvent(UserId id, String username, String email,
                                    LocalDateTime eventDate) implements DomainEvent {

        public CreatedUserEvent {

            assert (isNotNull(id));
            assert (isNotAnEmptyString(username));
            assert (isNotAnEmptyString(email));
            assert (isNotNull(eventDate));
        }

        public CreatedUserEvent(UserId id, String username, String email) {

            this(id, username, email, LocalDateTime.now());
        }
    }

}
