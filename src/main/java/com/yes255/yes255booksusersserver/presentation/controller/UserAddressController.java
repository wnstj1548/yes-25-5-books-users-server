package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원 주소 API", description = "회원 주소 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/user-addresses")
public class UserAddressController { // todo : addressId 제거

    private final UserAddressService userAddressService;

    @Operation(summary = "회원 주소 등록", description = "회원의 주소를 등록합니다.")
    @PostMapping
    public ResponseEntity<CreateUserAddressResponse> createUserAddress(@PathVariable Long userId,
                                                                       @RequestBody CreateUserAddressRequest userAddressRequest) {
        return ResponseEntity.ok(userAddressService.createAddress(userId, userAddressRequest));
    }

    @Operation(summary = "회원 주소 수정", description = "특정 회원의 주소를 수정합니다.")
    @PutMapping("/{userAddressId}")
    public ResponseEntity<UpdateUserAddressResponse> updateUserAddress(@PathVariable Long userId,
                                                                       @PathVariable Long userAddressId,
                                                                       @RequestBody UpdateUserAddressRequest userAddressRequest) {
        return ResponseEntity.ok(userAddressService.updateAddress(userId, userAddressId, userAddressRequest));
    }

    @Operation(summary = "회원 주소 조회", description = "특정 회원의 주소를 조회합니다.")
    @GetMapping("/{userAddressId}")
    public ResponseEntity<UserAddressResponse> findUserAddressById(@PathVariable Long userId,
                                                                  @PathVariable Long userAddressId) {
        return ResponseEntity.ok(userAddressService.findAddressById(userId, userAddressId));
    }

    @Operation(summary = "회원 주소 목록 조회", description = "회원의 모든 주소 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> findAllUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(userAddressService.findAllAddresses(userId));
    }

    @Operation(summary = "회원 주소 삭제", description = "특정 회원의 주소를 삭제합니다.")
    @DeleteMapping("/{userAddressId}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long userId,
                                                  @PathVariable Long userAddressId) {

        userAddressService.deleteAddress(userId, userAddressId);

        return ResponseEntity.noContent().build();
    }
}
