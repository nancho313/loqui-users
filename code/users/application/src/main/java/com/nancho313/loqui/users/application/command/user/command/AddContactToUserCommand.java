package com.nancho313.loqui.users.application.command.user.command;

import com.nancho313.loqui.users.application.command.Command;

public record AddContactToUserCommand(String userId, String contactId) implements Command {
}
