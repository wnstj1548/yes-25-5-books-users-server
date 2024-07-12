package com.yes255.yes255booksusersserver.presentation.dto.response.couponuser;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReadMaximumDiscountCouponResponse(Long couponId, BigDecimal discountAmount, String couponName) {
}
