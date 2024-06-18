package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaProviderRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody CreateUserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findByUserId(@PathVariable Long userId,
                                                        @RequestParam String userEmail) {
        return new ResponseEntity<>(userService.findUserByUserId(userId, userEmail), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> findAllByUserNameByUserPhone(@RequestBody CreateUserRequest userRequest) {
        return new ResponseEntity<>(userService.findAllUserByUserNameByUserPhone(userRequest.userName(), userRequest.userPhone())
                , HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId,
                                                   @RequestBody UpdateUserRequest userRequest) {
        return new ResponseEntity<>(userService.updateUser(userId, userRequest), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long userId,
                                                   @RequestParam String userEmail) {

        userService.deleteUser(userId, userEmail);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }












    // 제공자, 회원 상태, 회원 등급, 포인트 정책  record 생성용
    @GetMapping("/test")
    public void test() {
        userService.createRecord();
    }
}
