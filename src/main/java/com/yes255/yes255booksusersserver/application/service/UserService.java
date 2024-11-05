package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UnlockDormantRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    LoginUserResponse findLoginUserByEmailByPassword(LoginUserRequest email);

    UserResponse findUserByUserId(Long userId);

    List<FindUserResponse> findAllUserEmailByUserNameByUserPhone(FindEmailRequest emailRequest);

    UserResponse createUser(CreateUserRequest userRequest);

    UpdateUserResponse updateUser(Long userId, UpdateUserRequest userRequest);

    void deleteUser(Long userId, DeleteUserRequest userRequest);

    boolean checkUserPassword(Long userId, CheckPasswordRequest passwordRequest);

    void updateLastLoginDate(Long userId);

    boolean loginUserByEmailByPassword(LoginUserRequest loginUserRequest);

    boolean findUserPasswordByEmailByName(FindPasswordRequest passwordRequest);

    boolean setUserPasswordByUserId(Long userId, UpdatePasswordRequest passwordRequest);

    boolean isEmailDuplicate(String email);

    List<Long> findUserIdsWithBirthdaysInCurrentMonth();

    void unLockDormantStateByEmail(UnlockDormantRequest request);
}
