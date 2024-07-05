package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponBoxResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/coupons")
public class CouponUserController {

    private final CouponUserService couponUserService;

    @PostMapping("/claim")
    public ResponseEntity<Void> claimCoupon(@RequestParam Long couponId,
                     @CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        couponUserService.createCouponUser(couponId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/state")
    public ResponseEntity<Page<CouponBoxResponse>> getActiveCouponBox(@RequestParam String couponState,
                                                                      Pageable pageable,
                                                                      @CurrentUser JwtUserDetails jwtUserDetails) {
        CouponUser.UserCouponStatus status = CouponUser.UserCouponStatus.valueOf(couponState.toUpperCase());
        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(couponUserService.getStateUserCoupons(userId, status, pageable));
    }

//    @GetMapping("/active")
//    public ResponseEntity<Page<CouponBoxResponse>> getActiveCouponBox(Pageable pageable,
//                                                               @CurrentUser JwtUserDetails jwtUserDetails) {
//
//        Long userId = jwtUserDetails.userId();
//
//        return ResponseEntity.ok(couponUserService.getStateUserCoupons(userId, CouponUser.UserCouponStatus.ACTIVE, pageable));
//    }
//
//    @GetMapping("/used")
//    public ResponseEntity<Page<CouponBoxResponse>> getUsedCouponBox(Pageable pageable,
//                                                                @CurrentUser JwtUserDetails jwtUserDetails) {
//
//        Long userId = jwtUserDetails.userId();
//
//        return ResponseEntity.ok(couponUserService.getStateUserCoupons(userId, CouponUser.UserCouponStatus.USED, pageable));
//    }
//
//    @GetMapping("/expired")
//    public ResponseEntity<Page<CouponBoxResponse>> getExpiredUsedCouponBox(Pageable pageable,
//                                                                    @CurrentUser JwtUserDetails jwtUserDetails) {
//
//        Long userId = jwtUserDetails.userId();
//
//        return ResponseEntity.ok(couponUserService.getStateUserCoupons(userId, CouponUser.UserCouponStatus.EXPIRED, pageable));
//    }
}
