package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CouponUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    private LocalDate userCouponUsedAt;

    @NotNull(message = "쿠폰 상태는 필수입니다.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus userCouponStatus;

    // 일반, 생일, 웰컴
    @NotNull(message = "쿠폰 타입은 필수입니다.")
    @Column(nullable = false)
    private String userCouponType;

    @NotNull(message = "쿠폰 만료 일자는 필수입니다.")
    @Column(nullable = false)
    private LocalDate couponExpiredAt;

    @NotNull(message = "쿠폰 아이디은 필수입니다.")
    @Column(nullable = false)
    private Long couponId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum UserCouponStatus {
        ACTIVE, USED, EXPIRED
    }

    @Builder
    public CouponUser(Long userCouponId, LocalDate userCouponUsedAt, UserCouponStatus userCouponStatus, String userCouponType, LocalDate couponExpiredAt, Long couponId, User user) {
        this.userCouponId = userCouponId;
        this.userCouponUsedAt = userCouponUsedAt;
        this.userCouponStatus = userCouponStatus;
        this.userCouponType = userCouponType;
        this.couponExpiredAt = couponExpiredAt;
        this.couponId = couponId;
        this.user = user;
    }

    public void updateUserCouponStatus(UserCouponStatus userCouponStatus) {
        this.userCouponStatus = userCouponStatus;
    }

    public void updateCouponUsedAt(LocalDate userCouponUsedAt) {
        this.userCouponUsedAt = userCouponUsedAt;
    }
}
