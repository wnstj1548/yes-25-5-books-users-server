package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.Users;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserResponse;

public interface UserServices {

    Users getCurrentUser(Long userId);

    CreateUserResponse createUser(CreateUserRequest userRequest);

//    public User updateUser(User user);
}
