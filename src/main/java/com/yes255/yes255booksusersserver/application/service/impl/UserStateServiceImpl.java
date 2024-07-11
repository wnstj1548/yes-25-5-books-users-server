package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import com.yes255.yes255booksusersserver.common.exception.UserStateException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.userstate.UserStateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "회원 상태 API", description = "회원 상태 관련 API 입니다.")
@Transactional
@Service
@RequiredArgsConstructor
public class UserStateServiceImpl implements UserStateService {

    private final JpaUserStateRepository userStateRepository;
    private final JpaUserRepository userRepository;

    @Operation(summary = "회원 상태 생성", description = "회원 상태를 생성합니다. ex) ACTIVE, INACTIVE")
    @Override
    public UserStateResponse createUserState(CreateUserStateRequest userStateRequest) {

        UserState existedUserState = userStateRepository.findByUserStateName(userStateRequest.userStateName());

        if (Objects.nonNull(existedUserState)) {
            throw new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 이미 존재합니다.", 400, LocalDateTime.now()));
        }

        UserState userState = userStateRepository.save(UserState.builder()
                        .userStateName(userStateRequest.userStateName()).build());

        return UserStateResponse.builder()
                .userStateName(userState.getUserStateName())
                .build();
    }

    @Operation(summary = "회원 상태 수정", description = "회원 상태를 수정합니다.")
    @Override
    public UserStateResponse updateUserState(Long userStateId, UpdateUserStateRequest userStateRequest) {

        UserState userState = userStateRepository.findById(userStateId)
                .orElseThrow(() -> new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now())));

        userState.updateUserStateName(userStateRequest.userStateName());

        userStateRepository.save(userState);

        return UserStateResponse.builder()
                .userStateName(userState.getUserStateName())
                .build();
    }

    @Operation(summary = "특정 회원 상태 조회", description = "특정 회원 상태를 조회합니다.")
    @Transactional(readOnly = true)
    @Override
    public UserStateResponse findByUserStateId(Long userStateId) {

        UserState userState = userStateRepository.findById(userStateId)
                .orElseThrow(() -> new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now())));

        return UserStateResponse.builder()
                .userStateId(userState.getUserStateId())
                .userStateName(userState.getUserStateName())
                .build();
    }

    @Operation(summary = "모든 회원 상태 조회", description = "모든 회원 상태를 조회합니다.")
    @Transactional(readOnly = true)
    @Override
    public List<UserStateResponse> findAllUserStates() {

        List<UserState> userStates = userStateRepository.findAll();

        if (userStates.isEmpty()) {
            throw new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        return userStates.stream()
                .map(userState -> UserStateResponse.builder()
                        .userStateId(userState.getUserStateId())
                        .userStateName(userState.getUserStateName())
                        .build())
                .collect(Collectors.toList());
    }

    @Operation(summary = "특정 회원 상태 삭제", description = "특정 회원 상태를 삭제합니다.")
    @Override
    public void deleteUserState(Long userStateId) {

        userStateRepository.findById(userStateId)
                .orElseThrow(() -> new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now())));

        userStateRepository.deleteById(userStateId);
    }

    // 3개월 이상 미접속 회원 휴면 전환
    @Operation(summary = "미접속자 휴면 전환", description = "3개월 이상 미접속자를 휴면 상태로 전환합니다.")
    @Override
    public void updateUserStateByUser() {

        UserState userState = userStateRepository.findByUserStateName("ACTIVE");
        
        UserState inActive = userStateRepository.findByUserStateName("INACTIVE");

        if (Objects.isNull(userState)) {
            throw new UserStateException(ErrorStatus.toErrorStatus("회원 상태가 존재하지 않습니다.", 400, LocalDateTime.now()));
        }

        List<User> users = userRepository.findAllByUserState(userState);

        for (User user : users) {
            if (user.getUserLastLoginDate() == null) {
                continue;
            }

            Period period = Period.between(user.getUserLastLoginDate().toLocalDate(), LocalDate.now());

            // 3개월 이상 여부 확인
            if (period.getYears() > 0 || period.getMonths() >= 3) {
                user.updateUserState(inActive);

                userRepository.save(user);
            }
        }
    }
}
