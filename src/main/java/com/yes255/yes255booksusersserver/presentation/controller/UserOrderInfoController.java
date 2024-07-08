package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.OrderUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadUserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 주문 정보 API", description = "회원 주문 정보 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserOrderInfoController {

    private final OrderUserService orderUserService;

    @Operation(summary = "주문 고객 조회", description = "주문 시 고객의 정보를 조회합니다.")
    @GetMapping("/orders/info")
    public ResponseEntity<ReadOrderUserInfoResponse> getUserInfo(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(orderUserService.orderUserInfo(userId));
    }

    @Operation(summary = "주문 고객 주소 목록 조회", description = "주문 시 고객의 주소 목록을 조회합니다.")
    @GetMapping("/addresses")
    public ResponseEntity<Page<ReadOrderUserAddressResponse>> getUserAddresses(Pageable pageable,
                                                               @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(orderUserService.getUserAddresses(userId, pageable));
    }
    @Operation(summary = "주문 고객 포인트, 등급 조회", description = "주문 시 회원의 포인트와 등급을 조회합니다.")
    @GetMapping("/grade")
    public ResponseEntity<ReadUserInfoResponse> getUserPointsAndGrade(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(orderUserService.getUserInfo(userId));
    }
}
