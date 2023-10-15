package com.nancho313.loqui.users.contract.api.controller;

import com.nancho313.loqui.users.application.command.CommandHandler;
import com.nancho313.loqui.users.application.command.signup.command.SignUpCommand;
import com.nancho313.loqui.users.application.command.signup.response.SignUpResponse;
import com.nancho313.loqui.users.contract.api.dto.SignUpApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/signup")
@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final CommandHandler<SignUpCommand, SignUpResponse> commandHandler;

    @PostMapping
    public ResponseEntity<SignUpResponse> signUpUser(@RequestBody SignUpApiRequest request) {

        var response = commandHandler.handle(new SignUpCommand(request.username(), request.password(), request.email()));
        return ResponseEntity.ok(new SignUpResponse(response.id()));
    }

}
