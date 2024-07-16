package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.userstate.UserStateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserStateControllerTest {

    @Mock
    private UserStateService userStateService;

    @InjectMocks
    private UserStateController userStateController;

    private CreateUserStateRequest createUserStateRequest;
    private UpdateUserStateRequest updateUserStateRequest;
    private UserStateResponse userStateResponse;

    @BeforeEach
    void setUp() {
        createUserStateRequest = CreateUserStateRequest.builder()
                .userStateName("Active")
                .build();

        updateUserStateRequest = UpdateUserStateRequest.builder()
                .userStateName("Inactive")
                .build();

        userStateResponse = UserStateResponse.builder()
                .userStateId(1L)
                .userStateName("Active")
                .build();
    }

    @Test
    @DisplayName("회원 상태 생성 - 성공")
    void testCreateUserState() {
        when(userStateService.createUserState(any(CreateUserStateRequest.class)))
                .thenReturn(userStateResponse);

        ResponseEntity<UserStateResponse> response = userStateController.createUserState(createUserStateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userStateResponse);
    }

    @Test
    @DisplayName("특정 회원 상태 수정 - 성공")
    void testUpdateUserState() {
        UserStateResponse updatedUserStateResponse = UserStateResponse.builder()
                .userStateId(1L)
                .userStateName("Inactive")
                .build();

        when(userStateService.updateUserState(anyLong(), any(UpdateUserStateRequest.class)))
                .thenReturn(updatedUserStateResponse);

        ResponseEntity<UserStateResponse> response = userStateController.updateUserState(1L, updateUserStateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedUserStateResponse);
    }

    @Test
    @DisplayName("특정 회원 상태 조회 - 성공")
    void testFindUserState() {
        when(userStateService.findByUserStateId(anyLong()))
                .thenReturn(userStateResponse);

        ResponseEntity<UserStateResponse> response = userStateController.findUserState(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userStateResponse);
    }

    @Test
    @DisplayName("모든 회원 상태 목록 조회 - 성공")
    void testFindAllUserStates() {
        List<UserStateResponse> userStateResponses = List.of(userStateResponse);

        when(userStateService.findAllUserStates())
                .thenReturn(userStateResponses);

        ResponseEntity<List<UserStateResponse>> response = userStateController.findAllUserStates();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userStateResponses);
    }

    @Test
    @DisplayName("특정 회원 상태 삭제 - 성공")
    void testDeleteUserState() {
        ResponseEntity<UserStateResponse> response = userStateController.deleteUserState(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
