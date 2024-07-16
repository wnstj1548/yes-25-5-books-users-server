package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private LoginUserRequest loginUserRequest;
    private CreateUserRequest createUserRequest;
    private FindEmailRequest findEmailRequest;
    private FindPasswordRequest findPasswordRequest;
    private UserResponse userResponse;
    private UpdateUserRequest updateUserRequest;
    private DeleteUserRequest deleteUserRequest;
    private UnlockDormantRequest unlockDormantRequest;
    private JwtUserDetails jwtUserDetails;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");

        loginUserRequest = LoginUserRequest.builder()
                .email("user@example.com")
                .password("password")
                .build();

        createUserRequest = CreateUserRequest.builder()
                .userName("User")
                .userBirth(LocalDate.of(1990, 1, 1))
                .userEmail("user@example.com")
                .userPhone("010-1234-5678")
                .userPassword("password")
                .userConfirmPassword("password")
                .providerName("LOCAL")
                .build();

        findEmailRequest = FindEmailRequest.builder()
                .name("User")
                .phone("010-1234-5678")
                .build();

        findPasswordRequest = FindPasswordRequest.builder()
                .email("user@example.com")
                .name("User")
                .build();

        userResponse = UserResponse.builder()
                .userId(1L)
                .userName("User")
                .userBirth(LocalDate.of(1990, 1, 1))
                .userEmail("user@example.com")
                .userPhone("010-1234-5678")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .userName("Updated User")
                .userPhone("010-9876-5432")
                .userBirth(LocalDate.of(1991, 2, 2))
                .userPassword("password")
                .newUserPassword("newPassword")
                .newUserConfirmPassword("newPassword")
                .build();

        deleteUserRequest = DeleteUserRequest.builder()
                .userPassword("password")
                .build();

        unlockDormantRequest = UnlockDormantRequest.from("user@example.com");
    }

    @Test
    @DisplayName("이메일로 로그인 유저 찾기 - 성공")
    void testFindLoginUserByEmail() {
        LoginUserResponse loginUserResponse = LoginUserResponse.builder()
                .userId(1L)
                .userRole("ROLE_USER")
                .loginStatusName("SUCCESS")
                .build();

        when(userService.findLoginUserByEmailByPassword(any(LoginUserRequest.class)))
                .thenReturn(loginUserResponse);

        ResponseEntity<LoginUserResponse> response = userController.findLoginUserByEmail(loginUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(loginUserResponse);
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void testSignUp() {
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.signUp(createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userResponse);
    }

    @Test
    @DisplayName("이름과 전화번호로 모든 이메일 찾기 - 성공")
    void testFindAllByUserNameByUserPhone() {
        FindUserResponse findUserResponse = FindUserResponse.builder()
                .userEmail("user@example.com")
                .build();
        List<FindUserResponse> responses = List.of(findUserResponse);

        when(userService.findAllUserEmailByUserNameByUserPhone(any(FindEmailRequest.class), any()))
                .thenReturn(responses);

        ResponseEntity<List<FindUserResponse>> response = userController.findAllByUserNameByUserPhone(findEmailRequest, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("이메일과 이름으로 비밀번호 찾기 - 성공")
    void testFindPassword() {
        when(userService.findUserPasswordByEmailByName(any(FindPasswordRequest.class)))
                .thenReturn(true);

        ResponseEntity<Boolean> response = userController.findPassword(findPasswordRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }

    @Test
    @DisplayName("특정 회원 조회 - 성공")
    void testFindByUserId() {
        when(userService.findUserByUserId(anyLong()))
                .thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.findByUserId(jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userResponse);
    }

    @Test
    @DisplayName("특정 회원 정보 수정 - 성공")
    void testUpdateUser() {
        UpdateUserResponse updateUserResponse = UpdateUserResponse.builder()
                .userName("Updated User")
                .userBirth(LocalDate.of(1991, 2, 2))
                .userPhone("010-9876-5432")
                .build();

        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class)))
                .thenReturn(updateUserResponse);

        ResponseEntity<UpdateUserResponse> response = userController.updateUser(updateUserRequest, jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updateUserResponse);
    }

    @Test
    @DisplayName("회원탈퇴 - 성공")
    void testDeleteUser() {
        ResponseEntity<Void> response = userController.deleteUser(deleteUserRequest, jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("이메일 중복 확인 - 성공")
    void testCheckEmail() {
        String email = "user@example.com";
        when(userService.isEmailDuplicate(email)).thenReturn(true);

        ResponseEntity<Boolean> response = userController.checkEmail(email);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }

    @Test
    @DisplayName("휴면 계정 해제 - 성공")
    void testUnLockDormantState() {
        ResponseEntity<Void> response = userController.unLockDormantState(unlockDormantRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
