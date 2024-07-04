package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.UpdateCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponBoxResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CouponUserController {

    private final CouponUserService couponUserService;

    @PostMapping("/coupons/claim")
    public ResponseEntity<Void> claimCoupon(@RequestParam Long couponId,
                     @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        couponUserService.createCouponUser(couponId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/coupons/state")
    public ResponseEntity<Page<CouponBoxResponse>> getActiveCouponBox(@RequestParam String couponState,
                                                                      Pageable pageable,
                                                                      @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();
        CouponUser.UserCouponStatus status = CouponUser.UserCouponStatus.valueOf(couponState.toUpperCase());

        return ResponseEntity.ok(couponUserService.getStateUserCoupons(userId, status, pageable));
    }

    @PatchMapping("/user-coupons")
    public ResponseEntity<Void> updateCouponState(@RequestBody UpdateCouponRequest couponRequest,
                                                  @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        couponUserService.updateCouponState(userId, couponRequest);

        return ResponseEntity.ok().build();
    }
}
