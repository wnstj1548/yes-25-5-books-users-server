package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class UserStateServiceImpl implements UserStateService {

    private final JpaUserStateRepository userStateRepository;

    @Override
    public UserStateResponse createUserState(CreateUserStateRequest userStateRequest) {

        UserState existedUserState = userStateRepository.findByUserStateName(userStateRequest.userStateName());

        if (Objects.nonNull(existedUserState)) {
            throw new IllegalArgumentException("유저 상태가 이미 존재합니다.");
        }

        UserState userState = userStateRepository.save(UserState.builder()
                        .userStateName(userStateRequest.userStateName()).build());

        return UserStateResponse.builder()
                .userStateName(userState.getUserStateName())
                .build();
    }

    @Override
    public UserStateResponse updateUserState(Long userStateId, UpdateUserStateRequest userStateRequest) {

        UserState userState = userStateRepository.findById(userStateId)
                .orElseThrow(() -> new IllegalArgumentException("유저 상태가 존재하지 않습니다."));

        userState.updateUserStateName(userStateRequest.userStateName());

        userStateRepository.save(userState);

        return UserStateResponse.builder()
                .userStateName(userState.getUserStateName())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserStateResponse findByUserStateId(Long userStateId) {

        UserState userState = userStateRepository.findById(userStateId)
                .orElseThrow(() -> new IllegalArgumentException("유저 상태가 존재하지 않습니다."));

        return UserStateResponse.builder()
                .userStateId(userState.getUserStateId())
                .userStateName(userState.getUserStateName())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStateResponse> findAllUserStates() {

        List<UserState> userStates = userStateRepository.findAll();

        if (userStates.isEmpty()) {
            throw new IllegalArgumentException("유저 상태가 존재하지 않습니다.");
        }

        return userStates.stream()
                .map(userState -> UserStateResponse.builder()
                        .userStateId(userState.getUserStateId())
                        .userStateName(userState.getUserStateName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserState(Long userStateId) {

        userStateRepository.findById(userStateId)
                .orElseThrow(() -> new IllegalArgumentException("유저 상태가 존재하지 않습니다."));


        userStateRepository.deleteById(userStateId);
    }
}
