package com.yes255.yes255booksusersserver.presentation.dto.request.couponuser;

import lombok.Builder;

@Builder
public record ReadMaximumDiscountCouponRequest(Integer totalAmount) {
}
