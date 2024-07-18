package com.yes255.yes255booksusersserver.presentation.dto.response.couponuser;

import java.util.List;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record CouponInfoResponse(Long couponId,
                                 String couponName,             // 쿠폰명
                                 BigDecimal couponMinAmount,    // 최소주문금액
                                 BigDecimal couponMaxAmount,    // 최대할인금액
                                 BigDecimal couponDiscountAmount,     // 할인값
                                 BigDecimal couponDiscountRate,    // 할인률
                                 Date couponCreatedAt,          // 생성일자
                                 String couponCode,             // 쿠폰 코드
                                 Boolean couponDiscountType,
                                 Long bookId,
                                 List<Long> categoryIds,
                                 Boolean applyCouponToAllBooks) {
}
