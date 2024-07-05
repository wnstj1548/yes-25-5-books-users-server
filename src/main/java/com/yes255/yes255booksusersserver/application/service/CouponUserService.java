package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.presentation.dto.response.couponuser.CouponBoxResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponUserService {

    void createCouponUser(Long couponId, Long userId);

    Page<CouponBoxResponse> getUserCoupons(Long userId, Pageable pageable);

    Page<CouponBoxResponse> getStateUserCoupons(Long userId, CouponUser.UserCouponStatus userCouponStatus, Pageable pageable);

    void checkExpiredCoupon();

    void deleteExpiredCoupons();
}
