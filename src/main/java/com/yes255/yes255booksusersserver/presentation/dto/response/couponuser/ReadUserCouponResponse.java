package com.yes255.yes255booksusersserver.presentation.dto.response.couponuser;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record ReadUserCouponResponse(Long userCouponId, Date CouponExpiredAt, Long couponId, String couponName,
                                     BigDecimal couponMinAmount, BigDecimal couponDiscountAmount,
                                     BigDecimal couponDiscountRate) {
}
