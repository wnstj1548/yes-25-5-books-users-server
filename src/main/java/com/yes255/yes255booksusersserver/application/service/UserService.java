package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse findUserByUserId(Long userId, String userEmail);

    List<UserResponse> findAllUserByUserNameByUserPhone(String userName, String userPhone);

    UserResponse createUser(CreateUserRequest userRequest);

    UserResponse updateUser(Long userId, UpdateUserRequest userRequest);

    void deleteUser(Long userId, String userEmail);

    void updateLastLoginDate(Long userId);

    void createRecord();
}
