package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users/{userId}/user-addresses")
@RequiredArgsConstructor
@RestController
public class UserAddressController { // todo : addressId 제거

    private final UserAddressService userAddressService;

    // 유저 주소 등록
    @PostMapping
    public ResponseEntity<CreateUserAddressResponse> createUserAddress(@PathVariable Long userId,
                                                                       @RequestBody CreateUserAddressRequest userAddressRequest) {
        return ResponseEntity.ok(userAddressService.createAddress(userId, userAddressRequest));
    }

    // 유저 주소 수정
    @PutMapping("/{userAddressId}")
    public ResponseEntity<UpdateUserAddressResponse> updateUserAddress(@PathVariable Long userId,
                                                                       @PathVariable Long userAddressId,
                                                                       @RequestBody UpdateUserAddressRequest userAddressRequest) {
        return ResponseEntity.ok(userAddressService.updateAddress(userId, userAddressId, userAddressRequest));
    }

    // 특정 유저 주소 조회
    @GetMapping("/{userAddressId}")
    public ResponseEntity<UserAddressResponse> findUserAddressById(@PathVariable Long userId,
                                                                  @PathVariable Long userAddressId) {
        return ResponseEntity.ok(userAddressService.findAddressById(userId, userAddressId));
    }

    // 유저 주소 목록 조회
    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> findAllUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(userAddressService.findAllAddresses(userId));
    }

    // 유저 주소 삭제
    @DeleteMapping("/{userAddressId}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long userId,
                                                  @PathVariable Long userAddressId) {

        userAddressService.deleteAddress(userId, userAddressId);

        return ResponseEntity.noContent().build();
    }
}
