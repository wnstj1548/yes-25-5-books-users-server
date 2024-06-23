package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.UserStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user-states")
public class UserStateController {

    private final UserStateService userStateService;

    // 유저 상태 생성
    @PostMapping
    public ResponseEntity<UserStateResponse> createUserState(@RequestBody CreateUserStateRequest userStateRequest) {
        return new ResponseEntity<>(userStateService.createUserState(userStateRequest), HttpStatus.CREATED);
    }

    // 유정 상태 수정
    @PutMapping("/{userStateId}")
    public ResponseEntity<UserStateResponse> updateUserState(@PathVariable Long userStateId,
                                                             @RequestBody UpdateUserStateRequest userStateRequest) {
        return new ResponseEntity<>(userStateService.updateUserState(userStateId, userStateRequest), HttpStatus.OK);
    }

    // 특정 유저 상태 조회
    @GetMapping("/{userStateId}")
    public ResponseEntity<UserStateResponse> findUserState(@PathVariable Long userStateId) {
        return new ResponseEntity<>(userStateService.findByUserStateId(userStateId), HttpStatus.OK);
    }

    // 유저 상태 목록 조회
    @GetMapping
    public ResponseEntity<List<UserStateResponse>> findAllUserStates() {
        return new ResponseEntity<>(userStateService.findAllUserStates(), HttpStatus.OK);
    }

    // 유저 상태 삭제
    @DeleteMapping("/{userStateId}")
    public ResponseEntity<UserStateResponse> deleteUserState(@PathVariable Long userStateId) {

        userStateService.deleteUserState(userStateId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
