package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReaderOrderUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/order-info")
public class UserOrderInfoController {

    private final UserAddressService userAddressService;

    @GetMapping
    public ResponseEntity<ReaderOrderUserInfoResponse> getOrderInfo(@PathVariable Long userId) {
        return new ResponseEntity<>(userAddressService.orderUserInfo(userId), HttpStatus.OK);
    }
}
