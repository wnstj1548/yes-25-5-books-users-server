package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "couponAdaptor", url = "http://133.186.153.195:8081")
public interface CouponAdaptor {

    @PostMapping("/coupons/welcome/{userId}")
    void issueWelcomeCoupon(@PathVariable Long userId);
}
