package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserServices;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllers {

    private final UserServices userServices;

    @PostMapping("/users/sign-up")
    public ResponseEntity<CreateUserResponse> signUp(@RequestBody CreateUserRequest userRequest) {
        return new ResponseEntity<>(userServices.createUser(userRequest), HttpStatus.CREATED);
    }
}
