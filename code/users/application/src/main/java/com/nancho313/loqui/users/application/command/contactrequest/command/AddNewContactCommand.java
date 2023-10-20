package com.nancho313.loqui.users.application.command.contactrequest.command;

import com.nancho313.loqui.users.application.command.Command;

public record AddNewContactCommand(String userId, String contactId, String initialMessage) implements Command {
}
