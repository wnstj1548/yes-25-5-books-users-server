package com.yes255.yes255booksusersserver.presentation.dto.response.couponuser;

import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import java.util.List;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Builder
public record ReadUserCouponResponse(Long userCouponId, Date CouponExpiredAt, Long couponId, String couponName,
                                     BigDecimal couponMinAmount, BigDecimal couponDiscountAmount,
                                     BigDecimal couponDiscountRate,
                                     Long bookId,
                                     List<Long> categoryIds,
                                     Boolean applyCouponToAllBooks) {

    public static ReadUserCouponResponse fromCouponUser(CouponUser couponUser, CouponInfoResponse couponInfoResponse) {
        return ReadUserCouponResponse.builder()
                .userCouponId(couponUser.getUserCouponId())
                .CouponExpiredAt(couponUser.getCouponExpiredAt())
                .couponId(couponUser.getCouponId())
                .couponName(couponInfoResponse.couponName())
                .couponMinAmount(couponInfoResponse.couponMinAmount())
                .couponDiscountAmount(couponInfoResponse.couponDiscountAmount())
                .couponDiscountRate(couponInfoResponse.couponDiscountRate())
                .bookId(couponInfoResponse.bookId())
                .categoryIds(couponInfoResponse.categoryIds())
                .applyCouponToAllBooks(couponInfoResponse.applyCouponToAllBooks())
                .build();
    }
}
