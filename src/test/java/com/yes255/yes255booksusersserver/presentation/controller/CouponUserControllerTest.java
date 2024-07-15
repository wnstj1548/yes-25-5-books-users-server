package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.couponuser.UpdateCouponRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ReadMaximumDiscountCouponResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ReadUserCouponResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponUserControllerTest {

    @InjectMocks
    private CouponUserController couponUserController;

    @Mock
    private CouponUserService couponUserService;

    private JwtUserDetails jwtUserDetails;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");
    }

    @Test
    @DisplayName("회원 쿠폰 생성 - 성공")
    void claimCoupon() {
        long couponId = 1L;

        couponUserController.claimCoupon(couponId, jwtUserDetails);

        verify(couponUserService).createCouponUser(couponId, jwtUserDetails.userId());
    }

    @Test
    @DisplayName("활성화된 회원 쿠폰함 조회 - 성공")
    void getActiveCouponBox() {
        String couponState = "ACTIVE";
        when(couponUserService.getStateUserCoupons(any(), any(), any())).thenReturn(null);

        couponUserController.getActiveCouponBox(couponState, null, jwtUserDetails);

        verify(couponUserService).getStateUserCoupons(jwtUserDetails.userId(), CouponUser.UserCouponStatus.ACTIVE, null);
    }

    @Test
    @DisplayName("회원 쿠폰 상태 업데이트 - 성공")
    void updateCouponState() {
        UpdateCouponRequest couponRequest = UpdateCouponRequest.builder()
                .couponId(1L)
                .operationType("USE")
                .build();

        couponUserController.updateCouponState(couponRequest, jwtUserDetails);

        verify(couponUserService).updateCouponState(jwtUserDetails.userId(), couponRequest);
    }

    @Test
    @DisplayName("모든 회원 쿠폰 조회 - 성공")
    void getAllUserCoupons() {
        List<ReadUserCouponResponse> coupons = Collections.emptyList();
        when(couponUserService.getAllUserCouponsByUserId(any())).thenReturn(coupons);

        couponUserController.getAllUserCoupons(jwtUserDetails);

        verify(couponUserService).getAllUserCouponsByUserId(jwtUserDetails.userId());
    }

    @Test
    @DisplayName("최대 할인 금액 쿠폰 조회 - 성공")
    void getMaxDiscountCouponByTotalAmount() {
        int totalAmount = 10000;
        ReadMaximumDiscountCouponResponse response = ReadMaximumDiscountCouponResponse.builder()
                .couponId(1L)
                .discountAmount(BigDecimal.valueOf(500))
                .couponName("Discount Coupon")
                .build();

        when(couponUserService.getMaximumDiscountCouponByUserId(any(), any())).thenReturn(response);

        couponUserController.getMaxDiscountCouponByTotalAmount(totalAmount, jwtUserDetails);

        verify(couponUserService).getMaximumDiscountCouponByUserId(jwtUserDetails.userId(), totalAmount);
    }
}
