package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    final public CouponUserService couponUserService;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpired() {
        // 만료 상태로 갱신
        couponUserService.checkExpiredCoupon();

        // 만료 쿠폰 삭제
        couponUserService.deleteExpiredCoupons();
    }

//    // 매일 1시 실행
//    @Scheduled(cron = "0 0 1 * * ?")
//    public void deleteExpiredCoupons() {
//        couponUserService.deleteExpiredCoupons();
//    }
}
