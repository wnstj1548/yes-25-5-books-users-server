package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.UpdateCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponBoxResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ReadMaximumDiscountCouponResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ReadUserCouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원 쿠폰 API", description = "회원 쿠폰 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CouponUserController {

    private final CouponUserService couponUserService;

    @Operation(summary = "회원 쿠폰 발급", description = "특정 회원의 회원 쿠폰을 발급받습니다.")
    @PostMapping("/coupons/claim")
    public ResponseEntity<Void> claimCoupon(@RequestParam Long couponId,
                     @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        couponUserService.createCouponUser(couponId, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 쿠폰 상태 조회", description = "회원 쿠폰의 상태(사용 가능한 쿠폰, 사용한 쿠폰, 만료된 쿠폰)를 조회합니다.")
    @GetMapping("/coupons/state")
    public ResponseEntity<Page<CouponBoxResponse>> getActiveCouponBox(@RequestParam String couponState,
                                                                      Pageable pageable,
                                                                      @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();
        CouponUser.UserCouponStatus status = CouponUser.UserCouponStatus.valueOf(couponState.toUpperCase());

        return ResponseEntity.ok(couponUserService.getStateUserCoupons(userId, status, pageable));
    }

    @Operation(summary = "회원 쿠폰 상태 수정", description = "회원 쿠폰의 상태를 수정합니다.")
    @PatchMapping("/user-coupons")
    public ResponseEntity<Void> updateCouponState(@RequestBody UpdateCouponRequest couponRequest,
                                                  @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        couponUserService.updateCouponState(userId, couponRequest);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 쿠폰 목록 조회", description = "특정 회원의 사용 가능한 쿠폰 목록을 조회합니다.")
    @GetMapping("/user-coupons")
    public ResponseEntity<List<ReadUserCouponResponse>> getAllUserCoupons(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(couponUserService.getAllUserCouponsByUserId(userId));
    }

    @Operation(summary = "할인 금액이 높은 쿠폰 조회", description = "특정 회원의 할인 금액이 가장 높은 쿠폰을 조회합니다.")
    @GetMapping("/user-coupons/max")
    public ResponseEntity<ReadMaximumDiscountCouponResponse> getMaxDiscountCouponByTotalAmount(@RequestParam Integer totalAmount,
                                                                        @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(couponUserService.getMaximumDiscountCouponByUserId(userId, totalAmount));
    }
}
