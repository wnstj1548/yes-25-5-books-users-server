package com.yes255.yes255booksusersserver.presentation.dto.response.couponuser;

import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Builder
public record CouponBoxResponse(Long userCouponId, LocalDate userCouponUsedAt, CouponUser.UserCouponStatus userCouponStatus,
                                String userCouponType, Date CouponExpiredAt, Long couponId, Long userId,

                                String couponName, BigDecimal couponMinAmount, BigDecimal couponMaxAmount,
                                BigDecimal couponDiscountAmount, BigDecimal couponDiscountRate, Date couponCreatedAt,
                                String couponCode, Boolean couponDiscountType) {

    public static CouponBoxResponse fromCouponInfo(CouponUser couponUser, CouponInfoResponse couponInfoResponse) {
        return CouponBoxResponse.builder()
                .userCouponId(couponUser.getUserCouponId())
                .userCouponUsedAt(couponUser.getUserCouponUsedAt())
                .userCouponStatus(couponUser.getUserCouponStatus())
                .userCouponType(couponUser.getUserCouponType())
                .CouponExpiredAt(couponUser.getCouponExpiredAt())
                .couponId(couponUser.getCouponId())
                .userId(couponUser.getUser().getUserId())
                .couponName(couponInfoResponse.couponName())
                .couponMinAmount(couponInfoResponse.couponMinAmount())
                .couponMaxAmount(couponInfoResponse.couponMaxAmount())
                .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                .couponCreatedAt(couponInfoResponse.couponCreatedAt())
                .couponCode(couponInfoResponse.couponCode())
                .build();
    }

    public static CouponBoxResponse fromCouponUser(CouponUser couponUser, CouponInfoResponse couponInfoResponse) {
        return CouponBoxResponse.builder()
                .couponId(couponUser.getCouponId())
                .CouponExpiredAt(couponUser.getCouponExpiredAt())
                .couponName(couponInfoResponse.couponName())
                .couponMinAmount(couponInfoResponse.couponMinAmount())
                .couponMaxAmount(couponInfoResponse.couponMaxAmount())
                .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                .couponDiscountType(couponInfoResponse.couponDiscountType())
                .build();
    }
}
