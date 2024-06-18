package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    LoginUserResponse findLoginUserByEmail(LoginUserRequest email);

    UpdateUserResponse findUserByUserId(Long userId);

    List<FindUserResponse> findAllUserEmailByUserNameByUserPhone(FindEmailRequest emailRequest, Pageable pageable);

    UserResponse createUser(CreateUserRequest userRequest);

    UpdateUserResponse updateUser(Long userId, UpdateUserRequest userRequest);

    void deleteUser(Long userId, DeleteUserRequest userRequest);

    void updateLastLoginDate(Long userId);

    boolean loginUserByEmailByPassword(LoginUserRequest loginUserRequest);

    boolean findUserPasswordByEmailByName(FindPasswordRequest passwordRequest);

    boolean setUserPasswordByUserId(UpdatePasswordRequest passwordRequest);

    void createRecord();
}
