package com.nancho313.loqui.users.application.command;

public interface Response {

    default Response empty() {

        return new EmptyResponse();
    }

    record EmptyResponse() implements Response {
    }
}
