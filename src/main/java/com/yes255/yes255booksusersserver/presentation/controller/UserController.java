package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
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

/**
 * 회원 관련 API를 제공하는 UserController
 */

@Tag(name = "회원 API", description = "회원 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 인증을 위해 회원 정보를 반환합니다.
     *
     * @param userRequest 로그인 요청 데이터
     * @return 로그인 사용자 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "인증을 위한 회원 정보 반환", description = "로그인 인증을 위한 회원 정보를 반환합니다.")
    @PostMapping("/users")
    public ResponseEntity<LoginUserResponse> findLoginUserByEmail(@RequestBody LoginUserRequest userRequest) {
        return new ResponseEntity<>(userService.findLoginUserByEmailByPassword(userRequest), HttpStatus.OK);
    }

    /**
     * 회원 가입을 진행합니다.
     *
     * @param userRequest 회원 가입 요청 데이터
     * @return ResponseEntity<UserResponse> 생성된 회원 데이터와 상태 코드 201(CREATED)
     */
    @Operation(summary = "회원 가입", description = "회원 가입을 진행합니다.")
    @PostMapping("/users/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody CreateUserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    /**
     * 회원 로그인을 처리합니다.
     *
     * @param userRequest 로그인 요청 데이터
     * @return ResponseEntity<Boolean> 로그인 성공 여부와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 로그인", description = "회원의 로그인을 처리합니다.")
    @PostMapping("/users/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginUserRequest userRequest) {
        return new ResponseEntity<>(userService.loginUserByEmailByPassword(userRequest), HttpStatus.OK);
    }

    /**
     * 이메일을 찾습니다.
     *
     * @param emailRequest 이메일 찾기 요청 데이터
     * @param pageable     페이지네이션 정보
     * @return ResponseEntity<List<FindUserResponse>> 찾은 이메일 목록과 상태 코드 200(OK)
     */
    @Operation(summary = "이메일 찾기", description = "이메일(아이디)을 찾기 위해 회원 이름과 이메일을 통해 이메일 목록을 조회합니다.")
    @PostMapping("/users/findEmail")
    public ResponseEntity<List<FindUserResponse>> findAllByUserNameByUserPhone(@RequestBody FindEmailRequest emailRequest,
                                                                               Pageable pageable) {
        return new ResponseEntity<>(userService.findAllUserEmailByUserNameByUserPhone(emailRequest, pageable)
                , HttpStatus.OK);
    }

    /**
     * 비밀번호 찾기를 진행합니다.
     *
     * @param passwordRequest 비밀번호 찾기 요청 데이터
     * @return ResponseEntity<Boolean> 비밀번호 찾기 성공 여부와 상태 코드 200(OK)
     */
    @Operation(summary = "비밀번호 찾기", description = "이메일과 회원 이름을 통해 비밀번호 찾기 인증을 진행합니다.")
    @PostMapping("/users/findPassword")
    public ResponseEntity<Boolean> findPassword(@RequestBody FindPasswordRequest passwordRequest) {
        return new ResponseEntity<>(userService.findUserPasswordByEmailByName(passwordRequest), HttpStatus.OK);
    }

    // todo : 비밀번호 설정
//    @Operation(summary = "비밀번호 설정", description = "비밀번호를 재설정합니다.")
//    @PostMapping("/auth/setPassword")


    /**
     * 회원 조회를 처리합니다.
     *
//     * @param jwtUserDetails 유저 토큰 정보
     * @return ResponseEntity<UserResponse> 조회된 회원 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 조회", description = "특정 회원 정보를 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<UserResponse> findByUserId(@CurrentUser JwtUserDetails jwtUserDetails) {

//        Long userId = jwtUserDetails.userId();

        Long userId = 275L;

        return new ResponseEntity<>(userService.findUserByUserId(userId), HttpStatus.OK);
    }

    /**
     * 회원 정보를 수정합니다.
     *
     * @param userRequest 회원 수정 요청 데이터
     * @param jwtUserDetails 유저 토큰 정보
     * @return ResponseEntity<UpdateUserResponse> 수정된 회원 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 수정", description = "특정 회원 정보를 수정합니다.")
    @PutMapping("/users")
    public ResponseEntity<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest userRequest,
                                                         @CurrentUser JwtUserDetails jwtUserDetails) {

//        Long userId = jwtUserDetails.userId();
        Long userId = 275L;

//        return new ResponseEntity<>(userService.updateUser(userId, userRequest), HttpStatus.OK);
        return ResponseEntity.ok(userService.updateUser(userId, userRequest));
    }

    /**
     * 회원 탈퇴를 처리합니다.
     *
     * @param userRequest 회원 탈퇴 요청 데이터
     * @param jwtUserDetails 유저 토큰 정보
     * @return ResponseEntity<Void> 회원 탈퇴 성공 여부와 상태 코드 204(NO_CONTENT)
     */
    @Operation(summary = "회원 탈퇴", description = "특정 회원이 탈퇴합니다.")
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@RequestBody DeleteUserRequest userRequest,
                                           @CurrentUser JwtUserDetails jwtUserDetails) {

//        Long userId = jwtUserDetails.userId();

        Long userId = 275L;

        userService.deleteUser(userId, userRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
