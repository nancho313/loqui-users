package com.nancho313.loqui.users.integrationtest.util;

import com.nancho313.loqui.events.AcceptedContactRequestEvent;
import org.springframework.stereotype.Component;

@Component
@ITKafkaListener(topics = "accepted-contact-request")
public class AcceptedContactRequestEventMessageCaptor extends KafkaMessageCaptor<AcceptedContactRequestEvent> {

}