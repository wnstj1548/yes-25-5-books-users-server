package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserStateResponse;

import java.util.List;

public interface UserStateService {

    UserStateResponse createUserState(CreateUserStateRequest userStateRequest);

    UserStateResponse updateUserState(Long userStateId, UpdateUserStateRequest userStateRequest);

    UserStateResponse findByUserStateId(Long userStateId);

    List<UserStateResponse> findAllUserStates();

    void deleteUserState(Long userStateId);

    void updateUserStateByUser();
}
