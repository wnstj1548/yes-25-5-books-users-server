package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserTotalPureAmountService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadPurePriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 총 순수 금액 API", description = "회원의 총 순수 금액 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserTotalPureAmountController {

    private final UserTotalPureAmountService userTotalPureAmountService;

    @Operation(summary = "회원 순수 금액 조회", description = "특정 회원의 총 순수 금액을 조회합니다.")
    @GetMapping("/pure-price")
    public ResponseEntity<ReadPurePriceResponse> getPurePrice(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(userTotalPureAmountService.findUserTotalPureAmountByUserId(userId));
    }
}
