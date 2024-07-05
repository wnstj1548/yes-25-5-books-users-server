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
}
