package com.nancho313.loqui.users.application.command.contactrequest.command;

import com.nancho313.loqui.users.application.command.Command;

public record ProcessContactRequestCommand(String contactRequestId, String requestedUserId, boolean accepted) implements Command {
}
