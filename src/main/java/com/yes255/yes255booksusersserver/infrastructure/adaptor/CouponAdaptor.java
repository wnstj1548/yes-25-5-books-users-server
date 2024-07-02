package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ExpiredCouponUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "couponAdaptor", url = "${api.coupons}/coupons")
public interface CouponAdaptor {

    @PostMapping("/expired")
    ExpiredCouponUserResponse getCouponExpiredDate(@RequestParam("couponId") Long couponId);
}
