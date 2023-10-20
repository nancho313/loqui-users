package com.nancho313.loqui.users.domain.aggregate;

import com.nancho313.loqui.commons.ObjectValidator;
import com.nancho313.loqui.users.domain.event.DomainEvent;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class DomainAggregate {

    private final List<DomainEvent> currentEvents;

    protected DomainAggregate(List<DomainEvent> currentEvents) {
        this.currentEvents = ObjectValidator.getImmutableList(currentEvents);
    }
}
