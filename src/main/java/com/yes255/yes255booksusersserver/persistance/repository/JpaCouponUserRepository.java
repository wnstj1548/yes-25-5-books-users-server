package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.CouponUser;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JpaCouponUserRepository extends JpaRepository<CouponUser, Long> {

    Boolean existsByCouponIdAndUser(Long couponId, User user);

    List<CouponUser> findByUserUserId(Long userId);

    List<CouponUser> findByUserUserIdAndUserCouponStatus(Long userId, CouponUser.UserCouponStatus userCouponStatus);

    Page<CouponUser> findByUserUserIdAndUserCouponStatus(Long userId, CouponUser.UserCouponStatus userCouponStatus, Pageable pageable);

    List<CouponUser> findByUserCouponStatus(CouponUser.UserCouponStatus status);

    List<CouponUser> findByUserCouponStatusAndCouponExpiredAtBefore(CouponUser.UserCouponStatus userCouponStatus, Date date);

    Optional<CouponUser> findByUserCouponIdAndUserUserId(Long userCouponId, Long userId);
}
