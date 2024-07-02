package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ExpiredCouponUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "couponAdaptor", url = "${api.coupons}/coupons")
public interface CouponAdaptor {

    @GetMapping("/expired")
    ExpiredCouponUserResponse getCouponExpiredDate(Long couponId);
}
