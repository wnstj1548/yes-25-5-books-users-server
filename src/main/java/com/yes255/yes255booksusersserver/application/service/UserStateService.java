package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.userstate.UserStateResponse;

import java.util.List;

public interface UserStateService {

    UserStateResponse createUserState(CreateUserStateRequest userStateRequest);

    UserStateResponse updateUserState(Long userStateId, UpdateUserStateRequest userStateRequest);

    UserStateResponse findByUserStateId(Long userStateId);

    List<UserStateResponse> findAllUserStates();

    void deleteUserState(Long userStateId);

    void updateUserStateByUser();
}
