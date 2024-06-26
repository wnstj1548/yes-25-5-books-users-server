package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.UserStateServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.UserStateException;
import com.yes255.yes255booksusersserver.persistance.domain.UserState;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserStateRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.userstate.UserStateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStateServiceImplTest {

    @Mock
    private JpaUserStateRepository userStateRepository;

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private UserStateServiceImpl userStateService;

    @DisplayName("회원 상태 생성 - 성공")
    @Test
    void testCreateUserState_Success() {

        CreateUserStateRequest request = CreateUserStateRequest.builder()
                .userStateName("ACTIVE")
                .build();

        when(userStateRepository.findByUserStateName(request.userStateName())).thenReturn(null);

        UserState savedUserState = UserState.builder()
                .userStateId(1L)
                .userStateName(request.userStateName())
                .build();

        when(userStateRepository.save(any(UserState.class))).thenReturn(savedUserState);

        UserStateResponse response = userStateService.createUserState(request);

        assertNotNull(response);
        assertEquals(request.userStateName(), response.userStateName());
    }

    @DisplayName("회원 상태 생성 - 실패 (이미 존재하는 회원 상태)")
    @Test
    void testCreateUserState_AlreadyExists() {

        CreateUserStateRequest request = CreateUserStateRequest.builder()
                .userStateName("ACTIVE")
                .build();

        UserState existedUserState = UserState.builder()
                .userStateId(1L)
                .userStateName(request.userStateName())
                .build();

        when(userStateRepository.findByUserStateName(request.userStateName())).thenReturn(existedUserState);

        assertThrows(UserStateException.class, () -> {
            userStateService.createUserState(request);
        });
    }

    @DisplayName("회원 상태 업데이트 - 성공 ")
    @Test
    void testUpdateUserState_Success() {

        Long userStateId = 1L;
        UpdateUserStateRequest request = UpdateUserStateRequest.builder()
                .userStateName("INACTIVE")
                .build();

        UserState existingUserState = UserState.builder()
                .userStateId(userStateId)
                .userStateName("ACTIVE")
                .build();

        when(userStateRepository.findById(userStateId)).thenReturn(Optional.of(existingUserState));

        UserState updatedUserState = UserState.builder()
                .userStateId(userStateId)
                .userStateName(request.userStateName())
                .build();

        when(userStateRepository.save(any(UserState.class))).thenReturn(updatedUserState);

        UserStateResponse response = userStateService.updateUserState(userStateId, request);

        assertNotNull(response);
        assertEquals(request.userStateName(), response.userStateName());
    }

    @DisplayName("회원 상태 업데이트 - 실패 (회원 상태가 존재하지 않음)")
    @Test
    void testUpdateUserState_NotFound() {

        Long userStateId = 1L;
        UpdateUserStateRequest request = UpdateUserStateRequest.builder()
                .userStateName("INACTIVE")
                .build();

        when(userStateRepository.findById(userStateId)).thenReturn(Optional.empty());

        assertThrows(UserStateException.class, () -> {
            userStateService.updateUserState(userStateId, request);
        });
    }

    @DisplayName("회원 상태 조회 - 성공")
    @Test
    void testFindByUserStateId_Success() {

        Long userStateId = 1L;
        UserState userState = UserState.builder()
                .userStateId(userStateId)
                .userStateName("ACTIVE")
                .build();

        when(userStateRepository.findById(userStateId)).thenReturn(Optional.of(userState));

        UserStateResponse response = userStateService.findByUserStateId(userStateId);

        assertNotNull(response);
        assertEquals(userStateId, response.userStateId());
        assertEquals(userState.getUserStateName(), response.userStateName());
    }

    @DisplayName("회원 상태 조회 - 실패 (회원 상태가 존재하지 않음)")
    @Test
    void testFindByUserStateId_NotFound() {

        Long userStateId = 1L;
        when(userStateRepository.findById(userStateId)).thenReturn(Optional.empty());

        assertThrows(UserStateException.class, () -> {
            userStateService.findByUserStateId(userStateId);
        });
    }

    @DisplayName("모든 회원 상태 조회 - 성공")
    @Test
    void testFindAllUserStates_Success() {

        List<UserState> userStates = new ArrayList<>();
        userStates.add(UserState.builder().userStateId(1L).userStateName("ACTIVE").build());
        userStates.add(UserState.builder().userStateId(2L).userStateName("INACTIVE").build());

        when(userStateRepository.findAll()).thenReturn(userStates);

        List<UserStateResponse> responses = userStateService.findAllUserStates();

        assertNotNull(responses);
        assertEquals(userStates.size(), responses.size());

        for (int i = 0; i < userStates.size(); i++) {
            UserStateResponse expected = UserStateResponse.builder()
                    .userStateId(userStates.get(i).getUserStateId())
                    .userStateName(userStates.get(i).getUserStateName())
                    .build();
            assertEquals(expected, responses.get(i));
        }
    }

    @DisplayName("모든 회원 상태 조회 - 실패 (회원 상태가 없음)")
    @Test
    void testFindAllUserStates_NotFound() {

        when(userStateRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(UserStateException.class, () -> {
            userStateService.findAllUserStates();
        });
    }

    @DisplayName("회원 상태 삭제 - 성공")
    @Test
    void testDeleteUserState_Success() {

        Long userStateId = 1L;
        when(userStateRepository.findById(userStateId)).thenReturn(Optional.of(UserState.builder().userStateId(userStateId).userStateName("ACTIVE").build()));

        assertDoesNotThrow(() -> userStateService.deleteUserState(userStateId));

        verify(userStateRepository, times(1)).deleteById(userStateId);
    }

    @DisplayName("회원 상태 삭제 - 실패 (회원 상태가 존재하지 않음)")
    @Test
    void testDeleteUserState_NotFound() {

        Long userStateId = 1L;
        when(userStateRepository.findById(userStateId)).thenReturn(Optional.empty());

        assertThrows(UserStateException.class, () -> {
            userStateService.deleteUserState(userStateId);
        });
    }
}
