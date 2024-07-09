package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 주소 관련 API를 제공하는 UserAddressController
 */

@Tag(name = "회원 주소 API", description = "회원 주소 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/user-addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    /**
     * 회원의 주소를 등록합니다.
     *
     * @param userAddressRequest 회원 주소 생성 요청 데이터
     * @param jwtUserDetails 유저 토큰 정보
     * @return 생성된 회원 주소 응답 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 주소 등록", description = "회원의 주소를 등록합니다.")
    @PostMapping
    public ResponseEntity<CreateUserAddressResponse> createUserAddress(@RequestBody CreateUserAddressRequest userAddressRequest,
                                                                       @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(userAddressService.createAddress(userId, userAddressRequest));
    }

    /**
     * 특정 회원의 주소를 수정합니다.
     *
     * @param userAddressId      수정할 회원 주소 ID
     * @param userAddressRequest 회원 주소 수정 요청 데이터
     * @param jwtUserDetails 유저 토큰 정보
     * @return 수정된 회원 주소 응답 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 주소 수정", description = "특정 회원의 주소를 수정합니다.")
    @PutMapping("/{userAddressId}")
    public ResponseEntity<UpdateUserAddressResponse> updateUserAddress(@PathVariable Long userAddressId,
                                                                       @RequestBody UpdateUserAddressRequest userAddressRequest,
                                                                       @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(userAddressService.updateAddress(userId, userAddressId, userAddressRequest));
    }

    /**
     * 특정 회원의 주소를 조회합니다.
     *
     * @param userAddressId  조회할 회원 주소 ID
     * @param jwtUserDetails 유저 토큰 정보
     * @return 조회된 회원 주소 응답 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "회원 주소 조회", description = "특정 회원의 주소를 조회합니다.")
    @GetMapping("/{userAddressId}")
    public ResponseEntity<UserAddressResponse> findUserAddressById(@PathVariable Long userAddressId,
                                                                   @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(userAddressService.findAddressById(userId, userAddressId));
    }

    /**
     * 회원의 모든 주소 목록을 조회합니다.
     *
     * @param jwtUserDetails 유저 토큰 정보
     * @return 회원 주소 목록 응답 데이터와 상태 코드 200(OK)
     */
//    @Operation(summary = "회원 주소 목록 조회", description = "회원의 모든 주소 목록을 조회합니다.")
//    @GetMapping
//    public ResponseEntity<List<UserAddressResponse>> findAllUserAddresses(@CurrentUser JwtUserDetails jwtUserDetails) {
//
//        Long userId = jwtUserDetails.userId();
//
//        return ResponseEntity.ok(userAddressService.findAllAddresses(userId));
//    }

    @Operation(summary = "회원 주소 목록 조회", description = "회원의 모든 주소 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<UserAddressResponse>> findAllUserAddresses(@CurrentUser JwtUserDetails jwtUserDetails, Pageable pageable) {
        Long userId = jwtUserDetails.userId();
        return ResponseEntity.ok(userAddressService.findAllAddresses(userId, pageable));
    }

    /**
     * 특정 회원의 주소를 삭제합니다.
     *
     * @param userAddressId  삭제할 회원 주소 ID
     * @param jwtUserDetails 유저 토큰 정보
     * @return 상태 코드 204(NO CONTENT)
     */
    @Operation(summary = "회원 주소 삭제", description = "특정 회원의 주소를 삭제합니다.")
    @DeleteMapping("/{userAddressId}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long userAddressId,
                                                  @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        userAddressService.deleteAddress(userId, userAddressId);

        return ResponseEntity.noContent().build();
    }
}
