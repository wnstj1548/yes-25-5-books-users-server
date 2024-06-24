package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.presentation.dto.request.user.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Tag(name = "회원 API", description = "회원 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "인증을 위한 회원 정보 반환", description = "로그인 인증을 위한 회원 정보를 반환합니다.")
    @PostMapping("/users")
    public ResponseEntity<LoginUserResponse> findLoginUserByEmail(@RequestBody LoginUserRequest userRequest) {
        return new ResponseEntity<>(userService.findLoginUserByEmailByPassword(userRequest), HttpStatus.OK);
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 진행합니다.")
    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody CreateUserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "회원 로그인", description = "회원의 로그인을 진행합니다.")
    @PostMapping("/auth/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginUserRequest userRequest) {
        return new ResponseEntity<>(userService.loginUserByEmailByPassword(userRequest), HttpStatus.OK);
    }


    @Operation(summary = "이메일 찾기", description = "이메일(아이디)을 찾기 위해 회원 이름과 이메일을 통해 이메일 목록을 조회합니다.")
    @PostMapping("/auth/findEmail")
    public ResponseEntity<List<FindUserResponse>> findAllByUserNameByUserPhone(@RequestBody FindEmailRequest emailRequestRequest,
                                                                               Pageable pageable) {
        return new ResponseEntity<>(userService.findAllUserEmailByUserNameByUserPhone(emailRequestRequest, pageable)
                , HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 찾기", description = "이메일과 회원 이름을 통해 비밀번호 찾기 인증을 진행합니다.")
    @PostMapping("/auth/findPassword")
    public ResponseEntity<Boolean> findPassword(@RequestBody FindPasswordRequest passwordRequest) {
        return new ResponseEntity<>(userService.findUserPasswordByEmailByName(passwordRequest), HttpStatus.OK);
    }

    // todo : 비밀번호 설정
//    @Operation(summary = "비밀번호 설정", description = "비밀번호를 재설정합니다.")
//    @PostMapping("/auth/setPassword")


    @Operation(summary = "회원 조회", description = "특정 회원 정보를 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<UserResponse> findByUserId() {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(userService.findUserByUserId(userId), HttpStatus.OK);
    }

    @Operation(summary = "회원 수정", description = "특정 회원 정보를 수정합니다.")
    @PutMapping("/users")
    public ResponseEntity<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest userRequest) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(userService.updateUser(userId, userRequest), HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴", description = "특정 회원이 탈퇴합니다.")
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@RequestBody DeleteUserRequest userRequest) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        userService.deleteUser(userId, userRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
