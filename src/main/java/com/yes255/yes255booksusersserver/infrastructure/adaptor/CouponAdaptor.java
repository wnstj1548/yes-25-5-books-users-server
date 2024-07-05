package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.ExpiredCouponUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "couponAdaptor", url = "${api.coupons}/coupons")
public interface CouponAdaptor {

    @PostMapping("/expired")
    ExpiredCouponUserResponse getCouponExpiredDate(@RequestParam("couponId") Long couponId);

    // 쿠폰 서버로부터 쿠폰함에 필요한 정보 획득
    @GetMapping("/info")
    List<CouponInfoResponse> getCouponsInfo(@RequestParam List<Long> couponIdList);

    @PostMapping("/issueWelcomeCoupon")
    void issueWelcomeCoupon(@RequestParam("userId") Long userId);

    @PostMapping("/issueBirthdayCoupon")
    void issueBirthdayCoupon(@RequestParam("userId") Long userId);

    @PostMapping("/expiredWithUserId")
    ExpiredCouponUserResponse getCouponExpiredDateWithUserId(@RequestParam("couponId") Long couponId, @RequestParam("userId") Long userId);

}
