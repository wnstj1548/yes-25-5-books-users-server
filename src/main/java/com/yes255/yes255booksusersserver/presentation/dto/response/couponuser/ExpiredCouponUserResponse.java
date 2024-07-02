package com.yes255.yes255booksusersserver.presentation.dto.response.couponuser;

import lombok.Builder;

import java.util.Date;

@Builder
public record ExpiredCouponUserResponse(Date couponExpiredAt) {
}
