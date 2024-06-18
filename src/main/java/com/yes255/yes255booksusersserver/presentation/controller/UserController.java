package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaProviderRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.FindUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.LoginUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateUserResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JpaCustomerRepository customerRepository;
    private final JpaProviderRepository providerRepository;

    // 인증을 위한 회원 정보 반환
    @PostMapping("/users")
    public ResponseEntity<LoginUserResponse> findLoginUserByEmail(@RequestBody LoginUserRequest userRequest) {
        return new ResponseEntity<>(userService.findLoginUserByEmail(userRequest), HttpStatus.OK);
    }

    // 회원 가입
    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody CreateUserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    // 유저 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginUserRequest userRequest) {
        return new ResponseEntity<>(userService.loginUserByEmailByPassword(userRequest), HttpStatus.OK);
    }


    // 이메일(아이디) 찾기 (이름과 이메일이 조건에 일치하는)
    @PostMapping("/auth/findEmail")
    public ResponseEntity<List<FindUserResponse>> findAllByUserNameByUserPhone(@RequestBody FindEmailRequest emailRequestRequest,
                                                                               Pageable pageable) {
        return new ResponseEntity<>(userService.findAllUserEmailByUserNameByUserPhone(emailRequestRequest, pageable)
                , HttpStatus.OK);
    }

    // 비밀번호 찾기
    @PostMapping("/auth/findPassword")
    public ResponseEntity<Boolean> findPassword(@RequestBody FindPasswordRequest passwordRequest) {
        return new ResponseEntity<>(userService.findUserPasswordByEmailByName(passwordRequest), HttpStatus.OK);
    }

    // todo : 비밀번호 설정
    @PostMapping("/auth/setPassword")



    // 회원 수정 페이지
    @GetMapping("/users/{userId}")
    public ResponseEntity<UpdateUserResponse> findByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.findUserByUserId(userId), HttpStatus.OK);
    }

    // 회원 수정
    @PutMapping("/users/{userId}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable Long userId,
                                                         @RequestBody UpdateUserRequest userRequest) {
        return new ResponseEntity<>(userService.updateUser(userId, userRequest), HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long userId,
                                                   @RequestBody DeleteUserRequest userRequest) {

        userService.deleteUser(userId, userRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }












    // 제공자, 회원 상태, 회원 등급, 포인트 정책  record 생성용
    @GetMapping("/test")
    public void test() {
        userService.createRecord();
    }
}
