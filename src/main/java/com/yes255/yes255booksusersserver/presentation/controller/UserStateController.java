package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.CreateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.userstate.UpdateUserStateRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.userstate.UserStateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 상태 관리 API를 제공하는 UserStateController
 */

@Tag(name = "회원 상태 API", description = "회원 상태 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user-states")
public class UserStateController {

    private final UserStateService userStateService;

    /**
     * 회원 상태를 생성합니다.
     *
     * @param userStateRequest 생성할 회원 상태의 요청 데이터
     * @return ResponseEntity<UserStateResponse> 생성된 회원 상태 데이터와 상태 코드 201(CREATED)
     */
    @Operation(summary = "회원 상태 생성", description = "회원 상태를 생성합니다.")
    @PostMapping
    public ResponseEntity<UserStateResponse> createUserState(@RequestBody CreateUserStateRequest userStateRequest) {
        return new ResponseEntity<>(userStateService.createUserState(userStateRequest), HttpStatus.CREATED);
    }

    /**
     * 특정 회원 상태를 수정합니다.
     *
     * @param userStateId      수정할 회원 상태의 ID
     * @param userStateRequest 수정할 회원 상태의 요청 데이터
     * @return ResponseEntity<UserStateResponse> 수정된 회원 상태 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 상태 수정", description = "특정 회원 상태를 수정합니다.")
    @PutMapping("/{userStateId}")
    public ResponseEntity<UserStateResponse> updateUserState(@PathVariable Long userStateId,
                                                             @RequestBody UpdateUserStateRequest userStateRequest) {
        return new ResponseEntity<>(userStateService.updateUserState(userStateId, userStateRequest), HttpStatus.OK);
    }

    /**
     * 특정 회원 상태를 조회합니다.
     *
     * @param userStateId 조회할 회원 상태의 ID
     * @return ResponseEntity<UserStateResponse> 조회된 회원 상태 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 상태 조회", description = "특정 회원 상태를 조회합니다.")
    @GetMapping("/{userStateId}")
    public ResponseEntity<UserStateResponse> findUserState(@PathVariable Long userStateId) {
        return new ResponseEntity<>(userStateService.findByUserStateId(userStateId), HttpStatus.OK);
    }

    /**
     * 모든 회원 상태 목록을 조회합니다.
     *
     * @return ResponseEntity<List<UserStateResponse>> 모든 회원 상태 목록과 상태 코드 200(OK)
     */
    @Operation(summary = "회원 상태 목록 조회", description = "모든 회원 상태 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserStateResponse>> findAllUserStates() {
        return new ResponseEntity<>(userStateService.findAllUserStates(), HttpStatus.OK);
    }

    /**
     * 특정 회원 상태를 삭제합니다.
     *
     * @param userStateId 삭제할 회원 상태의 ID
     * @return ResponseEntity<UserStateResponse> 삭제된 회원 상태 데이터와 상태 코드 204(NO_CONTENT)
     */
    @Operation(summary = "회원 상태 삭제", description = "특정 회원 상태를 삭제합니다.")
    @DeleteMapping("/{userStateId}")
    public ResponseEntity<UserStateResponse> deleteUserState(@PathVariable Long userStateId) {

        userStateService.deleteUserState(userStateId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
