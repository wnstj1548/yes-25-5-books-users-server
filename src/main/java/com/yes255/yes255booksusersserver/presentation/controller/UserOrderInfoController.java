package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.OrderUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserOrderInfoController {

    private final OrderUserService orderUserService;

    @GetMapping("/orders/info")
    public ResponseEntity<ReadOrderUserInfoResponse> getUserInfo(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(orderUserService.orderUserInfo(userId));
    }

    @GetMapping("/addresses")
    public ResponseEntity<Page<ReadOrderUserAddressResponse>> getUserAddresses(Pageable pageable,
                                                               @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(orderUserService.getUserAddresses(userId, pageable));
    }

    @GetMapping("/grade")
    public ResponseEntity<ReadUserInfoResponse> getUserPointsAndGrade(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(orderUserService.getUserInfo(userId));
    }
}
