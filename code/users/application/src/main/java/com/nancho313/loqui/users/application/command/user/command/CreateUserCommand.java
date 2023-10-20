package com.nancho313.loqui.users.application.command.user.command;

import com.nancho313.loqui.users.application.command.Command;

public record CreateUserCommand(String userId, String username, String email) implements Command {
}
